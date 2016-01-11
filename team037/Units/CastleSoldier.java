package team037.Units;

import battlecode.common.*;
import team037.Enums.Strategies;
import team037.SlugNavigator;
import team037.Utilites.MapUtils;

public class CastleSoldier extends BaseSoldier
{
    private static final String MOVE_TO_MOM = "move to mom";
    private static final String FIGHT = "fight";
    private static final String MOVE_TO_FRONT = "move to front";
    private static final String CLEAN_UP = "clean up";
    private static final String PATROL = "patrol";

    private String lastAction;

    private MapLocation archonLoc;
    SlugNavigator move;
    private int archonId = Integer.MAX_VALUE;
    private Direction momMoved = null;

    public CastleSoldier(RobotController rc)
    {
        super(rc);
        move = new SlugNavigator(rc);
    }

    @Override
    public void handleMessages() throws GameActionException {
    }


    @Override
    public boolean act() throws GameActionException {

        findMom();  // looks for the archon

        int round = rc.getRoundNum();
        if (!rc.isCoreReady()) {
            rc.setIndicatorString(0, "cooldown");
            return false;
        } else if (moveToMom()) {
            lastAction = MOVE_TO_MOM;
        } else if (moveToFront()) {
            lastAction = MOVE_TO_FRONT;
        } else if (fight()) {
            lastAction = FIGHT;
        } else if (cleanUp()) {
            lastAction = CLEAN_UP;
        } else if (patrol()) {
            lastAction = PATROL;
        } else {
            rc.setIndicatorString(0, "wait");
            return false;
        }

        rc.setIndicatorString(0, lastAction);
        return true;
    }

    private void findMom() throws GameActionException {
        if (archonId == Integer.MAX_VALUE) {
            MapLocation location = null;
            int id = Integer.MAX_VALUE;
            RobotInfo[] possible = rc.senseNearbyRobots(2, us);
            for (int i = possible.length; --i >= 0;) {
                if (possible[i].type == RobotType.ARCHON) {
                    location = possible[i].location;
                    id = possible[i].ID;
                    break;
                }
            }
            archonId = id;
            archonLoc = location;
        } else {
            if (rc.canSenseRobot(archonId)) {
                RobotInfo archon = rc.senseRobot(archonId);
                if (!archon.location.equals(archonLoc)) {
                    momMoved = archonLoc.directionTo(archon.location);
                    archonLoc = archon.location;
                }
            }

        }
    }

    private boolean moveToMom() throws GameActionException {
        //precondition
        if (momMoved == null) {
            return false;
        }
        if (rc.canMove(momMoved)) {
            rc.move(momMoved);
            momMoved = null;
            return true;
        }
        return false;
    }

    public boolean fight() throws GameActionException {
        //if (rc.getWeaponDelay() >= 1 || !(rc.getRoundNum() % 2 == 1)) {
        if (rc.getWeaponDelay() >= 1) {
            return false;
        }
        if (nearByEnemies.length == 0 && nearByZombies.length == 0) {
            return false;
        }

        // try attacking enemies or zombies
        if (nearByEnemies.length > 0 && tryAttack(nearByEnemies)) {
            return true;
        } else if (nearByZombies.length > 0 && tryAttack(nearByZombies)) {
            return true;
        }

        return false;
    }

    private boolean tryAttack(RobotInfo[] enemies) throws GameActionException {
        double lowestHP = Integer.MAX_VALUE;
        MapLocation toAttack = null;
        for (int i = enemies.length; --i >= 0;) {
            if(currentLocation.distanceSquaredTo(enemies[i].location) <= type.attackRadiusSquared && enemies[i].health < lowestHP) {
                lowestHP = enemies[i].health;
                toAttack = enemies[i].location;
            }
        }
        if (toAttack != null) {
            rc.attackLocation(toAttack);
            return true;
        }
        return false;
    }

    private boolean moveToFront() throws GameActionException {
        //prereqs
        int eucl = Math.max(Math.abs(currentLocation.x - archonLoc.x), Math.abs(currentLocation.y - archonLoc.y));
        if (eucl == Strategies.CASTLE_SIZE) {
            return false;
        }

        if (eucl < Strategies.CASTLE_SIZE) {
            if (move.getTarget() == null || move.getTarget().distanceSquaredTo(archonLoc) <= 4) {
                Direction toMove = archonLoc.directionTo(currentLocation);
                MapLocation target = MapUtils.findOnMapLocationNUnitsAway(this, toMove, 3);
                move.setTarget(target);
            }
        } else if (eucl > Strategies.CASTLE_SIZE) {
            if (move.getTarget() == null || move.getTarget().distanceSquaredTo(archonLoc) > 4) {
                move.setTarget(archonLoc);
            }
        }
        return move.moveAndClear();
    }

    private boolean cleanUp() throws GameActionException {
        //prereqs
        if (nearByEnemies.length > 0 || nearByZombies.length > 0) {
            return false;
        }
        if (lastAction.equals(CLEAN_UP)) {
            return false;
        }
        /*
        if (!(rc.getRoundNum() % 4 == 0)) {
            return false;
        }
        */

        if (rc.senseRubble(currentLocation) > 0) {
            rc.clearRubble(Direction.NONE);
            return true;
        }
        Direction toCheck = currentLocation.directionTo(archonLoc);
        int i = 8;
        do {
            if (rc.senseRubble(currentLocation.add(toCheck)) > 0) {
                rc.clearRubble(toCheck);
                return true;
            }
            toCheck = toCheck.rotateLeft();
        } while (--i >= 0);
        return false;
    }

    private boolean patrol() throws GameActionException {
        // prereq
        if (rc.getCoreDelay() >= 1) {
            return false;
        }

        Direction toMove = getNextPatrolDirection();
        if (!rc.canMove(toMove)) {
            return false;
        }

        rc.move(toMove);
        return true;
    }

    private Direction getNextPatrolDirection() {
        int xDiff = currentLocation.x - archonLoc.x;
        int yDiff = currentLocation.y - archonLoc.y;

        int square = Strategies.CASTLE_SIZE;
        if (xDiff == square && yDiff == square) {
            return Direction.NORTH;
        } else if (xDiff == square && yDiff == -square) {
            return Direction.WEST;
        } else if (xDiff == -square && yDiff == -square) {
            return Direction.SOUTH;
        } else if (xDiff == -square && yDiff == square) {
            return Direction.EAST;
        } else if (xDiff == square) {
            return Direction.NORTH;
        } else if (xDiff == -square) {
            return Direction.SOUTH;
        } else if (yDiff == square) {
            return Direction.EAST;
        } else if (yDiff == -square) {
            return Direction.WEST;
        }
        return Direction.NONE;

    }



}