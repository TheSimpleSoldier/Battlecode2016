package team037.Units.Scouts;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseScout;

/**
 * Scout designed to stay near allied units.
 */
public class PatrolScout extends BaseScout
{
    public PatrolScout(RobotController rc)
    {
        super(rc);
    }

    @Override
    public MapLocation getNextSpot()
    {
        double longestDist = 5;
        for (int i = allies.length; --i >=0; ) {
            MapLocation ally = allies[i].location;
            double dist = rc.getLocation().distanceSquaredTo(ally);
            if (dist > longestDist)
            {
                longestDist = dist;
                target = ally;
            }
        }

        return target;
    }

    @Override
    public boolean updateTarget()
    {
        MapLocation target = navigator.getTarget();

        return (target == null || rc.getLocation().distanceSquaredTo(target) <= 5);
    }
}
