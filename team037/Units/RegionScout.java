package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class RegionScout extends BaseScout
{
    private int region = -1;


    public RegionScout(RobotController rc)
    {
        super(rc);
        rc.setIndicatorString(0, "Region Scout");
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
        if (mapKnowledge.exporedAllRegions())
            return false;

        if (region >= 0 && !currentLocation.equals(locationLastTurn))
            mapKnowledge.upDateExploredLocs(region, rc);

        if (region == -1 || mapKnowledge.regionExplored(region))
        {
            region = mapKnowledge.closestUnexploredRegion(currentLocation);
        }

        if (region >= 0 && mapKnowledge.exploredRegions[region])
        {
            region = -1;
        }

        if (region >= 0 && !mapKnowledge.exploredRegions[region])
        {
            move.setTarget(mapKnowledge.nxtUnexploredSquare(region));
        }

        move.takeNextStep();

        return true;
    }
}