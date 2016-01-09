package team037.Units;

import battlecode.common.*;
import team037.SlugNavigator;
import team037.Utilites.MapUtils;

public class CastleSoldier extends BaseSoldier
{
    private MapLocation archonLoc;
    SlugNavigator move;

    public CastleSoldier(RobotController rc)
    {
        super(rc);
        move = new SlugNavigator(rc);
    }

    @Override
    public boolean act() throws GameActionException {
        if (!rc.isCoreReady()) {
            return false;
        }

        findMom();

        if (fight()) ;
        else if (moveToFront());
        else if (patrol());

        return false;
    }

    private void findMom() throws GameActionException {
        if (archonLoc != null) {
            return;
        }
        RobotInfo[] possible = rc.senseNearbyRobots(2, us);
        for (int i = possible.length; --i >= 0;) {
            if (possible[i].type == RobotType.ARCHON) {
                archonLoc = possible[i].location;
            }
        }
    }

    public boolean fight() throws GameActionException {
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
        if (currentLocation.distanceSquaredTo(archonLoc) >= 4) {
            return false;
        }

        if (move.getTarget() == null) {
            Direction toMove = archonLoc.directionTo(currentLocation);
            MapLocation target = MapUtils.findOnMapLocationNUnitsAway(this, toMove, 3);
            move.setTarget(target);
        }
        return move.moveAndClear();
    }

    private boolean patrol() throws GameActionException {
        int xDiff = currentLocation.x - archonLoc.x;
        int yDiff = currentLocation.y - archonLoc.y;

        int square = 2;
        if (xDiff == square && yDiff == square) {
            if (rc.canMove(Direction.NORTH)) {
                rc.move(Direction.NORTH);
                return true;
            }
            return false;
        } else if (xDiff == square && yDiff == -square) {
            if (rc.canMove(Direction.WEST)) {
                rc.move(Direction.WEST);
                return true;
            }
            return false;
        } else if (xDiff == -square && yDiff == -square) {
            if (rc.canMove(Direction.SOUTH)) {
                rc.move(Direction.SOUTH);
                return true;
            }
            return false;
        } else if (xDiff == -square && yDiff == square) {
            if (rc.canMove(Direction.EAST)) {
                rc.move(Direction.EAST);
                return true;
            }
            return false;
        } else if (xDiff == square) {
            if (rc.canMove(Direction.NORTH)) {
                rc.move(Direction.NORTH);
                return true;
            }
            return false;
        } else if (xDiff == -square) {
            if (rc.canMove(Direction.SOUTH)) {
                rc.move(Direction.SOUTH);
                return true;
            }
            return false;
        } else if (yDiff == square) {
            if (rc.canMove(Direction.EAST)) {
                rc.move(Direction.EAST);
                return true;
            }
            return false;
        } else if (yDiff == -square) {
            if (rc.canMove(Direction.WEST)) {
                rc.move(Direction.WEST);
                return true;
            }
            return false;
        }

        return false;

    }



}