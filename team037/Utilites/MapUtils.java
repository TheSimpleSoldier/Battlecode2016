package team037.Utilites;

import battlecode.common.*;
import battlecode.common.GameConstants;
import team037.Unit;


public class MapUtils
{
    /**
     * Senses the edge in the direction specified
     */
    public static int senseEdge(RobotController rc, Direction d) throws GameActionException {
        if (d == null || d.isDiagonal()) {
            return Integer.MIN_VALUE;
        }

        RobotType type = rc.getType();
        int sensorRadiusSquared = type.sensorRadiusSquared;
        int radius = (int)Math.floor(Math.sqrt(sensorRadiusSquared));

        MapLocation current = rc.getLocation();

        if (rc.onTheMap(current.add(d, radius))) {
            return Integer.MIN_VALUE;
        }

        radius -= 1;
        while(!rc.onTheMap(current.add(d, radius))) {
            radius -= 1;
        }

        if (d.equals(Direction.EAST) || d.equals(Direction.WEST)) {
            return current.add(d, radius).x;
        } else {
            return current.add(d, radius).y;
        }
    }

    /**
     * Senses furthest point on map
     */
    public static int senseFarthest(Direction d) throws GameActionException
    {
        if (d.isDiagonal()) {
            return Integer.MIN_VALUE;
        }

        RobotType type = Unit.rc.getType();
        int sensorRadiusSquared = type.sensorRadiusSquared;
        int radius = (int)Math.floor(Math.sqrt(sensorRadiusSquared));

        MapLocation current = Unit.rc.getLocation();

        if (Unit.rc.onTheMap(current.add(d, radius))) {
            if (d.equals(Direction.EAST) || d.equals(Direction.WEST)) {
                return current.add(d, radius).x;
            } else {
                return current.add(d, radius).y;
            }
        }

        return senseEdge(Unit.rc, d);
    }

    /**
     * Do you want your unit to move X squares in a direction?
     * Do you need to have a MapLocation in that direction?
     * This will get you a MapLocation ON THE MAP!
     *  NOTE: we can only move to squares you can sense, so X cannot be very large
     * @throws GameActionException
     */
    public static MapLocation findOnMapLocationNUnitsAway(Unit unit, Direction d, int toMove) throws GameActionException {
        MapLocation current = unit.currentLocation;
        for (int i = toMove; --i >= 0;) {
            if (!Unit.rc.canSenseLocation(current)) {
                break;
            }
            MapLocation toTry = current.add(d);
            if (Unit.rc.onTheMap(toTry)) {
                current = toTry;
            } else if (Unit.rc.onTheMap(current.add(d.rotateLeft()))) {
                current = current.add(d.rotateLeft());
            } else if (Unit.rc.onTheMap(current.add(d.rotateRight()))) {
                current = current.add(d.rotateRight());
            } else if (Unit.rc.onTheMap(current.add(d.rotateLeft().rotateLeft()))) {
                current = current.add(d.rotateLeft().rotateLeft());
            } else if (Unit.rc.onTheMap(current.add(d.rotateRight().rotateRight()))) {
                current = current.add(d.rotateRight().rotateRight());
            } else if (Unit.rc.onTheMap(current.add(d.rotateLeft().rotateLeft().rotateLeft()))) {
                current = current.add(d.rotateLeft().rotateLeft().rotateLeft());
            } else if (Unit.rc.onTheMap(current.add(d.rotateRight().rotateRight().rotateRight()))) {
                current = current.add(d.rotateRight().rotateRight().rotateRight());
            }
        }
        return current;
    }

    public static Direction randomDirection(int id, int roundNumber) {
        Direction toReturn;

        switch((id + roundNumber) % 8) {
            case 0:
                toReturn = Direction.NORTH;
                break;
            case 1:
                toReturn =  Direction.NORTH_EAST;
                break;
            case 2:
                toReturn = Direction.EAST;
                break;
            case 3:
                toReturn = Direction.SOUTH_EAST;
                break;
            case 4:
                toReturn = Direction.SOUTH;
                break;
            case 5:
                toReturn = Direction.SOUTH_WEST;
                break;
            case 6:
                toReturn = Direction.EAST;
                break;
            case 7:
                toReturn = Direction.NORTH_WEST;
                break;
            default:
                toReturn = Direction.NORTH;
                break;
        }
        return toReturn;
    }


    /**
     * returns a direction that this unit can move in
     * @param unit
     * @return
     */
    public static Direction getRCCanMoveDirection(Unit unit) {
        Direction toMove = randomDirection(unit.id, unit.rc.getRoundNum());
        int i = 8;
        do {
            if (unit.rc.canMove(toMove)) {
                break;
            }
            toMove = toMove.rotateLeft();
        } while (--i >= 0);

        if (i <= 0) {
            return Direction.NONE;
        }

        return toMove;
    }

    public static MapLocation getCenterOfMass(MapLocation[] locations)
    {
        int x = 0;
        int y = 0;
        int count = 0;
        for(int k = locations.length; --k >= 0;)
        {
            count++;
            x += locations[k].x;
            y += locations[k].y;
        }

        if(count > 0)
        {
            return new MapLocation(x / count, y / count);
        }
        return null;
    }

    public static MapLocation getNearestLocation(MapLocation[] locations, MapLocation location)
    {
        double nearestDist = Integer.MAX_VALUE;
        MapLocation nearest = null;
        for(int k = locations.length; --k >= 0;)
        {
            if(locations[k] != null)
            {
                double tempDist = location.distanceSquaredTo(locations[k]);
                if(tempDist < nearestDist)
                {
                    nearest = locations[k];
                    nearestDist = tempDist;
                }
            }
        }

        return nearest;
    }

}
