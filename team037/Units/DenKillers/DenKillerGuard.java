package team037.Units.DenKillers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.Units.BaseUnits.BaseGaurd;

public class DenKillerGuard extends BaseGaurd
{
    public DenKillerGuard(RobotController rc)
    {
        super(rc);
    }

    @Override
    public void sendMessages() throws GameActionException
    {
        if (mapKnowledge.denLocations.contains(currentLocation))
        {
            mapKnowledge.denLocations.remove(currentLocation);
            rc.broadcastSignal(2500);
        }
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (rc.getRoundNum() % 5 == 0)
        {
            mapKnowledge.updateDens(rc);
            if (mapKnowledge.denLocations.hasLocations())
            {
                navigator.setTarget(mapKnowledge.closestDen(currentLocation));
            }
        }
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation goal = navigator.getTarget();
        if (goal == null) return true;
        if (currentLocation.equals(goal)) return true;
        if (rc.canSenseLocation(goal) && (rc.senseRobotAtLocation(goal) == null || rc.senseRobotAtLocation(goal).team != Team.ZOMBIE || !rc.onTheMap(goal))) return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot()
    {
        if (mapKnowledge.denLocations.hasLocations())
            return mapKnowledge.closestDen(currentLocation);

        return null;
    }
}
