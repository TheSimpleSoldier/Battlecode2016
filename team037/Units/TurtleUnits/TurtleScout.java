package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.MapUtils;

public class TurtleScout extends BaseScout
{
    // This determines if the scout is going left or right around the turtle location
    private static boolean goingLeft = false;
    // this is the minimum distance that a scout will keep between it and the turtle center at all times
    private static final int minDist = 25;
    // this is the max distance that a scout will keep between it and the turtle center
    private static final int maxDist = 49;
    private static int directionUpdateTurn = 0;

    public TurtleScout(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);
        rc.setIndicatorString(0, "Turtle scout x: " + turtlePoint.x + " y: " + turtlePoint.y);
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();

        if (target == null) return true;
        if (currentLocation.equals(target)) return true;
        if (rc.canSense(target) && !rc.onTheMap(target)) return true;
        if (rc.canSense(target) && rc.senseRobotAtLocation(target) != null) return true;
        if (currentLocation.distanceSquaredTo(target) < minDist) return true;
        if (currentLocation.distanceSquaredTo(target) > maxDist) return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (currentLocation.distanceSquaredTo(turtlePoint) < minDist)
        {
            // set target to run away from the turtle point
            return currentLocation.add(turtlePoint.directionTo(currentLocation), 100);
        }

        if (currentLocation.distanceSquaredTo(turtlePoint) > maxDist)
        {
            return turtlePoint;
        }

        if (MapUtils.nextToEdge(currentLocation, rc) && rc.getRoundNum() - directionUpdateTurn > 5)
        {
            goingLeft = !goingLeft;
            directionUpdateTurn = rc.getRoundNum();
        }

        Direction goTo = currentLocation.directionTo(turtlePoint);

        if (goTo != null)
        {
            if (goingLeft)
            {
                goTo = goTo.rotateLeft().rotateLeft();
            }
            else
            {
                goTo = goTo.rotateRight().rotateRight();
            }

            return currentLocation.add(goTo, 100);
        }

        return null;
    }

    @Override
    public boolean fight() throws GameActionException
    {
        return false;
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (zombies.length > 0)
        {
            rc.setIndicatorString(1, "");

            // if enemies can attack us then try to move to get them to chase us while the turrets blast them
            if (fightMicro.EnemiesInRangeOfLoc(currentLocation, zombies))
            {
                rc.setIndicatorString(1, "running away");
                Direction goTo = currentLocation.directionTo(turtlePoint);
                Direction left = goTo.rotateLeft().rotateLeft();
                Direction right = goTo.rotateRight().rotateRight();

//                if (rc.canMove(left) && !fightMicro.EnemiesInRangeOfLoc(currentLocation.add(left), zombies))
//                {
//                    rc.move(left);
//                    return true;
//                }
//
//                if (rc.canMove(right) && !fightMicro.EnemiesInRangeOfLoc(currentLocation.add(right), zombies))
//                {
//                    rc.move(right);
//                    return true;
//                }

                if (rc.canMove(left) && fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(left), zombies) < fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(right), zombies))
                {
                    rc.move(left);
                    return true;
                }

                if (rc.canMove(right) && fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(left), zombies) > fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(right), zombies))
                {
                    rc.move(right);
                    return true;
                }
            }

            return true;
        }


        return false;
    }
}
