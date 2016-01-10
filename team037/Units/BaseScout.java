package team037.Units;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.FlyingNavigator;
import team037.Messages.Communication;
import team037.Messages.PartsCommunication;
import team037.Messages.SimpleBotInfoCommunication;
import team037.Messages.TurretSupportCommunication;
import team037.Unit;
import team037.Utilites.PartsUtilities;
import team037.DataStructures.*;

public class BaseScout extends Unit
{
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

        msgParts();

        msgDens();
    }

    private void msgDens() throws GameActionException
    {
        int dist = -1;
        for (int i = zombies.length; --i>=0; )
        {
            if (zombies[i].type == RobotType.ZOMBIEDEN)
            {
                MapLocation zombieDen = zombies[i].location;
                if (!mapKnowledge.denLocations.contains(zombieDen) && msgsSent < 20)
                {
                    if (dist == -1)
                        dist = Math.max(type.sensorRadiusSquared * 2, Math.min(400, rc.getLocation().distanceSquaredTo(start)));

                    mapKnowledge.addDenLocation(zombieDen);
                    Communication communication = new SimpleBotInfoCommunication();
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.SDEN), zombies[i].ID, zombieDen.x, zombieDen.y});
                    communicator.sendCommunication(dist, communication);
                    msgsSent++;
                }
            }
        }

        if (rc.getRoundNum() % 5 == 1)
        {
            MapLocation deadDen = mapKnowledge.updateDens(rc);

            if (dist == -1)
                dist = Math.max(type.sensorRadiusSquared * 2, Math.min(400, rc.getLocation().distanceSquaredTo(start)));

            // if a den has been destroyed send it out
            if (deadDen != null && msgsSent < 20)
            {
                Communication communication = new SimpleBotInfoCommunication();
                communication.opcode = CommunicationType.DEAD_DEN;
                communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.DEAD_DEN), 0, deadDen.x, deadDen.y});
                communicator.sendCommunication(dist, communication);
                msgsSent++;
            }
        }
    }

    private void msgParts() throws GameActionException
    {
        AppendOnlyMapLocationArray parts = PartsUtilities.findPartsAndNeutralsICanSense(rc);
        MapLocation[] partsArray = parts.array;

        for (int i = partsArray.length; --i>=0; )
        {
            MapLocation spot = partsArray[i];
            if (spot != null && !mapKnowledge.partListed(spot))
            {
                RobotInfo bot = null;

                if (rc.canSenseLocation(spot))
                {
                    bot = rc.senseRobotAtLocation(spot);
                }

                int dist = Math.max(type.sensorRadiusSquared * 2, Math.min(400, rc.getLocation().distanceSquaredTo(start)));
                if (rc.senseParts(spot) > 0 && msgsSent < 20)
                {
                    // create parts msg
                    Communication communication = new PartsCommunication();
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.PARTS), (int) rc.senseParts(spot), spot.x, spot.y});
                    communicator.sendCommunication(dist, communication);
                    msgsSent++;
                }
                else if (bot != null && bot.team == Team.NEUTRAL && msgsSent < 20)
                {
                    // create neutral bot msg
                    Communication communication = new PartsCommunication();
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.NEUTRAL), bot.type.partCost, spot.x, spot.y});
                    communicator.sendCommunication(dist, communication);
                    msgsSent++;
                }
            }
        }
    }

    private void msgTurrets() throws GameActionException
    {
        for (int i = allies.length; --i >= 0; )
        {
            if (allies[i].type == RobotType.TURRET)
            {
                mapKnowledge.addAlliedTurretLocation(allies[i].location);
            }
        }

        MapLocation[] allyTurrets = mapKnowledge.getAlliedTurretLocations();
        boolean sentMsg = false;

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
                    if (dist <= RobotType.TURRET.attackRadiusSquared && dist > RobotType.TURRET.sensorRadiusSquared && msgsSent < 20)
                    {
                        Communication communication = new TurretSupportCommunication();
                        communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.TURRET_SUPPORT),(int)Math.ceil(enemies[i].coreDelay),enemy.x, enemy.y});
                        communicator.sendCommunication(rc.getLocation().distanceSquaredTo(allyTurrets[j]) + 1, communication);
                        sentMsg = true;
                        msgsSent++;
                    }
                }
            }

            if (!sentMsg)
            {
                for (int i = zombies.length; --i >= 0; )
                {
                    MapLocation enemy = zombies[i].location;

                    for (int j = allyTurrets.length; --j >= 0; )
                    {
                        if (allyTurrets[j] == null)
                            continue;

                        if (allyTurrets[j].distanceSquaredTo(enemy) <= RobotType.TURRET.attackRadiusSquared && msgsSent < 20)
                        {
                            TurretSupportCommunication communication = new TurretSupportCommunication();
                            communication.opcode = CommunicationType.TURRET_SUPPORT;
                            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.TURRET_SUPPORT),(int)Math.ceil(zombies[i].coreDelay),enemy.x, enemy.y});
                            communicator.sendCommunication(rc.getLocation().distanceSquaredTo(allyTurrets[j]) + 1, communication);
                            msgsSent++;
                        }
                    }
                }
            }
        }
    }
}
