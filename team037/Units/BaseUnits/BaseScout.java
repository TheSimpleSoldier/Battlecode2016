package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.FlyingNavigator;
import team037.Messages.*;
import team037.ScoutMapKnowledge;
import team037.Unit;
import team037.Utilites.CommunicationUtilities;
import team037.Utilites.PartsUtilities;

public class BaseScout extends Unit
{
    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();

    public BaseScout(RobotController rc)
    {
        super(rc);
        mapKnowledge = mKnowledge;
        navigator = new FlyingNavigator(rc);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.avoidEnemiesInRoute(enemies, navigator.getTarget());
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.avoidEnemiesInRoute(zombies, navigator.getTarget());
    }

    public boolean precondition()
    {
        return !rc.isCoreReady();
    }

    @Override
    public void sendMessages() throws GameActionException
    {
        if(mKnowledge.updated && msgsSent < 20)
        {
            Communication com = mKnowledge.getMapBoundsCommunication();
            communicator.sendCommunication(mKnowledge.getRange(), com);
            msgsSent++;
            mKnowledge.updated = false;
        }
        msgArchons();
        msgTurrets();
        msgParts();
        msgDens();
        msgRubble();
    }

    private void msgArchons() throws GameActionException
    {
        for(int k = 0; k < enemies.length; k++)
        {
            if(enemies[k].type == RobotType.ARCHON && msgsSent < 20)
            {
                BotInfoCommunication communication = new BotInfoCommunication();
                communication.opcode = CommunicationType.ENEMY;
                communication.id = enemies[k].ID;
                communication.team = opponent;
                communication.type = RobotType.ARCHON;
                communication.x = enemies[k].location.x;
                communication.y = enemies[k].location.y;
                communicator.sendCommunication(mKnowledge.getRange(), communication);
                msgsSent++;
            }
        }
    }

