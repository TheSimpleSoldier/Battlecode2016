package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.DataStructures.SimpleRobotInfo;
import team037.Enums.CommunicationType;
import team037.Enums.Strategies;
import team037.Messages.AttackCommunication;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Messages.SimpleBotInfoCommunication;
import team037.RobotPlayer;
import team037.Units.PacMan.PacMan;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.MapUtils;
import battlecode.common.*;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseArchon;

public class TurtleArchon extends BaseArchon implements PacMan
{
    private boolean reachedTurtleSpot = false;
    private boolean updatedTurtleSpot = false;
    private MapLocation origionalTurtleSpot;
    private boolean hiding = false;
    private int updateRound = 0;
    private int index = 0;
    private int ArchonDist;
    private boolean offensiveEnemies = false;
    private boolean offensiveAllies = false;
    private boolean underAttack = false;
    private static int lastUnderAttack = 0;
    private int lastZombieSighting = 0;
    private int lastEnemieSighting = 0;
    private static int adjacentTurrets = 0;
    private static boolean scavenging = false;
    private static Direction lastDir = null;
    private static boolean stayHome = false;
    private static int lastRoundRunAway = 0;
    private static boolean lastRush = false;



    public TurtleArchon(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, enemyArchonStartLocs);
        origionalTurtleSpot = new MapLocation(turtlePoint.x, turtlePoint.y);
        rc.setIndicatorString(0, "Turtle archon");

        for (int i = alliedArchonStartLocs.length; --i>=0;)
        {
            if (alliedArchonStartLocs[i].equals(currentLocation))
            {
                index = i;
                break;
            }
        }

        if (!currentLocation.equals(turtlePoint))
        {
            scavenging = true;
            rc.setIndicatorString(0, "Scavenging archon");
        }
        else
        {
            System.out.println("we are staying home");
            stayHome = true;
        }

        lastZombieSighting = rc.getRoundNum();

