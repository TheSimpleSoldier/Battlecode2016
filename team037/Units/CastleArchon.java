package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.SlugNavigator;

public class CastleArchon extends BaseArchon {

    private SlugNavigator move;
    private boolean movedAway = false;

    public CastleArchon(RobotController rc) {
        super(rc);
        move = new SlugNavigator(rc);
    }

    @Override
    public boolean act() throws GameActionException {
        // preconditions
        if (!rc.isCoreReady()) {
            return false;
        }
        if (moveAwayFromFriendlyArchons()) {
            return true;
        }
        return false;
    }


    private boolean moveAwayFromFriendlyArchons() {
        // precondition
        if (movedAway) {
            return false;
        }

        AppendOnlyMapLocationArray archons = getFriendlyArchonsInSight();

        if (archons.length == 0) {
            movedAway = true;
            return false;
        }

        return moveAway(archons);
    }

    private AppendOnlyMapLocationArray getFriendlyArchonsInSight() {
        return null;
    }

    private boolean moveAway(AppendOnlyMapLocationArray archons) {
        int x = archons.array[0].x;
        int y = archons.array[0].y;
        for (int i = archons.length - 1; i > 0; i--) {
            x += archons.array[i].x;
            y += archons.array[i].y;
        }
        MapLocation center = new MapLocation(x/archons.length, y/archons.length);
        return false;
    }

}