    private void msgDens() throws GameActionException
    {
        for (int i = zombies.length; --i>=0; )
        {
            if (zombies[i].type == RobotType.ZOMBIEDEN)
            {
                MapLocation zombieDen = zombies[i].location;
                if (!mKnowledge.dens.contains(zombieDen) && msgsSent < 20)
                {
                    mKnowledge.dens.add(zombieDen);
                    Communication communication = new SimpleBotInfoCommunication();
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.SDEN), zombies[i].ID, zombieDen.x, zombieDen.y});
                    communicator.sendCommunication(mKnowledge.getRange(), communication);
                    msgsSent++;
                }
            }
        }

        if (rc.getRoundNum() % 5 == 1)
        {
            MapLocation deadDen = mKnowledge.updateDens(rc);

            // if a den has been destroyed send it out
            if (deadDen != null && msgsSent < 20)
            {
                Communication communication = new SimpleBotInfoCommunication();
                communication.opcode = CommunicationType.DEAD_DEN;
                communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.DEAD_DEN), 0, deadDen.x, deadDen.y});
                communicator.sendCommunication(mKnowledge.getRange(), communication);
                msgsSent++;
            }
        }
    }

    private void msgParts() throws GameActionException
    {
        MapLocation[] partsArray = PartsUtilities.findPartsAndNeutrals(rc);

        for (int i = partsArray.length; --i>=0; )
        {
            MapLocation spot = partsArray[i];
            if (spot != null && !mKnowledge.partListed(spot))
            {
                mKnowledge.partsAndNeutrals.add(spot);

                RobotInfo bot = null;

                if (rc.canSenseLocation(spot))
                {
                    bot = rc.senseRobotAtLocation(spot);
                }

                if (rc.senseParts(spot) > 0 && msgsSent < 20)
                {
                    // create parts msg
                    Communication communication = new PartsCommunication();
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.PARTS), (int) rc.senseParts(spot), spot.x, spot.y});
                    communicator.sendCommunication(mKnowledge.getRange(), communication);
                    msgsSent++;
                }
                else if (bot != null && bot.team == Team.NEUTRAL && msgsSent < 20)
                {
                    // create neutral bot msg
                    Communication communication = new PartsCommunication();
                    int parts = bot.type.partCost;
                    if (bot.type == RobotType.ARCHON)
                    {
                        parts = 1000;
                    }
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.NEUTRAL), parts, spot.x, spot.y});
                    communicator.sendCommunication(mKnowledge.getRange(), communication);
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
                mKnowledge.addAlliedTurretLocation(allies[i].location);
            }
        }

        MapLocation[] allyTurrets = mKnowledge.getAlliedTurretLocations();
        boolean sentMsg = false;

        if (allyTurrets != null && mKnowledge.ourTurretLocations.hasLocations())
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

    private void msgRubble() {

        if (msgsSent > 19 || rc.getRoundNum() % 5 != id % 5) {
            return;
        }

        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), RobotType.SCOUT.sensorRadiusSquared);

        double rubbleThresh = GameConstants.RUBBLE_OBSTRUCTION_THRESH;

        long broadcast = 0L;

        try {
            MapLocation checkLoc = locations[41];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000000000000000000000000L;
            }
            checkLoc = locations[42];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000000000000000000000000L;
            }
            checkLoc = locations[43];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000000000000000000000000L;
            }
            checkLoc = locations[44];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000000000000000000000L;
            }
            checkLoc = locations[45];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000000000000000000000L;
            }
            checkLoc = locations[46];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000000000000000000000L;
            }
            checkLoc = locations[47];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000000000000000000L;
            }

            // x++

            checkLoc = locations[55];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000000000000000000L;
            }
            checkLoc = locations[56];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000000000000000000L;
            }
            checkLoc = locations[57];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000000000000000L;
            }
            checkLoc = locations[58];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000000000000000L;
            }
            checkLoc = locations[59];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000000000000000L;
            }
            checkLoc = locations[60];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000000000000L;
            }
            checkLoc = locations[61];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000000000000L;
            }

            // x++

            checkLoc = locations[70];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000000000000L;
            }
            checkLoc = locations[71];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000000000L;
            }
            checkLoc = locations[72];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000000000L;
            }
            checkLoc = locations[73];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000000000L;
            }
            checkLoc = locations[74];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000000L;
            }
            checkLoc = locations[75];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000000L;
            }
            checkLoc = locations[76];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000000L;
            }

            // x++

            checkLoc = locations[85];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000000L;
            }
            checkLoc = locations[86];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000000L;
            }
            checkLoc = locations[87];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000000L;
            }
            checkLoc = locations[88];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000000L;
            }
            checkLoc = locations[89];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000000L;
            }
            checkLoc = locations[90];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000000L;
            }
            checkLoc = locations[91];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000000L;
            }

            // x++

            checkLoc = locations[100];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000000L;
            }
            checkLoc = locations[101];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000000L;
            }
            checkLoc = locations[102];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000000L;
            }
            checkLoc = locations[103];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000000L;
            }
            checkLoc = locations[104];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000000L;
            }
            checkLoc = locations[105];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000000L;
            }
            checkLoc = locations[106];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000000L;
            }

            // x++

            checkLoc = locations[115];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000000L;
            }
            checkLoc = locations[116];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000000L;
            }
            checkLoc = locations[117];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000000L;
            }
            checkLoc = locations[118];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000000L;
            }
            checkLoc = locations[119];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000000L;
            }
            checkLoc = locations[120];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000000L;
            }
            checkLoc = locations[121];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000000L;
            }

            // x++

            checkLoc = locations[129];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000000L;
            }
            checkLoc = locations[130];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100000L;
            }
            checkLoc = locations[131];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10000L;
            }
            checkLoc = locations[132];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1000L;
            }
            checkLoc = locations[133];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b100L;
            }
            checkLoc = locations[134];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b10L;
            }
            checkLoc = locations[135];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                broadcast += 0b1L;
            }

            int hi = (int) (broadcast >>> 25);       // Upper 24 bits
            int lo = (int) (broadcast & 0x1FFFFFF);  // Lower 25 bits

            Communication communication = new RubbleCommunication();
            communication.opcode = CommunicationType.RUBBLE;
            communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.RUBBLE),hi,lo});
            communicator.sendCommunication(mKnowledge.getRange(), communication);
            msgsSent++;
        } catch (GameActionException e) {

        }
    }
}
