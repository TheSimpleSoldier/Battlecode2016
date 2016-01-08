package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.SlugNavigator;
import team037.Utilites.PartsUtilities;

public class AlphaArchon extends BaseArchon
{
    SlugNavigator move;
    AppendOnlyMapLocationArray parts;
    int partsIdx;

    public AlphaArchon(RobotController rc)
    {
        super(rc);
        rc.setIndicatorString(2, "Alpha Archon");
        move = new SlugNavigator(rc);
        parts = PartsUtilities.findPartsICanSenseNotImpassible(rc);
        partsIdx = 0;
        move.setTarget(getNextPartLocation());
    }

    private MapLocation getNextPartLocation() {
        if (partsIdx >= parts.length) {
            parts = PartsUtilities.findPartsICanSenseNotImpassible(rc);
        }
        if (parts.length == 0) {
            return null;
        }
        MapLocation nextParts = parts.array[partsIdx];
        partsIdx += 1;
        return nextParts;
    }

    @Override
    public boolean act() throws GameActionException {
        if (!rc.isCoreReady()) {
            return false;
        }
        if (carryOutAbility());
        else if(rc.getLocation().equals(move.getTarget())) {
            MapLocation next = getNextPartLocation();
            if (next != null) {
                move.setTarget(getNextPartLocation());
            }
        }
        move.takeNextStep();
        return true;
    }
}
