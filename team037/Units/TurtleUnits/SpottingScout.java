package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Utilites.MapUtils;
import team037.Units.BaseUnits.BaseScout;

/**
 * Scout specialized at teaming with Turrets and assisting with long range targeting.
 */
public class SpottingScout extends BaseScout
{
    public MapLocation nxtTarget;

    public SpottingScout(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, enemyArchonStartLocs);
        rc.setIndicatorString(0, "Spotting scout x: " + turtlePoint.x + " y: " + turtlePoint.y);
        System.out.println("We have a spotting scout!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        nxtTarget = turtlePoint;
        int distFromTurtle = currentLocation.distanceSquaredTo(turtlePoint);

        if (distFromTurtle > 100) return true;

        Direction direction = null;
        boolean furtherAwayThanAllTurrets = true;

        for (int i = allies.length; --i>=0; )
        {
            if (allies[i].type == RobotType.TURRET)
            {
                MapLocation ally = allies[i].location;
                int allyDist = ally.distanceSquaredTo(turtlePoint);
                if (allyDist > distFromTurtle)
                {
                    furtherAwayThanAllTurrets = false;
                    if (currentLocation.distanceSquaredTo(ally) > (RobotType.SCOUT.sensorRadiusSquared - RobotType.TURRET.attackRadiusSquared))
                    {
                        direction = currentLocation.directionTo(ally);
                    }
                }
            }
        }

        if (furtherAwayThanAllTurrets)
        {
            nxtTarget = currentLocation.add(currentLocation.directionTo(turtlePoint), 20);
            return true;
        }
        else if (direction != null)
        {
            MapLocation nxt = currentLocation.add(direction);

            while (rc.canSense(nxt) && rc.senseRobotAtLocation(nxt) != null)
            {
                nxt = nxt.add(direction);
            }

            if (rc.canSense(nxt))
            {
                nxtTarget = nxt;
                return true;
            }
        }

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        return nxtTarget;
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
        }
    }
}
