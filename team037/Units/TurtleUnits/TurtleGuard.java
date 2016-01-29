package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.DataStructures.RobotTypeTracker;
import team037.FightMicro;
import team037.Utilites.MapUtils;
import team037.Units.BaseUnits.BaseGaurd;
import team037.Utilites.MoveUtils;
import team037.Utilites.ZombieTracker;

public class TurtleGuard extends BaseGaurd
{
    public static RobotTypeTracker robotTypeTracker;
    public static ZombieTracker zombieTracker;
    private static int nearestCombatEnemy;
    private static RobotInfo nearestCombatEnemyInfo;
    private static boolean nonScoutEnemies;

    private static int nearestMeleeZombie;
    private static RobotInfo nearestMeleeZombieInfo;
    private static int nearestBigZombie;
    private static RobotInfo nearestBigZombieInfo;
    private static int nearestRangedZombie;
    private static RobotInfo nearestRangedZombieInfo;
    private static int nearestDen;
    private static RobotInfo nearestDenInfo;
    private static boolean justDen;


    private static int nearestArchon;
    private static RobotInfo nearestArchonInfo;
    private static int nearestScout;
    private static RobotInfo nearestScoutInfo;
    private static int nearestTurret;
    private static RobotInfo nearestTurretInfo;


    private static boolean coreReady;
    private static boolean weaponReady;

