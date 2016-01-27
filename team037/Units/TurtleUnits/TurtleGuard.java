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

    @Override
    public boolean act() throws GameActionException {
        String lastAction = "NONE";
        if (turtlePoint != null) {
            rc.setIndicatorLine(currentLocation, turtlePoint, 0, 255, 0);
        }
        if (nearRallyPoint()) {
            if (zombiesNearTurtlePoint()) {
                lastAction = ZOMBIES_NEAR_RALLY_POINT;
            } else if (enemiesNearTurtlePoint()) {
                lastAction = ENEMIES_NEAR_RALLY_POINT;
            } else if (moveToCombat()) {
                lastAction = MOVE_TO_COMBAT;
            } else if (patrolTurtlePoint()) {
                lastAction = PATROL_TURTLE_POINT;
            }
        } else {
            if (enemiesInOpen()) {
                lastAction = ENEMIES_IN_OPEN;
            } else if (zombiesInOpen()) {
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



    private static boolean reverse = false;
    /*
    PATROL_TURTLE POINT
     */
    private boolean patrolTurtlePoint() throws GameActionException {
        // prereq
        if (!coreReady) {
            return false;
        }
        if (nearestTurret == Integer.MAX_VALUE) {
            Direction toMove = currentLocation.directionTo(turtlePoint);
            return MoveUtils.tryMoveForwardOrLeftRight(toMove, false);
        }
        if (nearestTurret > 8)  {
            if (reverse) {
                return MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestTurretInfo.location).rotateRight(), true);
            } else {
                return MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestTurretInfo.location).rotateLeft(), true);
            }
        }

        if (nearestArchon <=2) {
            return MoveUtils.tryMoveForwardOrSideways(nearestArchonInfo.location.directionTo(currentLocation), true);
        }

        Direction toMove = currentLocation.directionTo(turtlePoint).rotateLeft().rotateLeft().rotateLeft();
        if (reverse) {
            toMove = currentLocation.directionTo(turtlePoint).rotateRight().rotateRight().rotateRight();
        }

        if (!rc.onTheMap(currentLocation.add(toMove, 3))) {
            reverse = !reverse;
            toMove = toMove.opposite().rotateRight();
        }

        return MoveUtils.tryMoveForwardOrLeftRight(toMove, false);
    }

    /*
    MOVE TO COMBAT
     */
    private boolean moveToCombat() {
        return false;
    }

    /*
    ENEMIES NEAR TURTLE POINT
     */
    private boolean enemiesNearTurtlePoint() throws GameActionException {
        // prereqs
        if (nearestCombatEnemy == Integer.MAX_VALUE) {
            return false;
        }
        if (weaponReady && nearestCombatEnemy <= type.attackRadiusSquared) {
            rc.attackLocation(nearestCombatEnemyInfo.location);
            return true;
        }
        if (!coreReady) {
            return false;
        }
        if (nearestTurret > 8 && nearestTurret < Integer.MAX_VALUE) {
            MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestTurretInfo.location), false);
            return true;
        } else if (nearestTurret <= 8) {
            Direction forward = currentLocation.directionTo(turtlePoint).rotateLeft().rotateLeft();
            if (currentLocation.add(forward).distanceSquaredTo(nearestCombatEnemyInfo.location) < nearestCombatEnemy) {
                if (rc.canMove(forward)) {
                    rc.move(forward);
                    return true;
                }
            }

            Direction back = forward.opposite();
            if (currentLocation.add(back).distanceSquaredTo(nearestCombatEnemyInfo.location) < nearestCombatEnemy) {
                if (rc.canMove(back)) {
                    rc.move(back);
                    return true;
                }
            }
            // hold;
            return true;
        } else if (nearestTurret < 2 * nearestCombatEnemy) {
            Direction toMove = currentLocation.directionTo(nearestCombatEnemyInfo.location);
            MoveUtils.tryMoveForwardOrLeftRight(toMove, false);
        }

        return true;
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

            if (nearestTurret <= 9 || nearestTurret == Integer.MAX_VALUE) {
                if (nearestBigZombie < Integer.MAX_VALUE) {
                    if (rc.senseNearbyRobots(nearestBigZombieInfo.location, 8, us).length > 0) {
                        rc.setIndicatorString(1, "moving in");
                        return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestBigZombieInfo.location), false);
                    }
                }
                if (nearestMeleeZombie < Integer.MAX_VALUE) {
                    if (rc.senseNearbyRobots(nearestMeleeZombieInfo.location, 8, us).length > 0) {
                        rc.setIndicatorString(1, "moving in");
                        return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestMeleeZombieInfo.location), false);
                    }
                }
            }


            if (nearestTurret <= 2 && nearestTurret < Integer.MAX_VALUE) {
                return MoveUtils.tryMoveForwardOrLeftRight(nearestTurretInfo.location.directionTo(currentLocation), false);
            } else if (nearestScout <= 2 && nearestTurret < Integer.MAX_VALUE) {
                return MoveUtils.tryMoveForwardOrLeftRight(nearestScoutInfo.location.directionTo(currentLocation), false);
            }
        }

        return patrolTurtlePoint();
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

    /*
    ENEMIES_IN_OPEN
       What should we do if we see an enemy in the open?
     */
    private boolean enemiesInOpen() throws GameActionException {
        // precondition
        return false;
    }

}
