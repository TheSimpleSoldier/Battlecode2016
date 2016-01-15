package team037.Units.DenKillers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.Utilites.MapUtils;
import team037.Units.BaseUnits.BaseSoldier;

public class DenKillerSoldier extends BaseSoldier
{
    public DenKillerSoldier(RobotController rc)
    {
        super(rc);
    }

    @Override
    public void sendMessages() throws GameActionException
    {
        if (mapKnowledge.dens.contains(currentLocation))
        {
            mapKnowledge.dens.remove(currentLocation);
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
            if (mapKnowledge.dens.hasLocations())
            {
                navigator.setTarget(MapUtils.getNearestLocation(mapKnowledge.dens.array, currentLocation));
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
        if (mapKnowledge.dens.hasLocations())
            return MapUtils.getNearestLocation(mapKnowledge.dens.array, currentLocation);

        return null;
    }
}
