package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.SortedParts;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Messages.MissionCommunication;
import team037.Messages.SimpleBotInfoCommunication;
import team037.Unit;
import team037.MapKnowledge;
import team037.Units.PacMan.PacMan;
import team037.Utilites.BuildOrderCreation;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.ZombieTracker;


public class BaseArchon extends Unit implements PacMan
{
    public BuildOrder buildOrder;
    public static Bots nextBot;
    public static RobotType nextType;
    public static RobotInfo[] neutralBots;
    public static SortedParts sortedParts = new SortedParts();
    private int rushingUnits = 0;
    private boolean sentRushSignal = false;
    private int turnHealed = 0;
    private int retreatCall = 0;
    public static ZombieTracker zombieTracker;
    private static int distToFurthestArchon;

    public BaseArchon(RobotController rc)
    {
        super(rc);
        buildOrder = BuildOrderCreation.createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Bots.typeFromBot(nextBot);
        zombieTracker = new ZombieTracker(rc);
        archonComs = false;
        archonDistressComs = false;
        rc.setIndicatorString(0, "Base Archon zombie Strength: " + zombieTracker.getZombieStrength());

        distToFurthestArchon = 0;
        for (int i = alliedArchonStartLocs.length; --i>=0; )
        {
            int currentDist = currentLocation.distanceSquaredTo(alliedArchonStartLocs[i]);
            if (currentDist > distToFurthestArchon)
            {
                distToFurthestArchon = currentDist;
            }
        }

    }

    public boolean precondition()
    {
        return !rc.isCoreReady();
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (currentLocation != null && navigator.getTarget() != null) {
            rc.setIndicatorLine(currentLocation, navigator.getTarget(), 255, 0, 0);
        }


        // if there are no visible zombies then we should move to collect parts
        if (!FightMicroUtilites.offensiveEnemies(enemies) && !FightMicroUtilites.offensiveEnemies(zombies))
        {
            MapLocation navigatorTarget;

            // if we are trying to get to parts and the location has rubble on it we should clear it
            try
            {
                navigatorTarget = navigator.getTarget();
                if (currentLocation != null && navigatorTarget != null && currentLocation.isAdjacentTo(navigatorTarget))
                {
                    if (rc.isCoreReady() && rc.canSense(navigatorTarget) && rc.senseRubble(navigatorTarget) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH)
                    {
                        rc.clearRubble(currentLocation.directionTo(navigatorTarget));
                        return true;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (!sortedParts.contains(navigator.getTarget()))
            {
                MapLocation parts = getNextPartLocationInSight();

                if (parts != null)
                {
                    navigator.setTarget(parts);
                    rc.setIndicatorLine(currentLocation, parts, 0, 0, 255);
                    rc.setIndicatorString(2, "Parts loc x: " + parts.x + " y: " + parts.y + " round: " + rc.getRoundNum());
                }
            }
        }

        return navigator.takeNextStep();
    }

    public void collectData() throws GameActionException
    {
        super.collectData();

        neutralBots = rc.senseNearbyRobots(2, Team.NEUTRAL);

        if (sortedParts.contains(currentLocation)) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
        }

        MapLocation target = navigator.getTarget();
        if (target != null && rc.canSenseLocation(target) && rc.senseParts(target) == 0)
        {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(target));
        }

        int index = sortedParts.getIndexOfMapLocation(currentLocation);

        if (index >= 0)
        {
            sortedParts.remove(index);
        }

        sortedParts.findPartsAndNeutralsICanSense(rc);

        // heal doesn't effect core cooldown
        healNearbyAllies();

        if (neutralBots.length > 0 && rc.isCoreReady())
        {
            rc.activate(neutralBots[0].location);
            Bots currentBot = nextBot;
            nextBot = getDefaultBotTypes(neutralBots[0].type);
            sendInitialMessages(currentLocation.directionTo(neutralBots[0].location));
            nextBot = currentBot;
        }

        MapLocation neutralArchon = sortedParts.getNeutralArchon();
        
        if (neutralArchon != null)
        {
            if (!navigator.getTarget().equals(neutralArchon))
            {
                System.out.println("We see a neutral archon");
                navigator.setTarget(neutralArchon);
            }
           
            if (rc.isCoreReady())
            {
                navigator.takeNextStep();
            }
        }

    }

    public Bots getDefaultBotTypes(RobotType type)
    {
        switch (type)
        {
            case SOLDIER:
                return Bots.BASESOLDIER;
            case TURRET:
                return Bots.BASETURRET;
            case TTM:
                return Bots.BASETURRET;
            case GUARD:
                return Bots.BASEGAURD;
            case SCOUT:
                return Bots.BASESCOUT;
            case ARCHON:
                return Bots.ALPHAARCHON;
            case VIPER:
                return Bots.BASEVIPER;
            default:
                return Bots.BASESOLDIER;
        }
    }

    public boolean fight() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(enemies)) return false;

