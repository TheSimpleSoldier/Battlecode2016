package team037.Units;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.SlugNavigator;
import team037.Utilites.MapUtils;

public class CastleArchon extends BaseArchon {

    private SlugNavigator move;
    private MapLocation home;
    private int numSoliders = 0;
    private int numTurrets = 0;

    public CastleArchon(RobotController rc) {
        super(rc);
        move = new SlugNavigator(rc);
    }

    @Override
    public boolean takeNextStep() throws GameActionException {
        return false;
    }

    @Override
    public boolean act() throws GameActionException {
        // preconditions
        if (!rc.isCoreReady()) {
            return false;
        }


        if (moveAwayFromFriendlyArchons()) ;
        else if (spawnIfNeeded()) ;
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
        for (int i = allies.length; --i >= 0;) {
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


    private boolean spawnIfNeeded() throws GameActionException {
        // prereqs
        if (rc.getTeamParts() < RobotType.SOLDIER.partCost) {
            return false;
        }
        countFriendlyTypes();
        if (numSoliders < 10 && rc.hasBuildRequirements(RobotType.SOLDIER)) {
            Direction spawnDir = MapUtils.getRCCanMoveDirection(this);
            if (!spawnDir.equals(Direction.NONE) && rc.canBuild(spawnDir, RobotType.SOLDIER)) {
                rc.build(spawnDir, RobotType.SOLDIER);
                return true;
            }
        }

        if (numTurrets < 4 && rc.hasBuildRequirements(RobotType.TURRET)) {
            Direction spawnDir = MapUtils.getRCCanMoveDirection(this);
            if (!spawnDir.equals(Direction.NONE) && rc.canBuild(spawnDir, RobotType.TURRET)) {
                rc.build(spawnDir, RobotType.TURRET);
                return true;
            }
        }

        if (numSoliders < 20 && rc.hasBuildRequirements(RobotType.SOLDIER)) {
            Direction spawnDir = MapUtils.getRCCanMoveDirection(this);
            if (!spawnDir.equals(Direction.NONE) && rc.canBuild(spawnDir, RobotType.SOLDIER)) {
                rc.build(spawnDir, RobotType.SOLDIER);
                return true;
            }
        }
        return false;
    }


    private void countFriendlyTypes() {
        int numS = 0;
        int numT = 0;
        for (int i = nearByAllies.length; --i >= 0;) {
            if (nearByAllies[i].type == RobotType.SOLDIER) {
                numS += 1;
            } else if (nearByAllies[i].type == RobotType.TURRET || nearByAllies[i].type == RobotType.TTM) {
                numT += 1;
            }
        }
        numSoliders = numS;
        numTurrets = numT;
    }


    private boolean healBuddies() {


        return false;
    }

}