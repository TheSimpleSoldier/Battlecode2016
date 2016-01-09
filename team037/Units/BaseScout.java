package team037.Units;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.FlyingNavigator;
import team037.MapKnowledge;
import team037.Messages.Communication;
import team037.Messages.TurretSupportCommunication;
import team037.Unit;

public class BaseScout extends Unit
{
    MapKnowledge mapKnowledge = new MapKnowledge();
    FlyingNavigator move;

    public BaseScout(RobotController rc)
    {
        super(rc);
        move = new FlyingNavigator(rc);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return move.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        msgTurrets();
    }

    private void msgTurrets() throws GameActionException
    {
        rc.setIndicatorString(1, "");
        for (int i = allies.length; --i >= 0; )
        {
            if (allies[i].type == RobotType.TURRET)
            {
                rc.setIndicatorString(0, "We found a turret");
                mapKnowledge.ourTurretLocations.add(allies[i].location);
            }
        }

        MapLocation[] allyTurrets = mapKnowledge.getAlliedTurretLocations();
        boolean sentMsg =false;

        if (allyTurrets != null && allyTurrets.length > 0)
        {
            for (int i = enemies.length; --i >= 0; )
            {
                MapLocation enemy = enemies[i].location;
                for (int j = allyTurrets.length; --j >= 0; )
                {
                    if (allyTurrets[j] == null)
                        continue;

                    double dist = allyTurrets[j].distanceSquaredTo(enemy);
                    if (dist <= RobotType.TURRET.attackRadiusSquared && dist > RobotType.TURRET.sensorRadiusSquared)
                    {
                        rc.setIndicatorString(1, "Helping Turret");
                        Communication communication = new TurretSupportCommunication();
                        communication.setValues(new int[]{0,0,enemy.x, enemy.y});
                        communicator.sendCommunication(rc.getLocation().distanceSquaredTo(allyTurrets[j]), communication);
                        sentMsg = true;
                    }
                }
            }

            if (!sentMsg)
            {
                for (int i = zombies.length; --i >= 0; )
                {
                    MapLocation enemy = zombies[i].location;
//                    if (enemy == null)
//                        continue;

                    for (int j = allyTurrets.length; --j >= 0; )
                    {
                        if (allyTurrets[j] == null)
                            continue;

                        if (allyTurrets[j].distanceSquaredTo(enemy) <= RobotType.TURRET.attackRadiusSquared)
                        {
                            rc.setIndicatorString(1, "Helping Turret");
                            TurretSupportCommunication communication = new TurretSupportCommunication();
                            communication.opcode = CommunicationType.TURRET_SUPPORT;
                            communication.setValues(new int[]{0,0,enemy.x, enemy.y});
                            communicator.sendCommunication(400, communication);
                            //communicator.sendCommunication(rc.getLocation().distanceSquaredTo(allyTurrets[j]) + 1, communication);
                        }
                    }
                }
            }
        }
    }
}
