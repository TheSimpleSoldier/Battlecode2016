package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseArchon;

public class ScoutBombArchon extends BaseArchon
{
    public ScoutBombArchon(RobotController rc)
    {
        super(rc);
    }

    // currently not used for turtle archons
    @Override
    public boolean carryOutAbility() {
        return false;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        if (navigator.getTarget().equals(currentLocation)) {
            return true;
        }

    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        return sortedParts.getBestSpot(currentLocation);
    }

    @Override
    public Bots changeBuildOrder(Bots nextBot)
    {
        return Bots.SCOUTBOMBSCOUT;
    }
}
