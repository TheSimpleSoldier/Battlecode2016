package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.ZombieSpawnSchedule;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Units.BaseUnits.BaseArchon;
import team037.Utilites.Utilities;

public class AlphaArchon extends BaseArchon
{

    public AlphaArchon(RobotController rc)
    {
        super(rc);
        navigator.setTarget(getNextPartLocation());
        ZombieSpawnSchedule schedule = rc.getZombieSpawnSchedule();


    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation currentTarget = navigator.getTarget();
        if (currentTarget == null)
            return true;
        if (rc.getLocation().equals(currentTarget))
            return true;
        if (rc.canSenseLocation(currentTarget) && (rc.senseParts(currentTarget) == 0 && rc.senseRobotAtLocation(currentTarget) == null)) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentTarget));
            return true;
        }

        MapLocation bestParts = sortedParts.getBestSpot(currentLocation);

        if (!bestParts.equals(currentTarget))
            return true;

        return false;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        return getNextPartLocation();
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (sortedParts.contains(currentLocation))
        {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
            Communication communication = new BotInfoCommunication();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
            communicator.sendCommunication(400, communication);
        }
    }
}
