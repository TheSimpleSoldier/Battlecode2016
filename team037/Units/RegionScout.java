package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class RegionScout extends BaseScout
{
    private int region = -1;

    public RegionScout(RobotController rc)
    {
        super(rc);
    }

    @Override
    public boolean act() throws GameActionException
    {
        if (fight()) return true;
        else if (exploreRegions()) {
            return true;
        }
        return false;
    }

    public boolean exploreRegions() throws GameActionException
    {
        if (mKnowledge.exporedAllRegions())
            return false;

        if (region >= 0 && !currentLocation.equals(locationLastTurn))
            mKnowledge.upDateExploredLocs(region, rc);

        if (region == -1 || mKnowledge.regionExplored(region))
        {
            region = mKnowledge.closestUnexploredRegion(currentLocation);
        }

        if (region >= 0 && mKnowledge.exploredRegions[region])
        {
            region = -1;
        }

        if (region >= 0 && !mKnowledge.exploredRegions[region])
        {
            move.setTarget(mKnowledge.nxtUnexploredSquare(region));
        }

        move.takeNextStep();

        return true;
    }
}
