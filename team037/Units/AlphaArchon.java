package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.Utilites.PartsUtilities;

public class AlphaArchon extends BaseArchon
{
//    SlugNavigator move;
    AppendOnlyMapLocationArray parts;
    int partsIdx;

    public AlphaArchon(RobotController rc)
    {
        super(rc);
//        move = new SlugNavigator(rc);
        parts = PartsUtilities.findPartsAndNeutralsICanSenseNotImpassible(rc);
        partsIdx = 0;
        navigator.setTarget(getNextPartLocation());
//        move.setTarget(getNextPartLocation());
    }

    private MapLocation getNextPartLocation() {
        if (partsIdx >= parts.length) {
            parts = PartsUtilities.findPartsAndNeutralsICanSenseNotImpassible(rc);
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
        if (rc.isCoreReady() && carryOutAbility()) {
            return true;
        }
        if (fight());
        else if (fightZombies());
        else if (carryOutAbility());
        if(rc.getLocation().equals(navigator.getTarget())) {
            navigator.setTarget(getNextPartLocation());
        }

        return navigator.takeNextStep();
    }
}
