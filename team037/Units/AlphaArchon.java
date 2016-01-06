package team037.Units;

import battlecode.common.*;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import team037.Unit;
import team037.SlugNavigator;
import team037.Utilites.PartsUtilities;
import team037.Utilites.AppendOnlyMapLocationArray;

import java.lang.Override;

public class AlphaArchon extends Unit
{
    SlugNavigator move;
    AppendOnlyMapLocationArray parts;
    int partsIdx;

    public AlphaArchon(RobotController rc)
    {
        super(rc);
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

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return false;
    }

    public boolean fightZombies() throws GameActionException
    {
        return false;
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }


    @Override
    public boolean act() throws GameActionException {
        if (!rc.isCoreReady()) {
            return false;
        }
        if(rc.getLocation().equals(move.getTarget())) {
            move.setTarget(getNextPartLocation());
        }
        move.takeNextStep();
        return true;
    }

    // maybe spawn a unit?
    public boolean carryOutAbility() throws GameActionException
    {
        if (rc.hasBuildRequirements(RobotType.SOLDIER) && rc.getCoreDelay() < 1)
        {
            for (int i = dirs.length; --i>=0; )
            {
                if (rc.canBuild(dirs[i], RobotType.SOLDIER))
                {
                    rc.build(dirs[i], RobotType.SOLDIER);
                }
            }
        }
        return false;
    }
}
