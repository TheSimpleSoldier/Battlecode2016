package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.MapUtils;

public class TurtleScout extends BaseScout
{
    // This determines if the scout is going left or right around the turtle location
    private static boolean goingLeft = false;
    // this is the minimum distance that a scout will keep between it and the turtle center at all times
    private static final int minDist = 5;
    // this is the max distance that a scout will keep between it and the turtle center
    private static final int maxDist = 6;
    private static int directionUpdateTurn = 0;
    private static int furthestTurret = 0;

    public TurtleScout(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, enemyArchonStartLocs);
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
        if (currentLocation.distanceSquaredTo(target) < getMinDist()) return true;
        if (currentLocation.distanceSquaredTo(target) > getMaxDist()) return true;

        return false;
    }

    /**
     * This method gets the closest dist to the turtle point that the scout should be
     * @return
     */
    public int getMinDist()
    {
        int newMinDist = (int) ((minDist + (furthestTurret * 0.75)) * (minDist + (furthestTurret * 0.75)) - 1);
        rc.setIndicatorString(0, "MinDist: " + newMinDist);
        return newMinDist;
    }

    /**
     * This method returns the furthest distance a turtle scout should ever be from the
     * turtle point
     * @return
     */
    public int getMaxDist()
    {
        int newMaxDist = ((maxDist + furthestTurret) * (maxDist + furthestTurret) + 1);
        rc.setIndicatorString(2, "MaxDist: " + newMaxDist);
        return newMaxDist;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (currentLocation.distanceSquaredTo(turtlePoint) < getMinDist())
        {
            // set target to run away from the turtle point
            return currentLocation.add(turtlePoint.directionTo(currentLocation), 100);
        }

        if (currentLocation.distanceSquaredTo(turtlePoint) > getMaxDist())
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
    public void sendMessages() throws GameActionException
    {
        // override default behavior
        msgTurrets();
        msgParts();
        msgDens();
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

                if (fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation, zombies) == 0)
                {
                    return true;
                }

                if (rc.canMove(left) && fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(left), zombies) < fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(right), zombies))
                {
                    rc.move(left);
                    return true;
                }
                else if (rc.canMove(right))
                {
                    rc.move(right);
                    return true;
                }
                else if (rc.canMove(left))
                {
                    rc.move(left);
                    return true;
                }
                else if (rc.canMove(goTo.rotateLeft()))
                {
                    rc.move(goTo.rotateLeft());
                    return true;
                }
                else if (rc.canMove(goTo.rotateRight()))
                {
                    rc.move(goTo.rotateRight());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (rallyPoint != null)
        {
            rc.setIndicatorLine(currentLocation, rallyPoint, 0, 0, 255);
            rc.setIndicatorString(1, "Going to new rally point");
            turtlePoint = rallyPoint;
            furthestTurret = 0;
        }

        // furthest turret dist should degrade if we don't have any turrets in vision
        if (rc.getRoundNum() % 10 == 0 && furthestTurret > 0)
        {
            furthestTurret--;
        }

        for (int i = allies.length; --i>=0;)
        {
            if (allies[i].type == RobotType.TURRET)
            {
                int newDist = (int) Math.sqrt(allies[i].location.distanceSquaredTo(turtlePoint));

                if (newDist > furthestTurret)
                {
                    furthestTurret = newDist;
                }
            }
        }
    }
}
