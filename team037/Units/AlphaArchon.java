package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Utilites.PartsUtilities;
import team037.Utilites.Utilities;

public class AlphaArchon extends BaseArchon
{

    public AlphaArchon(RobotController rc)
    {
        super(rc);
        navigator.setTarget(getNextPartLocation());
    }

    @Override
    public boolean act() throws GameActionException {
        if (rc.isCoreReady() && carryOutAbility()) {
            return true;
        }

        if (sortedParts.contains(currentLocation))
        {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
            Communication communication = new BotInfoCommunication();
                communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
                communicator.sendCommunication(400, communication);
        }

        if (fight());
        else if (fightZombies());
        else if (carryOutAbility());
        if(updateTarget()) {
            navigator.setTarget(getNextPartLocation());
        }

        if (navigator.getTarget() != null)
        {
            rc.setIndicatorString(0, "x: " + navigator.getTarget().x + " y: " + navigator.getTarget().y  + " Round numb: " + rc.getRoundNum());
        }
        else
        {
            rc.setIndicatorString(0, "null");
        }

        return navigator.takeNextStep();
    }

    /**
     * This method determines if we should update our target or not
     *
     * @return
     * @throws GameActionException
     */
    private boolean updateTarget() throws GameActionException
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
}