        try
        {
            ArchonDist = MapUtils.getCenterOfMass(alliedArchonStartLocs).distanceSquaredTo(MapUtils.getCenterOfMass(enemyArchonStartLocs));
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();

        if (target == null) return true;
        if (FightMicroUtilites.offensiveEnemies(enemies) || FightMicroUtilites.offensiveEnemies(zombies)) return true;
        if (target.equals(turtlePoint) && currentLocation.distanceSquaredTo(target) <= 2) return true;
        if (currentLocation.equals(target)) return true;
        if (!target.equals(turtlePoint) && rc.canSenseLocation(target) && rc.senseParts(target) == 0 && (rc.senseRobotAtLocation(target) == null || rc.senseRobotAtLocation(target).team != Team.NEUTRAL)) return true;
        if (zombieTracker.getNextZombieRound() - rc.getRoundNum() <= 25) return true;

        return false;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (rallyPoint != null)
        {
            if (!rallyPoint.equals(turtlePoint))
            {
                turtlePoint = rallyPoint;
                updateRound = rc.getRoundNum();
            }
            rallyPoint = null;
        }

        if (stayHome)
        {
            rc.setIndicatorDot(currentLocation.add(Direction.EAST), 255, 255, 255);
        }

        rc.setIndicatorLine(currentLocation, turtlePoint, 0, 255, 0);

        boolean offensiveZombies = FightMicroUtilites.offensiveEnemies(zombies);
        offensiveEnemies = FightMicroUtilites.offensiveEnemies(enemies);

        int round = rc.getRoundNum();

        if (scavenging)
        {
            String condition = "";
            if (zombies.length > 0 && rc.isCoreReady())
            {
                nextType = RobotType.GUARD;
                nextBot = Bots.TURTLEGUARD;
                buildNextUnit();
            }

            if (tooManyZombies())
            {
                condition = "tooManyZombies";
                scavenging = false;
            }

            if (FightMicroUtilites.offensiveEnemies(enemies))
            {
                condition = "offensiveEnemies";
                scavenging = false;
            }

            if (RobotType.ARCHON.maxHealth - rc.getHealth() >= 300)
            {
                condition = "lost health";
                scavenging = false;
                stayHome = true;
            }

//            if (zombieTracker.getNextZombieRound() - round < 50 && zombieTracker.getNextZombieRoundStrength() > 100)
//            {
//                condition = "strong zombie round";
//                scavenging = false;
//            }

            // if we stop scavenging...
            if (!scavenging)
            {
                rc.setIndicatorString(0, "not scavenging: " + round + "  " + condition + " " + zombieTracker.getNextZombieRoundStrength());
                navigator.setTarget(turtlePoint);
            }
        }

        if (offensiveZombies)
        {
            lastZombieSighting = round;
        }

        if (offensiveEnemies)
        {
            lastEnemieSighting = round;
        }

        offensiveEnemies = offensiveEnemies || offensiveZombies;
        offensiveAllies = FightMicroUtilites.offensiveEnemies(allies);

        adjacentTurrets = 0;
        RobotInfo [] toCheck = allies;
        if (allies.length > 10) {
            toCheck = rc.senseNearbyRobots(1, us);
        }
        for (int i = 0; --i >= 0;) {
            if (toCheck[i].location.distanceSquaredTo(currentLocation) == 1)
                if (toCheck[i].type.equals(RobotType.TURRET) || toCheck[i].type.equals(RobotType.TTM)) {
                    adjacentTurrets += 1;
                }
        }

        underAttack = underAttack();
        if (underAttack) {
            lastUnderAttack = round;
        }

        if (RobotPlayer.strategy.equals(Strategies.DYNAMIC_TURTLE) && (round - updateRound) > 100)
        {
            int closestDen = 99999;
            MapLocation den = null;

            if (mapKnowledge.dens.hasLocations()) {
                for (int i = mapKnowledge.dens.length; --i >= 0; ) {
                    if (mapKnowledge.dens.array[i] != null) {
                        MapLocation currentDen = mapKnowledge.dens.array[i];

                        if (rc.canSenseLocation(currentDen) && (rc.senseRobotAtLocation(currentDen) == null || rc.senseRobotAtLocation(currentDen).team != Team.ZOMBIE)) {
                            mapKnowledge.dens.remove(currentDen);

                            if (!underAttack) {
                                Communication communication = new SimpleBotInfoCommunication();
                                communication.opcode = CommunicationType.DEAD_DEN;
                                communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.DEAD_DEN), 0, currentDen.x, currentDen.y});
                                communicator.sendCommunication(400, communication);
                            }
                        } else {
                            int currentDist = currentDen.distanceSquaredTo(turtlePoint);

                            if (currentDist < closestDen) {
                                closestDen = currentDist;
                                den = currentDen;
                            }
                        }
                    }
                }
                if (den != null && den.distanceSquaredTo(turtlePoint) > 20)
                {
                    if (!underAttack)
                    {
                        rc.setIndicatorLine(currentLocation, den, 0, 0, 0);
                        turtlePoint = den.add(den.directionTo(turtlePoint), 3);
                        Communication newRallyPoint = new AttackCommunication();
                        newRallyPoint.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), turtlePoint.x, turtlePoint.y});
                        communicator.sendCommunication(1600, newRallyPoint);
                    }

                    hiding = false;
                }
            }
        }
        else
        {
            MapLocation currentTurtle = new MapLocation(turtlePoint.x, turtlePoint.y);

            if ((round - updateRound) < 100)
            {
            }
            else if (round - lastEnemieSighting < 50)
            {
            }
            else if (round < 300)
            {
            }
            else if (round % 5 != index)
            {
            }
            else
            {
                int closestDen = 99999;
                MapLocation den = null;

                if (rc.getRoundNum() < 1000)
                {
                    if (mapKnowledge.dens.hasLocations())
                    {
                        for (int i = mapKnowledge.dens.length; --i >= 0; )
                        {
                            if (mapKnowledge.dens.array[i] != null)
                            {
                                MapLocation currentDen = mapKnowledge.dens.array[i];

                                if (rc.canSenseLocation(currentDen) && (rc.senseRobotAtLocation(currentDen) == null || rc.senseRobotAtLocation(currentDen).team != Team.ZOMBIE))
                                {
                                    mapKnowledge.dens.remove(currentDen);

                                    if (!underAttack)
                                    {
                                        Communication communication = new SimpleBotInfoCommunication();
                                        communication.opcode = CommunicationType.DEAD_DEN;
                                        communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.DEAD_DEN), 0, currentDen.x, currentDen.y});
                                        communicator.sendCommunication(400, communication);
                                    }
                                }
                                else
                                {
                                    int currentDist = currentDen.distanceSquaredTo(turtlePoint);

                                    if (currentDist < closestDen)
                                    {
                                        closestDen = currentDist;
                                        den = currentDen;
                                    }
                                }
                            }
                        }
                    }
                }

                if (den != null && den.distanceSquaredTo(turtlePoint) > 20 && den.distanceSquaredTo(turtlePoint) < 400)
                {
                    turtlePoint = den.add(den.directionTo(turtlePoint), 3);

                    hiding = false;
                }
                else
                {
                    if (!hiding || round % 50 == index)
                    {
                        int leftX = mapKnowledge.minX;
                        int rightX = mapKnowledge.maxX;
                        int topY = mapKnowledge.minY;
                        int bottomY = mapKnowledge.maxY;

                        int currentX = turtlePoint.x;
                        int currentY = turtlePoint.y;

                        int distToTopLeft = (leftX - currentX) * (leftX - currentX) + (topY - currentY) * (topY - currentY);
                        int distToBottonLeft = (leftX - currentX) * (leftX - currentX) + (bottomY - currentY) * (bottomY - currentY);
                        int distToTopRight = (rightX - currentX) * (rightX - currentX) + (topY - currentY) * (topY - currentY);
                        int distToBottonRight = (rightX - currentX) * (rightX - currentX) + (bottomY - currentY) * (bottomY - currentY);

                        // go left
                        if (distToTopLeft < distToTopRight)
                        {
                            if (distToTopLeft < distToBottonLeft)
                            {
                                turtlePoint = new MapLocation(leftX, topY);
                                turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(rightX, bottomY)), 5);
                            }
                            else
                            {
                                turtlePoint = new MapLocation(leftX, bottomY);
                                turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(rightX, topY)), 5);
                            }
                        }
                        // go right
                        else
                        {
                            if (distToTopRight < distToBottonRight)
                            {
                                turtlePoint = new MapLocation(rightX, topY);
                                turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(leftX, bottomY)), 5);
                            }
                            else
                            {
                                turtlePoint = new MapLocation(rightX, bottomY);
                                turtlePoint = turtlePoint.add(turtlePoint.directionTo(new MapLocation(leftX, topY)), 5);
                            }
                        }

                        // no need to keep updating this
                        hiding = true;
                    }
                }
            }

            if (turtlePoint.equals(currentTurtle) || (zombieTracker.getNextZombieRound() - rc.getRoundNum() < 10 + Math.sqrt(turtlePoint.distanceSquaredTo(origionalTurtleSpot)) * 2 || round - turretSupportMsgRound < 25))
            {
                turtlePoint = currentTurtle;
            }
            else
            {
                updateRound = round;
                Communication newRallyPoint = new AttackCommunication();
                newRallyPoint.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), turtlePoint.x, turtlePoint.y});
                communicator.sendCommunication(3600, newRallyPoint);
                rc.setIndicatorLine(currentLocation, turtlePoint, 0, 0, 0);
                rc.setIndicatorDot(turtlePoint, 255, 0, 0);
            }
        }
    }

    public boolean underAttack()
    {
        if (!offensiveEnemies) return false;
        if (allies.length == 0) return true;
        if (!offensiveAllies) return true;
        if ((enemies.length + zombies.length + 2) > allies.length) return true;

        return false;
    }

    @Override
    public void handleMessages() throws GameActionException
    {
        if (!underAttack)
        {
            super.handleMessages();
        }
    }

    public boolean tooManyZombies()
    {
        if (zombies.length > 2) return true;

        for (int i = zombies.length; --i>=0; )
        {
            if (zombies[i].type == RobotType.RANGEDZOMBIE || zombies[i].type == RobotType.BIGZOMBIE)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (scavenging)
        {
            MapLocation bestParts = getNextPartLocation();

            if (bestParts != null)
            {
                return bestParts;
            }
            else
            {
                Direction direction = null;

                if (lastDir != null)
                {
                    if (Math.random() < 0.9)
                    {
                        direction = lastDir;
                    }
                }

                if (direction == null)
                {
                    direction = dirs[(int) (Math.random() * dirs.length)];
                }

                MapLocation next = currentLocation.add(direction, 4);

                while (!rc.onTheMap(next))
                {
                    direction = direction.rotateLeft();
                    next = currentLocation.add(direction, 4);
                }

                lastDir = direction;

                return next;
            }
        }
        else
        {
            if (stayHome) return turtlePoint;

            if (FightMicroUtilites.offensiveEnemies(enemies)) return turtlePoint;


            int round = rc.getRoundNum();
            int nextZombieRound = zombieTracker.getNextZombieRound();
            if (nextZombieRound - round <= 25) return turtlePoint;
            if (FightMicroUtilites.offensiveEnemies(zombies)) return turtlePoint;
            if (!reachedTurtleSpot && currentLocation.distanceSquaredTo(turtlePoint) > 10) return turtlePoint;
            if (!reachedTurtleSpot) reachedTurtleSpot = true;

            MapLocation bestParts = getNextPartLocation();

            if (bestParts == null) return turtlePoint;

            int turtleDist = turtlePoint.distanceSquaredTo(bestParts);

            // if we have a lot of parts, don't go looking for more!
            if (rc.getTeamParts() > 200) return turtlePoint;
            // if we are safe, don't do anything!
            if (adjacentTurrets == 4) return currentLocation;
            if (turtleDist > (1600 - (round/2))) return turtlePoint;
            if (Math.sqrt(turtleDist) > (nextZombieRound - round)) return turtlePoint;

//            rc.setIndicatorString(1, "BestParts x: " + bestParts.x + " y: " + bestParts.y);



            scavenging = true;

            rc.setIndicatorString(0, "scavenging: " + round + " x: " + bestParts.x + " y: " + bestParts.y);


            return bestParts;
        }
    }

    @Override
    public boolean fight() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(enemies)) return false;
        if (turtlePoint != null) navigator.setTarget(turtlePoint);

        rc.setIndicatorString(2, "runAwayFrom enemies" + round);
        lastRoundRunAway = round;

