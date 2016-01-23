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

    /**
     * This method returns the turtle location which is currently very dumb as the COM of starting allied archons
     *
     * @param alliedArchonStartLocs
     * @return
     */
    public static MapLocation getTurtleSpot(MapLocation[] alliedArchonStartLocs)
    {
        int x = 0, y = 0, len = alliedArchonStartLocs.length;

        for (int i = len; --i>=0; )
        {
            x += alliedArchonStartLocs[i].x;
            y += alliedArchonStartLocs[i].y;
        }

        return new MapLocation(x/len,y/len);
    }

    public static MapLocation getTurtleSpot2(MapLocation[] alliedArchonStartLocs, MapLocation[] enemyArchonStartLocs)
    {
        MapLocation enemyCOM = getCenterOfMass(enemyArchonStartLocs);

        int dist = 0;
        MapLocation best = null;

        for (int i = alliedArchonStartLocs.length; --i>=0; )
        {
            int currentDist = alliedArchonStartLocs[i].distanceSquaredTo(enemyCOM);

            if (currentDist > dist)
            {
                dist = currentDist;
                best = alliedArchonStartLocs[i];
            }
        }

        if (best == null)
        {
            best = getTurtleSpot(alliedArchonStartLocs);
        }

        return best;
    }

    public static MapLocation getCenterOfMass(MapLocation[] locations)
    {
        int x = 0;
        int y = 0;
        for(int k = locations.length; --k >= 0;)
        {
            x += locations[k].x;
            y += locations[k].y;
        }

        if(locations.length > 0)
        {
            return new MapLocation(x / locations.length, y / locations.length);
        }
        return null;
    }

    public static MapLocation getCenterOfRobotInfoMass(RobotInfo[] bots) {
        int x = 0;
        int y = 0;
        for(int k = bots.length; --k >= 0;)
        {
            x += bots[k].location.x;
            y += bots[k].location.y;
        }

        if(bots.length > 0)
        {
            return new MapLocation(x / bots.length, y / bots.length);
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

    /**
     * This method returns the closest location to a target that is unoccupied
     *
     * @param currentLoc
     * @return
     */
    public static MapLocation getClosestUnoccupiedSquare(MapLocation currentLoc, MapLocation target) throws GameActionException
    {
        // precondition
        if (currentLoc == null || target == null) return null;

        int dist = currentLoc.distanceSquaredTo(target);

        if (dist >= 49) return target;

        MapLocation closest = currentLoc;

        MapLocation[] getLocs = MapLocation.getAllMapLocationsWithinRadiusSq(target, dist);

        int closestDistToTarget = dist;

        for (int i = getLocs.length; --i>=0; )
        {
            MapLocation current = getLocs[i];

            if (!Unit.rc.canSenseLocation(current)) continue;
            if (!Unit.rc.onTheMap(current)) continue;
            if (Unit.rc.senseRubble(current) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) continue;

            int newDist = current.distanceSquaredTo(target);

            if (newDist < closestDistToTarget && Unit.rc.canSense(current) && Unit.rc.senseRobotAtLocation(current) == null)
            {
                closestDistToTarget = newDist;
                closest = current;
            }
        }

        return closest;
    }

    /**
     * This method returns the closest location to a target that is unoccupied
     *
     * @param currentLoc
     * @return
     */
    public static MapLocation getClosestUnoccupiedSquareCheckeredBoard(MapLocation currentLoc, MapLocation target) throws GameActionException
    {
        // precondition
        if (currentLoc == null || target == null) return null;

        if (Unit.rc.getTeam() == Team.A)
        {
            return getClosestUnoccupiedSquare(currentLoc, target);
        }

        int dist = currentLoc.distanceSquaredTo(target);

        if (dist >= 81)
        {
            Unit.rc.setIndicatorString(2, "returning target");
            return target;
        }

        MapLocation closest = currentLoc;

        MapLocation[] getLocs;

        int closestDistToTarget;
        boolean illegalSpot;

        if (currentLoc.x % 2 == currentLoc.y % 2)
        {
            closestDistToTarget = 999999;
            illegalSpot = true;
        }
        else
        {
            closestDistToTarget = dist;
            illegalSpot = false;
        }

        do {
            getLocs = MapLocation.getAllMapLocationsWithinRadiusSq(target, dist);

            for (int i = getLocs.length; --i>=0; )
            {
                MapLocation current = getLocs[i];

                if (current.x % 2 == current.y % 2) continue;
                if (!Unit.rc.canSenseLocation(current)) continue;
                if (!Unit.rc.onTheMap(current)) continue;
                if (Unit.rc.senseRubble(current) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) continue;

                int newDist = current.distanceSquaredTo(target);

                if (newDist < closestDistToTarget && Unit.rc.canSense(current) && Unit.rc.senseRobotAtLocation(current) == null)
                {
                    closestDistToTarget = newDist;
                    closest = current;
                }
            }


            dist *= 2;
        } while (closest.equals(currentLoc) && illegalSpot);


        if (closest.x % 2 == closest.y % 2 && !closest.equals(currentLoc))
        {
            System.out.println("Houston we have a problem");
        }
        else if (closest.equals(currentLoc) && currentLoc.x % 2 == currentLoc.y % 2)
        {
            System.out.println("Returning current loc");
        }

        return closest;
    }

    /**
     * This method returns true if the current location is nxt to an edge and false otherwise
     *
     * @param current
     * @return
     */
    public static boolean nextToEdge(MapLocation current, RobotController rc) throws GameActionException
    {
        for (int i = Unit.dirs.length; --i>=0; )
        {
            MapLocation next = current.add(Unit.dirs[i]);
            if (rc.canSense(next) && !rc.onTheMap(next))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * This method calculates the closest unit
     *
     * @param units
     * @return
     */
    public static MapLocation closestUnit(RobotInfo[] units, MapLocation currentLocation)
    {
        int dist = Integer.MAX_VALUE;
        MapLocation closest = null;

        for (int i = units.length; --i>=0; )
        {
            MapLocation spot = units[i].location;
            int currentDist = spot.distanceSquaredTo(currentLocation);
            if (currentDist < dist)
            {
                dist = currentDist;
                closest = units[i].location;
            }
        }

        return closest;
    }


    public static boolean canZombieMoveTowardMe(RobotController rc, MapLocation currentLocation, MapLocation zombieLocation) throws GameActionException {
        if (!rc.canSense(zombieLocation)) {
            return true;
        }
        Direction towardMe = zombieLocation.directionTo(currentLocation);
        MapLocation toCheck = zombieLocation.add(towardMe);

        if (rc.senseRobotAtLocation(toCheck) != null && rc.senseRubble(toCheck) < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
            return true;
        }

        toCheck = zombieLocation.add(towardMe.rotateLeft());
        if (rc.senseRobotAtLocation(toCheck) != null && rc.senseRubble(toCheck) < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
            return true;
        }

        toCheck = zombieLocation.add(towardMe.rotateLeft());
        if (rc.senseRobotAtLocation(toCheck) != null && rc.senseRubble(toCheck) < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
            return true;
        }
        return false;
    }

    public static Direction addDirections(Direction d1, Direction d2) {
        if (d1 == null || d2 == null) {
            return Direction.NONE;
        }
        int dx = d1.dx + d2.dx;
        int dy = d1.dy + d2.dy;
        if (dx > 0) {
            if (dy > 0) {
                return Direction.SOUTH_EAST;
            } else if (dy < 0) {
                return Direction.NORTH_EAST;
            } else {
                return Direction.EAST;
            }
        } else if (dx < 0) {
            if (dy > 0) {
                return Direction.SOUTH_WEST;
            } else if (dy < 0) {
                return Direction.NORTH_WEST;
            } else {
                return Direction.WEST;
            }
        } else {
            if (dy > 0) {
                return Direction.SOUTH;
            } else if (dy < 0) {
                return Direction.NORTH;
            } else {
                return Direction.NONE;
            }
        }
    }
}
