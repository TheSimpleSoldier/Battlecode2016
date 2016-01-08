package team037.Units;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.SlugNavigator;
import team037.Utilites.MapUtils;

public class CastleArchon extends BaseArchon {

    private SlugNavigator move;
    private MapLocation home;

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


        if (moveAwayFromFriendlyArchons()) ;
        else {
            return false;
        }
        return true;
    }


    private boolean moveAwayFromFriendlyArchons() throws GameActionException {
        // precondition
        if (home != null) {
            return false;
        }

        AppendOnlyMapLocationArray archons = getFriendlyArchonsInSight();

        if (archons.length == 0) {
            home = currentLocation;
            return false;
        }

        return moveAway(archons);
    }

    private AppendOnlyMapLocationArray getFriendlyArchonsInSight() {
        RobotInfo[] allies = nearByAllies;
        AppendOnlyMapLocationArray archonLocs = new AppendOnlyMapLocationArray();
        for (int i = allies.length; -- i >= 0;) {
            if (allies[i].type == RobotType.ARCHON) {
                archonLocs.add(allies[i].location);
            }
        }
        return archonLocs;
    }

    private boolean moveAway(AppendOnlyMapLocationArray archons) throws GameActionException {
        if (move.getTarget() == null || currentLocation.equals(move.getTarget())) {
            move.setTarget(getMoveAwayTarget(archons));
        }

        return move.takeNextStep();
    }

    private MapLocation getMoveAwayTarget(AppendOnlyMapLocationArray archons) throws GameActionException {

        int x = archons.array[0].x;
        int y = archons.array[0].y;
        for (int i = archons.length; --i > 0; ) {
            x += archons.array[i].x;
            y += archons.array[i].y;
        }
        MapLocation center = new MapLocation(x/archons.length, y/archons.length);
        Direction toMove = center.directionTo(currentLocation);
        if (toMove.equals(Direction.NONE) || toMove.equals(Direction.OMNI)) {
            toMove = MapUtils.randomDirection(id, rc.getRoundNum());
        }

        MapLocation newDest = MapUtils.findOnMapLocationNUnitsAway(this, toMove, 4);

        return newDest;
    }


    private boolean spawnIfNeeded() {
        if (rc.hasBuildRequirements(RobotType.SOLDIER)) {

        }
        return false;
    }


    private void countFriendlyTypes() {
        int numSoldiers = 0;
        int numTurrets = 0;
        for (int i = nearByAllies.length; --i > 0;) {

        }
    }

}