    public TurtleGuard(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, alliedArchonStartLocs);
        robotTypeTracker = new RobotTypeTracker(RobotType.TURRET, rc);
        zombieTracker = new ZombieTracker(rc);
    }

    public boolean fight() throws GameActionException
    {
        return false;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();
        coreReady = rc.isCoreReady();
        weaponReady = rc.isWeaponReady();

        if (rallyPoint != null)
       {
           rc.setIndicatorLine(currentLocation, rallyPoint, 0, 0, 255);

           if (!rallyPoint.equals(turtlePoint)) {
               turtlePoint = rallyPoint;
           }

           rallyPoint = null;
       }

        nearestCombatEnemy = Integer.MAX_VALUE;
        nearestCombatEnemyInfo = null;
        nearestCombatEnemy = Integer.MAX_VALUE;
        nearestCombatEnemyInfo = null;

        nonScoutEnemies = false;


        nearestBigZombie = Integer.MAX_VALUE;
        nearestBigZombieInfo = null;
        nearestMeleeZombie = Integer.MAX_VALUE;
        nearestMeleeZombieInfo = null;
        nearestRangedZombie = Integer.MAX_VALUE;
        nearestRangedZombieInfo = null;
        nearestDen = Integer.MAX_VALUE;
        nearestDenInfo = null;
        justDen = true;

        for (int i = zombies.length; --i >= 0;) {
            int dist = zombies[i].location.distanceSquaredTo(currentLocation);
            switch (zombies[i].type) {
                case FASTZOMBIE:
                case STANDARDZOMBIE:
                    justDen = false;
                    if (dist < nearestMeleeZombie) {
                        nearestMeleeZombie = dist;
                        nearestMeleeZombieInfo = zombies[i];
                    }
                    break;
                case BIGZOMBIE:
                    justDen = false;
                    if (dist < nearestBigZombie) {
                        nearestBigZombie = dist;
                        nearestBigZombieInfo = zombies[i];
                    }
                    break;
                case RANGEDZOMBIE:
                    justDen = false;
                    if (dist < nearestRangedZombie) {
                        nearestRangedZombie = dist;
                        nearestRangedZombieInfo = zombies[i];
                    }
                case ZOMBIEDEN:
                    if (dist < nearestDen) {
                        nearestDen = dist;
                        nearestDenInfo = zombies[i];
                    }
            }
        }

        nearestArchon = Integer.MAX_VALUE;
        nearestArchonInfo = null;
        nearestTurret = Integer.MAX_VALUE;
        nearestTurretInfo = null;
        nearestScout = Integer.MAX_VALUE;
        nearestScoutInfo = null;

        for (int i = allies.length; --i >= 0;) {
            int dist = allies[i].location.distanceSquaredTo(currentLocation);
            switch(allies[i].type) {
                case TURRET:
                case TTM:
                    if (dist < nearestTurret) {
                        nearestTurret = dist;
                        nearestTurretInfo = allies[i];
                    }
                break;
                case SCOUT:
                    if (dist < nearestScout) {
                        nearestScout = dist;
                        nearestScoutInfo = allies[i];
                    }
                case ARCHON:
                    if (dist < nearestArchon) {
                        nearestArchon = dist;
                        nearestArchonInfo = allies[i];
                    }
            }
        }

    }

    // additional methods with default behavior
    @Override
    public void handleMessages() throws GameActionException
    {
        communications = communicator.processCommunications();
        for(int k = communications.length; --k >= 0;)
        {
            switch(communications[k].opcode)
            {
                case SARCHON:
                case SENEMY:
                case SZOMBIE:
                case SDEN:
                case SPARTS:
                    int values[] = communications[k].getValues();

                    if (values.length >= 6)
                    {
                        int id = values[3];

                        if (RobotTypeTracker.contains(id))
                        {
                            int x = values[4];
                            int y = values[5];

                            MapLocation target = new MapLocation(x,y);
                            navigator.setTarget(target);
                        }
                    }

                    break;

                case CHANGEMISSION:
                    if(missionComs)
                    {
                        interpretMissionChange(communications[k]);
                    }
                    break;
                case ATTACK:
                case RALLY_POINT:
                    if(archonComs)
                    {
                        interpretLocFromArchon(communications[k]);
                    }
                    break;
                case ARCHON_DISTRESS:
                    if(archonDistressComs)
                    {
                        interpretDistressFromArchon(communications[k]);
                    }
                    break;
            }
        }
    }

    private static final String ENEMIES_IN_OPEN = "enemies in the open";
    private static final String ZOMBIES_IN_OPEN = "zombies in the open";
    private static final String MOVE_TO_TURTLE_POINT = "move to turtle location";
    private static final String ZOMBIES_NEAR_RALLY_POINT = "zombies near rally point";
    private static final String ENEMIES_NEAR_RALLY_POINT = "enemies near rally point";
    private static final String MOVE_TO_COMBAT = "move to combat";
    private static final String PATROL_TURTLE_POINT = "patrol turtle point";
    private static final String MOVE_OUT_OF_CORNER = "move out of corner";

    @Override
    public boolean act() throws GameActionException {
        String lastAction = "NONE";
        if (turtlePoint != null) {
            rc.setIndicatorLine(currentLocation, turtlePoint, 0, 255, 0);
        }
        if (nearRallyPoint()) {
            if (zombiesNearTurtlePoint()) {
                lastAction = ZOMBIES_NEAR_RALLY_POINT;
            } else if (moveToCombat()) {
                lastAction = MOVE_TO_COMBAT;
            } else if (moveOutOfCorner()) {
                lastAction = MOVE_OUT_OF_CORNER;
            } else if (patrolTurtlePoint()) {
                lastAction = PATROL_TURTLE_POINT;
            }
        } else {
            if (zombiesInOpen()) {
                lastAction = ZOMBIES_IN_OPEN;
            } else if (moveToTurtlePoint()) {
                lastAction = MOVE_TO_TURTLE_POINT;
            }
        }
        if (lastAction.equals("NONE") && coreReady) {
            lastAction = "RANDO";
            MoveUtils.tryMoveAnywhere(MapUtils.randomDirection(id, round), true);
        }
        rc.setIndicatorString(0, lastAction + " " + round);
        return !lastAction.equals("NONE");

    }

    private boolean moveOutOfCorner() throws GameActionException {
        //prereq
        if (!coreReady) {
            return false;
        }
        // detect in 4 cardinal directions, if 2 are off map, move diagonally away!
        int num = 0;
        Direction toMove = Direction.NONE;
        if (!rc.onTheMap(currentLocation.add(Direction.NORTH, 4))) {
            num += 1;
            toMove = Direction.SOUTH;
        }
        if (!rc.onTheMap(currentLocation.add(Direction.SOUTH, 4))) {
            num += 1;
            toMove = Direction.NORTH;
        }
        if (num == 0) {
            return false;
        }
        if (!rc.onTheMap(currentLocation.add(Direction.EAST, 4))) {
            num += 1;
            toMove = MapUtils.addDirections(toMove, Direction.WEST);
        }
        if (!rc.onTheMap(currentLocation.add(Direction.WEST, 4))) {
            num += 1;
            toMove = MapUtils.addDirections(toMove, Direction.EAST);
        }
        if (num == 2) {
            return MoveUtils.tryMoveForwardOrLeftRight(toMove, true);
        }
        return false;
    }


    private static boolean reverse = false;
    /*
    PATROL_TURTLE POINT
     */
    private boolean patrolTurtlePoint() throws GameActionException {
        // prereq
        if (!coreReady) {
            return false;
        }

        if (zombies.length == 0 && nearestArchon <=2) {
            rc.setIndicatorString(1, "making room for archon!");
            return MoveUtils.tryMoveForwardOrSideways(nearestArchonInfo.location.directionTo(currentLocation), true);
        }

        if (nearestTurret <= 2) {
            int turrets = 0;
            int guards = 0;
            for (int i = nearByAllies.length; --i >= 0;) {
                switch (nearByAllies[i].type) {
                    case GUARD:
                        guards += 1;
                        break;
                    case TURRET:
                    case TTM:
                        turrets += 1;
                        break;
                }
            }
            if (turrets <= 2) {
                Direction oppositeTurret = nearestTurretInfo.location.directionTo(currentLocation);
                if (rc.onTheMap(currentLocation.add(oppositeTurret, 3)) ){
                    rc.setIndicatorString(1, "chilling next to my turret!");
                    return true;
                }
            }
        }

        if (nearestTurret == Integer.MAX_VALUE) {
            rc.setIndicatorString(1, "can't find a turret!");
            Direction toMove = currentLocation.directionTo(turtlePoint);
            return MoveUtils.tryMoveForwardOrLeftRight(toMove, true);
        }
        if (nearestTurret > 4)  {
            if (reverse) {
                rc.setIndicatorString(1, "moving to turret!");
                return MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestTurretInfo.location).rotateRight(), true);
            } else {
                rc.setIndicatorString(1, "moving to turret!");
                return MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestTurretInfo.location).rotateLeft(), true);
            }
        }



        Direction toMove = currentLocation.directionTo(turtlePoint).rotateLeft().rotateLeft().rotateLeft();
        if (reverse) {
            toMove = currentLocation.directionTo(turtlePoint).rotateRight().rotateRight().rotateRight();
        }

        if (!rc.onTheMap(currentLocation.add(toMove, 3))) {
            reverse = !reverse;
            toMove = toMove.opposite().rotateRight();
        }

        rc.setIndicatorString(1, "patrol move!");
        return MoveUtils.tryMoveForwardOrLeftRight(toMove, true);
    }

    /*
    MOVE TO COMBAT
     */
    private boolean moveToCombat() {
        return false;
    }



    /*
    ZOMBIES NEAR TURTLE POINT
     */
    private boolean zombiesNearTurtlePoint() throws GameActionException {
        // prereqs
        if (zombies.length == 0) {
            return false;
        }

        if (justDen) {
            rc.setIndicatorString(1, "just den!");
            return zombiesInOpen();
        }
        if (weaponReady) {
            if (nearestBigZombie == 1) {
                rc.attackLocation(nearestBigZombieInfo.location);
                rc.setIndicatorString(1, "attacking");
                return true;
            }
            if (nearestMeleeZombie == 1) {
                rc.attackLocation(nearestMeleeZombieInfo.location);
                rc.setIndicatorString(1, "attacking");
                return true;
            }
            if (nearestRangedZombie == 1) {
                rc.attackLocation(nearestRangedZombieInfo.location);
                rc.setIndicatorString(1, "attacking");
                return true;
            }
        }
        if (coreReady) {
            if (nearestBigZombie == 1 || nearestMeleeZombie == 1 || nearestRangedZombie == 1) {
                rc.setIndicatorString(1, "standing and fighting!");
                return true;
            }
            else if (nearestBigZombie == 2) {
                rc.setIndicatorString(1, "moving to 1");
                return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestBigZombieInfo.location), false);
            }
            else if (nearestMeleeZombie == 2) {
                rc.setIndicatorString(1, "moving to 1");
                return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestMeleeZombieInfo.location), false);
            }
            else if (nearestRangedZombie == 2) {
                rc.setIndicatorString(1, "moving to 1");
                return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestRangedZombieInfo.location), false);
            }


            if (nearestTurret > 2 && nearestTurret < Integer.MAX_VALUE) {
                rc.setIndicatorString(1, "moving to turret");
                return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestTurretInfo.location), true);
            }

            if (nearestTurret <= 2 || nearestTurret == Integer.MAX_VALUE) {
                if (nearestBigZombie < Integer.MAX_VALUE) {
                    if (rc.senseNearbyRobots(nearestBigZombieInfo.location, 8, us).length > 0) {
                        rc.setIndicatorString(1, "standing guard!");
                        return true;
                    }
                }
                if (nearestMeleeZombie < Integer.MAX_VALUE) {
                    if (rc.senseNearbyRobots(nearestMeleeZombieInfo.location, 8, us).length > 0) {
                        rc.setIndicatorString(1, "standing guard!");
                        return true;
                    }
                }
            }



            if (nearestTurret <= 2 && nearestTurret < Integer.MAX_VALUE) {
                rc.setIndicatorString(1, "moving away from turret");
                return MoveUtils.tryMoveForwardOrLeftRight(nearestTurretInfo.location.directionTo(currentLocation), false);
            } else if (nearestScout <= 2 && nearestTurret < Integer.MAX_VALUE) {
                rc.setIndicatorString(1, "moving away from turret");
                return MoveUtils.tryMoveForwardOrLeftRight(nearestScoutInfo.location.directionTo(currentLocation), false);
            }
        }

        rc.setIndicatorString(1, "can't do anything!");
        return true;
    }

    /*
    MOVE_TO_TURTLE_POINT
     */
    private boolean moveToTurtlePoint() throws GameActionException {
        // prereqs
        if (!coreReady) {
            return false;
        }
        if (turtlePoint != null && !currentLocation.isAdjacentTo(turtlePoint))  {
            return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(turtlePoint), true);
        }
        return false;
    }

    /*
    ZOMBIES IN OPEN
      Fight the zombies in the wild!
     */
    private boolean zombiesInOpen() throws GameActionException {
        // precondition
        if (zombies.length == 0) {
            return false;
        }

        if (justDen) {
            if (currentLocation.isAdjacentTo(nearestDenInfo.location)) {
                if (zombieTracker.getNextZombieRound() - round <= 2 && coreReady) {
                    if (MoveUtils.tryMoveForwardOrSideways(nearestDenInfo.location.directionTo(currentLocation), false)) {
                        rc.setIndicatorString(1, "Moving away because spawn is near!");
                        return true;
                    }
                }
                if (weaponReady) {
                    rc.attackLocation(nearestDenInfo.location);
                    return true;
                }
            }
            if (coreReady) {
                if (currentLocation.distanceSquaredTo(nearestDenInfo.location) > 8 && MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestDenInfo.location), true)) {
                    return true;
                }
            }
        }

        if (rc.getHealth() < 10) {
            return fightMicro.guardZombieMicro(zombies, nearByZombies, allies);
        }

        if (coreReady) {
            if (nearestBigZombie < Integer.MAX_VALUE) {

                if (coreReady) {
                    if (currentLocation.distanceSquaredTo(nearestBigZombieInfo.location) == 1) {
                        if (weaponReady) {
                            if (currentLocation.isAdjacentTo(nearestBigZombieInfo.location)) {
                                rc.attackLocation(nearestBigZombieInfo.location);
                                return true;
                            }
                        }
                        return true;
                    } else {
                        return MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestBigZombieInfo.location), true);
                    }

                }

            }

            if (nearestMeleeZombie < Integer.MAX_VALUE) {

                if (coreReady) {
                    if (currentLocation.distanceSquaredTo(nearestMeleeZombieInfo.location) == 1) {
                        if (weaponReady) {
                            if (currentLocation.isAdjacentTo(nearestMeleeZombieInfo.location)) {
                                rc.attackLocation(nearestMeleeZombieInfo.location);
                                return true;
                            }
                        }
                        return true;
                    } else {
                        return MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestMeleeZombieInfo.location), true);
                    }
                }
            }

        }

        return fightMicro.guardZombieMicro(zombies, nearByZombies, allies);
    }


    /*
    NEAR RALLY POINT
       Are we close to the rally point?
     */
    private boolean nearRallyPoint() {
        if (turtlePoint == null) {
            return false;
        }
        return currentLocation.distanceSquaredTo(turtlePoint) < 100;
    }

}
