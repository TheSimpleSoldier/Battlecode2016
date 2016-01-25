package team037.Units.SuperRush;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseTurret;

public class SuperRushTurret extends BaseTurret
{
    private boolean rushing = false;
    private MapLocation lastTarget = null;
    private MapLocation[] updatedLocs;
    private int currentIndex = -1;
    private int dist = Integer.MAX_VALUE;

    public SuperRushTurret(RobotController rc)
    {
        super(rc);
        updatedLocs = new MapLocation[enemyArchonStartLocs.length];

        for (int i = updatedLocs.length; --i>=0; )
        {
            updatedLocs[i] = enemyArchonStartLocs[i];
        }

        rushTarget.add(rushTarget.directionTo(currentLocation), 3);
        dist = (int) Math.sqrt(currentLocation.distanceSquaredTo(rushTarget));
        dist = dist / 2;
        dist = dist*dist;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (currentLocation != null && rushTarget != null)
        {
            if (currentLocation.distanceSquaredTo(rushTarget) < dist)
            {
                rushing = true;
            }
            else
            {
                rushing = false;
            }
        }
    }

    @Override
    public MapLocation getNextSpot()
    {
        currentIndex++;
        return rushTarget;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        return true;
    }
}
