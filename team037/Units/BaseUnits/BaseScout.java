package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.FlyingNavigator;
import team037.MapKnowledge;
import team037.Messages.*;
import team037.ScoutMapKnowledge;
import team037.Unit;
import team037.Utilites.PartsUtilities;
import team037.Utilites.RubbleUtilities;

/**
 * Base extension of the Unit class for units of RobotType Scout.
 */
public class BaseScout extends Unit
{
    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();

    public BaseScout(RobotController rc)
    {
        super(rc);
        mapKnowledge = mKnowledge;
        navigator = new FlyingNavigator(rc);
    }

    /**
     * Scout-specific implementations of abstract methods required by extension of class Unit.
     */
    @Override // takeNextStep() in class Unit by contract of extension
    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }
    @Override // fight() in class Unit by contract of extension
    public boolean fight() throws GameActionException
    {
        return fightMicro.avoidEnemiesInRoute(enemies, navigator.getTarget());
    }
    @Override // fightZombies() in class Unit by contract of extension
    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.avoidEnemiesInRoute(zombies, navigator.getTarget());
    }
    @Override // precondition() in class Unit by contract of extension
    public boolean precondition()
    {
        return !rc.isCoreReady();
    }

    /**
     * Send messages to units regarding the following information:
     *  1) Discovery of a boundary of this map
     *  2) Changes to known bounds of the map
     *  3) Locations of archon sightings
     *  4) Targets for turrets
     *  5) Locations of parts and neutral units sighted
     *  6) Locations of dens sighted or missing (destroyed)
     *  7) Rubble topography in a 7x7 grid surrounding this scout
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    @Override // sendMessages() in class Unit
    public void sendMessages() throws GameActionException
    {
        if(mKnowledge.firstFoundEdge && msgsSent < 20)
        {
            Communication com = mKnowledge.getMapBoundsCommunication();
            communicator.sendCommunication(ScoutMapKnowledge.getMaxRange(), com);
            msgsSent++;
            mKnowledge.firstFoundEdge = false;
            mKnowledge.updated = false;
        }
        if(mKnowledge.updated && msgsSent < 20)
        {
            Communication com = mKnowledge.getMapBoundsCommunication();
            communicator.sendCommunication(ScoutMapKnowledge.getRange(), com);
            msgsSent++;
            mKnowledge.updated = false;
        }
        msgArchons();
        msgTurrets();
        msgParts();
        msgDens();
        msgRubble();
    }

    /**
     * Send messages to units regarding locations of archon sightings.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void msgArchons() throws GameActionException
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
                communicator.sendCommunication(2500, communication);
                msgsSent++;
            }
        }
    }
    /**
     * Send messages to units regarding the locations of dens sighted or missing (destroyed).
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void msgDens() throws GameActionException
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
                    communicator.sendCommunication(MapKnowledge.getMaxRange(), communication);
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
                communicator.sendCommunication(MapKnowledge.getRange(), communication);
                msgsSent++;
            }
        }
    }

    /**
     * Send messages to units regarding the locations of parts and neutral units sighted.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void msgParts() throws GameActionException
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

                int partVal = (int)rc.senseParts(spot);

                if (partVal > 0 && msgsSent < 20)
                {
                    double rubble = rc.senseRubble(spot);

                    if (rubble > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
                    {
                        int turns = RubbleUtilities.calculateClearActionsToPassableButSlow(rubble);
                        if (turns > 0)
                        {
                            partVal /= turns;
                            if (partVal <= 0) partVal = 1;
                        }
                    }

                    // create parts msg
                    Communication communication = new PartsCommunication();
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.PARTS), partVal, spot.x, spot.y});
                    communicator.sendCommunication(MapKnowledge.getRange(), communication);
                    msgsSent++;
                }
                else if (bot != null && bot.team == Team.NEUTRAL && msgsSent < 20)
                {
                    // create neutral bot msg
                    Communication communication = new PartsCommunication();
                    int parts = bot.type.partCost;
                    if (bot.type == RobotType.ARCHON)
                    {
                        parts = Integer.MAX_VALUE;
                    }
                    communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.NEUTRAL), parts, spot.x, spot.y});
                    communicator.sendCommunication(MapKnowledge.getRange(), communication);
                    msgsSent++;
                }
            }
        }
    }

    /**
     * Send messages to units regarding targets for turrets.
     * @throws GameActionException - ensure actions taken are valid under the Battlecode game engine.
     */
    public void msgTurrets() throws GameActionException
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
                boolean msg = false;
                for (int j = mKnowledge.ourTurretLocations.length; --j >= 0; )
                {
                    if (allyTurrets[j] == null)
                        continue;

                    double dist = allyTurrets[j].distanceSquaredTo(enemy);

                    if (dist <= RobotType.TURRET.attackRadiusSquared && dist > RobotType.TURRET.sensorRadiusSquared && msgsSent < 10)
                    {
                        msg = true;
                        break;
                    }

                    if (msgsSent == 10)
                    {
                        i = -1;
                        j = -1;
                    }
                }

                if (msg)
                {
                    Communication communication = new TurretSupportCommunication();
                    communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.TURRET_SUPPORT),(int)Math.ceil(enemies[i].coreDelay),enemy.x, enemy.y});
                    communicator.sendCommunication(type.sensorRadiusSquared*2, communication);
                    sentMsg = true;
                    msgsSent++;
                }
            }

            if (!sentMsg)
            {
                for (int i = zombies.length; --i >= 0; )
                {
                    MapLocation enemy = zombies[i].location;
                    boolean msg = false;

                    for (int j = mKnowledge.ourTurretLocations.length; --j >= 0; )
                    {
                        if (allyTurrets[j] == null)
                            continue;

                        if (allyTurrets[j].distanceSquaredTo(enemy) <= RobotType.TURRET.attackRadiusSquared && msgsSent < 10)
                        {
                            msg = true;
                            break;
                        }

                        if (msgsSent == 10)
                        {
                            i = -1;
                            j = -1;
                        }
                    }

                    if (msg)
                    {
                        TurretSupportCommunication communication = new TurretSupportCommunication();
                        communication.opcode = CommunicationType.TURRET_SUPPORT;
                        communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.TURRET_SUPPORT),(int)Math.ceil(zombies[i].coreDelay),enemy.x, enemy.y});
                        communicator.sendCommunication(type.sensorRadiusSquared*2, communication);
                        msgsSent++;
                    }
                }
            }
        }
    }

    /**
     * Send messages to units regarding the rubble topography in a 7x7 grid surrounding this scout.
     */
    public void msgRubble() {

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
