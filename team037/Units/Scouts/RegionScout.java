package team037.Units.Scouts;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseScout;

public class RegionScout extends BaseScout
{
    private int region = -1;

    public RegionScout(RobotController rc)
    {
        super(rc);
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (region >= 0 && !currentLocation.equals(locationLastTurn))
            mapKnowledge.upDateExploredLocs(region, rc);

        if (region >= 0 && mapKnowledge.exploredRegions[region])
            region = -1;

        if (region == -1 || mapKnowledge.regionExplored(region))
            region = mapKnowledge.closestUnexploredRegion(currentLocation);
    }

    @Override
    public MapLocation getNextSpot()
    {
        return mapKnowledge.nxtUnexploredSquare(region);
    }

    @Override
    public boolean updateTarget()
    {
        if (mapKnowledge.exporedAllRegions())
            return false;

        return (region >= 0 && !mapKnowledge.exploredRegions[region]);
    }
}