        rc.setIndicatorDot(currentLocation, 255, 0, 0);

        return runAway(null);
    }

    public boolean fightZombies() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(zombies)) return false;

        rc.setIndicatorDot(currentLocation, 255, 0, 0);

        return runAway(null);
    }

    @Override
    public void sendMessages() throws GameActionException
    {
        int offensiveEnemies = 0;

        for (int i = enemies.length; --i>=0;)
        {
            switch (enemies[i].type)
            {
                case TURRET:
                case GUARD:
                case SOLDIER:
                case VIPER:
                    offensiveEnemies++;
            }
        }

        offensiveEnemies += zombies.length;

        if (offensiveEnemies > allies.length && (rc.getRoundNum() - retreatCall) > 25 && msgsSent < 20)
        {
            retreatCall = rc.getRoundNum();
            Communication distressCall = new BotInfoCommunication();
            distressCall.setValues(new int[]{CommunicationType.toInt(CommunicationType.ARCHON_DISTRESS), 0, 0, id, currentLocation.x, currentLocation.y});
            communicator.sendCommunication(MapKnowledge.getRange(), distressCall);
            msgsSent++;
        }

        if(mapKnowledge.firstFoundEdge && msgsSent < 20)
        {
            Communication com = mapKnowledge.getMapBoundsCommunication();
            communicator.sendCommunication(distToFurthestArchon, com);
            msgsSent++;
            mapKnowledge.firstFoundEdge = false;
            mapKnowledge.updated = false;
        }
        if(mapKnowledge.updated && msgsSent < 20)
        {
            Communication com = mapKnowledge.getMapBoundsCommunication();
            communicator.sendCommunication(MapKnowledge.getRange(), com);
            msgsSent++;
            mapKnowledge.updated = false;
        }
    }

    public boolean healNearbyAllies() throws GameActionException {
        // precondition
        if (nearByAllies.length == 0 || turnHealed == rc.getRoundNum()) {
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

        try
        {
            if (weakest != null)
            {
                if (rc.senseRobotAtLocation(weakest.location) != null)
                {
                    rc.repair(weakest.location);
                }
                turnHealed = rc.getRoundNum();
                return true;
            }
        } catch (Exception e) {e.printStackTrace();}

        return false;
    }

    // maybe spawn a unit or repair a damaged unit
    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        if (enemies.length > allies.length)
        {
            return false;
        }

        return buildNextUnit();
    }

    public boolean buildNextUnit() throws GameActionException
    {
        // if there are multiple archons and we have limited parts and we see
        // either parts or neutrals we should let the other archon's build while
        // we pick them up
        if (alliedArchonStartLocs.length > 1 && rc.getTeamParts() < 200 && (rc.sensePartLocations(sightRange).length > 0 || rc.senseNearbyRobots(sightRange, Team.NEUTRAL).length > 0))
        {
            return false;
        }

        Bots temp = changeBuildOrder(nextBot);

        if (!temp.equals(nextBot))
        {
            if (rc.hasBuildRequirements(Bots.typeFromBot(temp)) && rc.isCoreReady())
            {
                Bots temp2 = nextBot;
                nextBot = temp;

                Direction dir = build();
                if(dir != Direction.NONE)
                {
                    sendInitialMessages(dir);
                    nextBot = temp2;
                    nextType = Bots.typeFromBot(nextBot);
                    return true;
                }
            }
        }
        else if(rc.hasBuildRequirements(nextType) && rc.isCoreReady())
        {
            Direction dir = build();
            if(dir != Direction.NONE)
            {
                sendInitialMessages(dir);
                nextBot = buildOrder.nextBot();
                nextType = Bots.typeFromBot(nextBot);
                return true;
            }
        }

        return false;
    }

    public void sendInitialMessages(Direction dir) throws GameActionException
    {
        int id = rc.senseRobotAtLocation(rc.getLocation().add(dir)).ID;
        MissionCommunication communication = new MissionCommunication();
        communication.opcode = CommunicationType.CHANGEMISSION;
        communication.id = id;
        communication.rType = nextType;
        communication.bType = nextBot;
        communication.newBType = nextBot;
        communicator.sendCommunication(2, communication);

        Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication();
        communicator.sendCommunication(2, mapBoundsCommunication);

        for (int j = mapKnowledge.dens.length; --j>=0; )
        {
            MapLocation den = mapKnowledge.dens.array[j];

            if (den != null)
            {
                Communication communicationDen = new SimpleBotInfoCommunication();
                communicationDen.setValues(new int[] {CommunicationType.toInt(CommunicationType.SDEN), 0, den.x, den.y});
                communicator.sendCommunication(2, communicationDen);
            }
        }
    }

    public void sendInitialMessages(Direction dir, RobotType nextType, Bots nextBot, boolean sendDenLocs) throws GameActionException
    {
        int id = rc.senseRobotAtLocation(rc.getLocation().add(dir)).ID;
        MissionCommunication communication = new MissionCommunication();
        communication.opcode = CommunicationType.CHANGEMISSION;
        communication.id = id;
        communication.rType = nextType;
        communication.bType = nextBot;
        communication.newBType = nextBot;
        communicator.sendCommunication(2, communication);

        Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication();
        communicator.sendCommunication(2, mapBoundsCommunication);

        if (sendDenLocs) {
            for (int j = mapKnowledge.dens.length; --j>=0; )
            {
                MapLocation den = mapKnowledge.dens.array[j];

                if (den != null)
                {
                    Communication communicationDen = new SimpleBotInfoCommunication();
                    communicationDen.setValues(new int[] {CommunicationType.toInt(CommunicationType.SDEN), 0, den.x, den.y});
                    communicator.sendCommunication(2, communicationDen);
                }
            }
        }
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

    public static MapLocation getNextPartLocation() throws GameActionException
    {
        MapLocation next = sortedParts.getBestSpot(currentLocation);
        MapLocation lastTarget = null;

        while (next != null && (rc.canSenseLocation(next) && rc.senseParts(next) == 0 && (rc.senseRobotAtLocation(next) == null || !rc.senseRobotAtLocation(next).team.equals(Team.NEUTRAL))))
        {
            int index = sortedParts.getIndexOfMapLocation(next);
            if (index < 0)
            {
                sortedParts.hardRemove(next);
                lastTarget = new MapLocation(next.x, next.y);
            }
            else
            {
                sortedParts.remove(index);
            }

            next = sortedParts.getBestSpot(currentLocation);

            if (lastTarget != null && lastTarget.equals(next))
            {
                System.out.println("we have a problem");
            }
        }

        return next;
    }

    public static MapLocation getNextPartLocationInSight() throws GameActionException
    {
        MapLocation next = sortedParts.getBestSpotInSightRange(currentLocation);
        MapLocation lastTarget = null;

        while (next != null && (rc.canSenseLocation(next) && rc.senseParts(next) == 0 &&  (rc.senseRobotAtLocation(next) == null || !rc.senseRobotAtLocation(next).team.equals(Team.NEUTRAL))))
        {
            int index = sortedParts.getIndexOfMapLocation(next);
            if (index < 0)
            {
                sortedParts.hardRemove(next);
                lastTarget = new MapLocation(next.x, next.y);
            }
            else
            {
                sortedParts.remove(index);
            }

            next = sortedParts.getBestSpotInSightRange(currentLocation);

            if (lastTarget != null && lastTarget.equals(next))
            {
                System.out.println("we have a problem");
            }
        }

        if (next != null)
        {
            rc.setIndicatorString(1, "x: " + next.x + " y: " + next.y);
        }
        return next;
    }

    public Direction build() throws GameActionException
    {
        double rubble = Double.MAX_VALUE;
        Direction least = null;
        for (int i = dirs.length; --i>=0; )
        {
            if(rc.onTheMap(currentLocation.add(dirs[i])))
            {
                if(rc.canBuild(dirs[i], nextType))
                {
                    rc.build(dirs[i], nextType);
                    return dirs[i];
                }
                double tempRubble = rc.senseRubble(currentLocation.add(dirs[i]));
                if(tempRubble < rubble && tempRubble > 0)
                {
                    rubble = tempRubble;
                    least = dirs[i];
                }
            }
        }
        if (least != null)
        {
            try { rc.clearRubble(least); } catch (Exception e) { e.printStackTrace(); }
        }

        return Direction.NONE;
    }

    public Bots changeBuildOrder(Bots nextBot)
    {
        return nextBot;
    }
}
