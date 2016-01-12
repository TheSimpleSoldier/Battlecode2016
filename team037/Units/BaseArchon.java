package team037.Units;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.SortedParts;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.*;
import team037.Unit;
import team037.Utilites.BuildOrderCreation;


public class BaseArchon extends Unit
{
    private BuildOrder buildOrder;
    Bots nextBot;
    RobotType nextType;
    RobotInfo[] neutralBots;
    public static SortedParts sortedParts = new SortedParts();
    private int rushingUnits = 0;
    private boolean sentRushSignal = false;

    public BaseArchon(RobotController rc)
    {
        super(rc);
        buildOrder = BuildOrderCreation.createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Bots.typeFromBot(nextBot);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public void collectData() throws GameActionException
    {
        super.collectData();
        neutralBots = rc.senseNearbyRobots(2, Team.NEUTRAL);

        // don't need to check every round
        if (rc.getRoundNum() % 5 == 0)
        {
            sortedParts.findPartsAndNeutralsICanSense(rc);
        }
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    // additional methods with default behavior
    public void handleMessages() throws GameActionException
    {
        super.handleMessages();

        int offennsiveEnemies = 0;

        for (int i = enemies.length; --i>=0;)
        {
            switch (enemies[i].type)
            {
                case TURRET:
                case GUARD:
                case SOLDIER:
                case VIPER:
                    offennsiveEnemies++;
            }
        }

        offennsiveEnemies += zombies.length;

        if (offennsiveEnemies > 0)
        {
            Communication distressCall = new BotInfoCommunication();
            distressCall.setValues(new int[]{CommunicationType.toInt(CommunicationType.ARCHON_DISTRESS), 0, 0, id, currentLocation.x, currentLocation.y});
            communicator.sendCommunication(400, distressCall);
        }
    }

    public boolean healNearbyAllies() throws GameActionException {
        // precondition
        if (nearByAllies.length == 0 || !repaired) {
            return false;
        }

        double weakestHealth = 9999;
        RobotInfo weakest = null;

        for (int i = nearByAllies.length; --i>=0; )
        {
            double health = nearByAllies[i].health;
            if (nearByAllies[i].type != RobotType.ARCHON && health < nearByAllies[i].maxHealth && currentLocation.distanceSquaredTo(nearByAllies[i].location) <= RobotType.ARCHON.attackRadiusSquared)
            {
                if (health < weakestHealth)
                {
                    weakestHealth = health;
                    weakest = nearByAllies[i];
                }
            }
        }

        if (weakest != null)
        {
            rc.repair(weakest.location);
            repaired = true;
            return true;
        }
        return false;
    }

    // maybe spawn a unit or repair a damaged unit
    public boolean carryOutAbility() throws GameActionException
    {
        // heal doesn't effect core cooldown
        healNearbyAllies();

        rc.setIndicatorString(1, "Map Bounds minX: " + mapKnowledge.minX + " minY: " + mapKnowledge.minY + " maxX: " + mapKnowledge.maxX + " maxY: " + mapKnowledge.maxY);

        if (neutralBots.length > 0 && rc.isCoreReady())
        {
            rc.activate(neutralBots[0].location);
        }

        if(rc.hasBuildRequirements(nextType) && rc.isCoreReady())
        {
            for (int i = dirs.length; --i>=0; )
            {
                if (rc.canBuild(dirs[i], nextType))
                {
                    rc.build(dirs[i], nextType);

                    if (nextBot == Bots.RUSHINGSOLDIER || nextBot == Bots.RUSHINGVIPER)
                    {
                        Communication rushMsg = new AttackCommunication();

                        MapLocation archonCOM = mapKnowledge.getArchonCOM();

                        rushMsg.setValues(new int[] {CommunicationType.toInt(CommunicationType.RALLY_POINT), archonCOM.x, archonCOM.y} );
                        communicator.sendCommunication(2, rushMsg);

                        MapLocation rushLoc = mapKnowledge.getOppositeCorner(archonCOM);
                        rushMsg.setValues(new int[] {CommunicationType.toInt(CommunicationType.ATTACK), rushLoc.x, rushLoc.y} );
                        communicator.sendCommunication(2, rushMsg);
                    }

                    int id = rc.senseRobotAtLocation(rc.getLocation().add(dirs[i])).ID;
                    MissionCommunication communication = new MissionCommunication();
                    communication.opcode = CommunicationType.CHANGEMISSION;
                    communication.id = id;
                    communication.rType = Bots.typeFromBot(nextBot);
                    communication.bType = nextBot;
                    communication.newBType = nextBot;
                    communicator.sendCommunication(2, communication);

                    Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication(id);
                    communicator.sendCommunication(5, mapBoundsCommunication);

                    for (int j = mapKnowledge.exploredEdges.length; --j>=0; )
                    {
                        if (mapKnowledge.exploredEdges[j])
                        {
                            Communication mapBoundDiscovered = new EdgeDiscovered();
                            mapBoundDiscovered.setValues(new int[]{CommunicationType.toInt(CommunicationType.EDGE_EXPLORED), id, j});
                            communicator.sendCommunication(5, mapBoundDiscovered);
                        }
                    }


                    if (nextBot == Bots.DENKILLERGUARD || nextBot == Bots.DENKILLERSOLDIER)
                    {
                        for (int j = mapKnowledge.denLocations.length; --j>=0; )
                        {
                            MapLocation den = mapKnowledge.denLocations.array[j];

                            if (den != null)
                            {
                                Communication communicationDen = new SimpleBotInfoCommunication();
                                communicationDen.setValues(new int[] {CommunicationType.toInt(CommunicationType.SDEN), 0, den.x, den.y});
                                communicator.sendCommunication(2, communicationDen);
                            }
                        }
                    }

                    nextBot = buildOrder.nextBot();
                    nextType = Bots.typeFromBot(nextBot);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method creates the initial starting map and broadcasts it to the world
     */
    public static void updateStartingMap()
    {
        try { mapKnowledge.senseAndUpdateEdges(); communicator.sendCommunication(2500, mapKnowledge.getMapBoundsCommunication(id)); } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * This method sends out the initial location of the archons
     */
    public static void sendOutInitialLocation()
    {
        try {
            Communication communication = new SimpleBotInfoCommunication();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.SARCHON), id, rc.getLocation().x, rc.getLocation().y});
            communicator.sendCommunication(2500, communication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
