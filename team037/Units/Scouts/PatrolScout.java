package team037.Units.Scouts;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseScout;

public class PatrolScout extends BaseScout
{
    public PatrolScout(RobotController rc)
    {
        super(rc);
    }

    public boolean carryOutAbility() throws GameActionException
    {
        if (target == null || rc.getLocation().distanceSquaredTo(target) <= 5) {
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

            move.setTarget(target);
        }
        return false;
    }
}
