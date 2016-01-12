package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.Utilites.PartsUtilities;

public class AlphaArchon extends BaseArchon
{

    public AlphaArchon(RobotController rc)
    {
        super(rc);
        navigator.setTarget(getNextPartLocation());
    }

    private MapLocation getNextPartLocation() {
        MapLocation best = sortedParts.getBestSpot();
        return best;
    }

    @Override
    public boolean act() throws GameActionException {
        if (rc.isCoreReady() && carryOutAbility()) {
            return true;
        }
        if (fight());
        else if (fightZombies());
        else if (carryOutAbility());
        if(updateTarget()) {
            navigator.setTarget(getNextPartLocation());
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
        if (rc.canSenseLocation(currentTarget) && (rc.senseParts(currentTarget) == 0 && rc.senseRobotAtLocation(currentTarget) == null))
            return true;
        if (rc.canSenseLocation(currentTarget) && (rc.senseRobotAtLocation(currentTarget) != null && rc.senseRobotAtLocation(currentTarget).team != Team.NEUTRAL))
            return true;

        return false;
    }
}
