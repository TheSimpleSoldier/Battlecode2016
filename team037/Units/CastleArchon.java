package team037.Units;

import battlecode.common.*;
import team037.CastleNavigator;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.SlugNavigator;
import team037.Utilites.MapUtils;

public class CastleArchon extends BaseArchon {

    private SlugNavigator move;
    private CastleNavigator castleMove;
    private MapLocation home;
    private int numSoliders = 0;
    private int numTurrets = 0;
    private Direction lastSpawn;
    private int waitTill;

    private static final String MOVE_AWAY = "move away from archons";
    private static final String SPAWN = "spawning a unit";
    private static final String WAIT = "waiting for new spawn";

    private String lastAction;

    public CastleArchon(RobotController rc) {
        super(rc);
        move = new SlugNavigator(rc);
        castleMove = new CastleNavigator(this);
        lastSpawn = MapUtils.randomDirection(id, 0);
    }


    @Override
    public boolean act() throws GameActionException {
        // don't affect core ready
        healNearbyAllies();

        // actions that affect core
        if (!rc.isCoreReady()) ;
        else if (moveAwayFromFriendlyArchons()) ;
        else if (spawnIfNeeded()) {
            lastAction = SPAWN;
        }
        else if (waitIfNeeded()) {
            lastAction = WAIT;
        }
        else {
            lastAction = "MOVE";
            Direction rand = MapUtils.randomDirection(id, rc.getRoundNum());
            if (rc.canMove(rand) && castleMove.canMove(rand)) {
                rc.move(rand);
            }
        }
        rc.setIndicatorString(0, lastAction);
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


    private Direction nextSpawnDir() {
        lastSpawn = lastSpawn.opposite().rotateLeft();
        if (rc.canBuild(Direction.NORTH, RobotType.SOLDIER)) {
            return Direction.NORTH;
        }
        return MapUtils.getRCCanMoveDirection(this);
    }

    private Direction nextTurretSpawnDir() {
        if (rc.canBuild(Direction.NORTH, RobotType.TURRET)) {
            return Direction.NORTH;
        } else if (rc.canBuild(Direction.SOUTH, RobotType.TURRET)) {
            return Direction.SOUTH;
        } else if (rc.canBuild(Direction.EAST, RobotType.TURRET)) {
            return Direction.EAST;
        } else if (rc.canBuild(Direction.WEST, RobotType.TURRET)) {
            return Direction.WEST;
        }
        return Direction.NONE;
    }

    private boolean trySpawn(Direction d, RobotType toSpawn) throws GameActionException {
        if (!d.equals(Direction.NONE) && rc.canBuild(d, toSpawn)) {
            rc.build(d, toSpawn);
            return true;
        }
        return false;
    }


    private boolean spawnIfNeeded() throws GameActionException {
        // prereqs
        if (rc.getTeamParts() < RobotType.SOLDIER.partCost) {
            return false;
        }
        // is it my "turn" to spawn?
        // we do this so that all archons get a turn to spawn
        // otherwise the one with the lowest ID almost always spawns first
        if ((id + rc.getRoundNum()) % 16 < 12) {
            return false;
        }


        countFriendlyTypes();
        if (numSoliders < 15 && rc.hasBuildRequirements(RobotType.SOLDIER)) {
            Direction spawnDir = nextSpawnDir();
            if (trySpawn(spawnDir, RobotType.SOLDIER)) {
                waitTill = round + RobotType.SOLDIER.buildTurns;
                return true;
            }
        }

        if (numTurrets < 4 && rc.hasBuildRequirements(RobotType.TURRET) && round > 2000) {
            Direction spawnDir = nextTurretSpawnDir();
            if (trySpawn(spawnDir, RobotType.TURRET)) {
                waitTill = round + RobotType.TURRET.buildTurns;
                return true;
            }
        }

        return false;
    }

    private boolean waitIfNeeded () {
        // we have a robot spawning, wait!
        if (round < waitTill) {
            return true;
        }
        return false;
    }


    private void countFriendlyTypes() {
        int numS = 0;
        int numT = 0;
        RobotInfo[] buddies = rc.senseNearbyRobots(8, us);
        for (int i = buddies.length; --i >= 0;) {
            if (buddies[i].type == RobotType.SOLDIER) {
                numS += 1;
            } else if (buddies[i].type == RobotType.TURRET || buddies[i].type == RobotType.TTM) {
                numT += 1;
            }
        }
        numSoliders = numS;
        numTurrets = numT;
    }




}