//        if (!stayHome && zombies.length > 10)
//        {
//            return runAway(null, true, true);
//        }

        return runAway(null);
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(zombies)) return false;
        if (turtlePoint != null) navigator.setTarget(turtlePoint);

        rc.setIndicatorString(2, "runAwayFrom zombies" + round);
        lastRoundRunAway = round;

//        if (!stayHome && zombies.length > 10)
//        {
//            return runAway(null, true, true);
//        }

        return runAway(null);
    }

    @Override
    public void sendInitialMessages(Direction dir) throws GameActionException
    {
        if (!underAttack)
        {
            super.sendInitialMessages(dir);
            Communication newRallyPoint = new AttackCommunication();
            newRallyPoint.setValues(new int[]{CommunicationType.toInt(CommunicationType.RALLY_POINT), turtlePoint.x, turtlePoint.y});
            communicator.sendCommunication(2, newRallyPoint);
        }
    }

    @Override
    public Bots getDefaultBotTypes(RobotType type)
    {
        switch (type)
        {
            case SOLDIER:
                return Bots.TURTLESOLDIER;
            case TURRET:
                return Bots.TURTLETURRET;
            case TTM:
                return Bots.TURTLETURRET;
            case GUARD:
                return Bots.TURTLEGUARD;
            case SCOUT:
                return Bots.SCOUTBOMBSCOUT;
            case ARCHON:
                return Bots.TURTLEARCHON;
            case VIPER:
                return Bots.RUSHINGVIPER;
            default:
                return Bots.TURTLESOLDIER;
        }
    }

    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        // preconditions
        if (stayHome && (!FightMicroUtilites.offensiveEnemies(zombies) || (allies.length >= 3 && FightMicroUtilites.offensiveEnemies(allies))))
        {
            return buildNextUnit();
        }

        boolean offensiveEnemies = (FightMicroUtilites.offensiveEnemies(enemies) || FightMicroUtilites.offensiveEnemies(zombies));
        if (offensiveEnemies && ((enemies.length + zombies.length) > allies.length)) return false;
        if (offensiveEnemies && allies.length == 0) return false;
        if (currentLocation.distanceSquaredTo(turtlePoint) >= 100 && rc.getTeamParts() < 500) return false;
        if (round - lastRoundRunAway < 25) return false;

        return buildNextUnit();
    }

    private int seeScout()
    {
        int count = 0;
        for (int i = allies.length; --i>=0; )
        {
            if (allies[i].type == RobotType.SCOUT) count++;
        }

        return count;
    }

    public boolean takeNextStep() throws GameActionException {
        if (currentLocation != null && navigator.getTarget() != null) {
            rc.setIndicatorLine(currentLocation, navigator.getTarget(), 255, 0, 0);
        }


        // if there are no visible zombies then we should move to collect parts
        if (!FightMicroUtilites.offensiveEnemies(enemies) && !FightMicroUtilites.offensiveEnemies(zombies)) {
            MapLocation navigatorTarget;

            // if we are trying to get to parts and the location has rubble on it we should clear it
            try {
                navigatorTarget = navigator.getTarget();

                if (rc.isCoreReady() && currentLocation != null && navigatorTarget != null && currentLocation.isAdjacentTo(navigatorTarget)) {
                    if (rc.canSense(navigatorTarget) && rc.senseRubble(navigatorTarget) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
                        rc.clearRubble(currentLocation.directionTo(navigatorTarget));

                        // if we went to move but cleared rubble then spawn a scouting scout
                        if ((scavenging && seeScout() < 3) || seeScout() == 0)
                        {
                            while (!rc.isCoreReady()) Clock.yield();
                            buildScavengerScout();
                        }

                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((!stayHome || round > 50) && !sortedParts.contains(navigator.getTarget())) {
                MapLocation parts = getNextPartLocationInSight();

                if (parts != null) {
                    navigator.setTarget(parts);
                    rc.setIndicatorLine(currentLocation, parts, 0, 0, 255);
                    rc.setIndicatorString(2, "Parts loc x: " + parts.x + " y: " + parts.y + " round: " + rc.getRoundNum());
                }
            }
        }

        if (currentLocation != null && ((scavenging && seeScout() < 3) || seeScout() == 0))
        {
            MapLocation current = new MapLocation(currentLocation.x, currentLocation.y);
            boolean takeNextStep = navigator.takeNextStep();

            // if we went to move but cleared rubble then spawn a scouting scout
            if (takeNextStep && rc.getLocation().equals(current) && !rc.isCoreReady())
            {
                while (!rc.isCoreReady()) Clock.yield();
                buildScavengerScout();
                return true;
            }
        }

        return navigator.takeNextStep();
    }

    @Override
    public Bots changeBuildOrder(Bots nextBot)
    {
        int round = rc.getRoundNum();

        if (round > 2500)
        {
            nextType = RobotType.VIPER;
            if (lastRush) {
                lastRush = !lastRush;
                return Bots.RUSHINGVIPER;
            } else {
                lastRush = !lastRush;
                return Bots.SCOUTBOMBVIPER;
            }
        }

        if (scavenging && zombies.length > 0 && allies.length <= 2)
        {
            nextType = RobotType.GUARD;
            return Bots.TURTLEGUARD;
        }
        else if (scavenging && zombieTracker.getNextZombieRound() - round < 30 && seeScout() < 5)
        {
            nextType = RobotType.SCOUT;
            return Bots.SCOUTBOMBSCOUT;
        }
        else if (scavenging && rc.getTeamParts() > 300 && zombieTracker.getZombieStrength() < 5)
        {
            nextType = RobotType.VIPER;
            return Bots.RUSHINGVIPER;
        }


        if (round - lastZombieSighting < 300 && round - lastEnemieSighting > 50)
        {
            if (zombieTracker.getNextZombieRoundStrength() < 5 || seeScout() >= 5) {

            }
            else if (zombieTracker.getNextZombieRoundStrength() < 10 )

            {
                if (zombieTracker.getNextZombieRound() - round < 10)
                {
                    nextType = RobotType.SCOUT;
                    return Bots.SCOUTBOMBSCOUT;
                }
            }
            else if (zombieTracker.getNextZombieRound() - round < 30)
            {
                nextType = RobotType.SCOUT;
                return Bots.SCOUTBOMBSCOUT;
            }
            else if (ArchonDist > 2500 && zombieTracker.getNextZombieRound() - round < 80)
            {
                nextType = RobotType.SCOUT;
                return Bots.SCOUTBOMBSCOUT;
            }
        }


        // late game if we aren't under attack spawn a lot of scout bombs
        if (round > 2500 && lastUnderAttack < 2000) {
            nextType = RobotType.SCOUT;
            return Bots.SCOUTBOMBSCOUT;
        }

        // and a some vipers to make them go "BRAAAAAAAIIIIIIINNNNNS"
        if (round > 2500 && lastUnderAttack < 2500) {
            nextType = RobotType.VIPER;
            if (lastRush) {
                lastRush = !lastRush;
                return Bots.RUSHINGVIPER;
            } else {
                lastRush = !lastRush;
                return Bots.SCOUTBOMBVIPER;
            }
        }

        if (round > 100 && currentLocation.distanceSquaredTo(turtlePoint) < sightRange/2)
        {
            int soldierCount = 0;
            int guardCount = 0;
            int turretCount = 0;

            for (int i = allies.length; --i>=0; )
            {
                switch (allies[i].type)
                {
                    case SOLDIER:
                        soldierCount++;
                        break;
                    case GUARD:
                        guardCount++;
                        break;
                    case TURRET:
                        turretCount++;
                        break;
                }
            }

            // don't want to spawn massive #'s of units instead of turrets
            if (turretCount < 3 && rc.getRobotCount() < 30)
            {
                if (soldierCount < 3)
                {
                    return Bots.TURTLESOLDIER;
                }
                else if (guardCount < 5)
                {
                    return Bots.TURTLEGUARD;
                }
            }
            else
            {
                if (soldierCount < 1)
                {
                    return Bots.TURTLESOLDIER;
                }
                else if (guardCount < 1)
                {
                    return Bots.TURTLEGUARD;
                }
            }
        }

        if (rc.getTeamParts() > 300 && round > 100)
        {
            nextType = RobotType.TURRET;
            return Bots.TURTLETURRET;
        }


        return nextBot;
    }
}
