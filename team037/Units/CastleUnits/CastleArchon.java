package team037.Units.CastleUnits;

import battlecode.common.*;
import team037.CastleNavigator;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.Enums.Bots;
import team037.SlugNavigator;
import team037.Unit;
import team037.Units.BaseUnits.BaseArchon;
import team037.Utilites.MapUtils;
import team037.Utilites.PartsUtilities;

/**
 * Archon extending BaseArchon experimented with fixed formations and uniform movement.
 */
public class CastleArchon extends BaseArchon {

    private SlugNavigator move;
    private CastleNavigator castleMove;
    private MapLocation home;
    private int numSoliders = 0;
    private boolean soldiersFiring = false;
    private boolean soldiersDamaged = false;
    private int numTurrets = 0;
    private Direction lastSpawn;
    private int waitTill;

    private static final String MOVE_AWAY = "move away from archons";
    private static final String MOVE_AWAY_BIG = "move away from archons";
    private static final String SPAWN = "spawning a unit";
    private static final String WAIT = "waiting for new spawn";

    private String lastAction;
    private int lastMove = 0;

    public CastleArchon(RobotController rc) {
        super(rc);
        move = new SlugNavigator(rc);
        castleMove = new CastleNavigator(this);
        lastSpawn = MapUtils.randomDirection(id, 0);
    }


    @Override
    public Unit getNewStrategy(Unit current) {
        return current;
    }

    @Override
    public boolean act() throws GameActionException {

        // see how the soldiers are doing
        countFriendlyTypes();

        // scan for parts
        scanForParts();

        // don't affect core ready
        healNearbyAllies();

        // actions that affect core
        if (!rc.isCoreReady()) {
            rc.setIndicatorString(0, "cooldown " + round);
            return false;
        } else if (moveAwayFromBigZombies()) {
            lastAction = MOVE_AWAY_BIG;
        } else if (moveAwayFromFriendlyArchons()) {
            lastAction = MOVE_AWAY;
        } else if (spawnIfNeeded()) {
            lastAction = SPAWN;
        } else if (waitIfNeeded()) {
            lastAction = WAIT;
        } else if (moveToParts()) {
            lastAction = "move to parts";
        } else if (randomMove()) {
            lastAction = "random move";
        }
        rc.setIndicatorString(0, lastAction + " " + round);
        return true;
    }

    private void move(Direction d) throws GameActionException {
        lastMove = round;
        rc.move(d);
    }

    public boolean healNearbyAllies() throws GameActionException {
        // precondition
        if (nearByAllies.length == 0) {
            return false;
        }

        double weakestHealth = 9999;
        RobotInfo weakest = null;

        for (int i = nearByAllies.length; --i>=0; )
        {
            double health = nearByAllies[i].health;
            if (nearByAllies[i].type != RobotType.ARCHON && health < nearByAllies[i].maxHealth && currentLocation.distanceSquaredTo(nearByAllies[i].location) <= RobotType.ARCHON.attackRadiusSquared)
            {
                if (health < weakestHealth)
                {
                    weakestHealth = health;
                    weakest = nearByAllies[i];
                }
            }
        }

        if (weakest != null)
        {
            rc.repair(weakest.location);
            return true;
        }
        return false;
    }

    private boolean moveToParts() throws GameActionException {
        if (castleMove.getTarget() == null || round - lastMove < 3) {
            return false;
        }

        Direction toMove = castleMove.getNextDirToTarget();
        if (castleMove.canMove(toMove)) {
            move(toMove);
            return true;
        }
        return false;
    }


    private void scanForParts() {
        AppendOnlyMapLocationArray parts = PartsUtilities.findPartsAndNeutralsICanSense(rc);
        if (currentLocation.equals(castleMove.getTarget())) {
            castleMove.setTarget(null);
        }
        if (parts.length > 0 && castleMove.getTarget() == null) {
            castleMove.setTarget(parts.array[0]);
        }
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

    private boolean moveAwayFromBigZombies() throws GameActionException {
        // prereq
        if (nearByZombies.length == 0 || round - lastMove < 3) {
            return false;
        }
        AppendOnlyMapLocationArray bigZombieLoc = new AppendOnlyMapLocationArray();
        for (int i = nearByZombies.length; --i >= 0;) {
            if (nearByZombies[i].type.equals(RobotType.BIGZOMBIE)) {
                bigZombieLoc.add(nearByZombies[i].location);
            }
        }

        if (bigZombieLoc.length == 0) {
            return false;
        }

        Direction toMove = bigZombieLoc.array[0].directionTo(currentLocation);
        if (castleMove.canDangerousMove(toMove)) {
            move(toMove);
            return true;
        }
        return false;
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
            toMove = MapUtils.randomDirection(id, round);
        }

        MapLocation newDest = MapUtils.findOnMapLocationNUnitsAway(this, toMove, 4);

        return newDest;
    }


    private boolean randomMove() throws GameActionException {
        // prereqs
        if (soldiersFiring || soldiersDamaged || round - lastMove < 3) {
            return false;
        }

        Direction rand = MapUtils.randomDirection(id, round);
        if (rc.canMove(rand) && castleMove.canMove(rand)) {
            move(rand);
            return true;
        }
        return false;
    }


    private Direction nextSpawnDir() {
        lastSpawn = lastSpawn.opposite().rotateLeft();
        if (rc.canBuild(Direction.NORTH, RobotType.SOLDIER)) {
            return Direction.NORTH;
        }
        return MapUtils.getRCCanMoveDirection();
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

    private boolean trySpawn(Direction d, RobotType toSpawn, Bots botType) throws GameActionException {
        if (!d.equals(Direction.NONE) && rc.canBuild(d, toSpawn)) {
            rc.build(d, toSpawn);
            sendInitialMessages(d, toSpawn, botType, false);
            return true;
        }
        return false;
    }


    private boolean spawnIfNeeded() throws GameActionException {
        // prereqs
        if (rc.getTeamParts() < RobotType.SOLDIER.partCost) {
            rc.setIndicatorString(1, "RETURN FALSE WE DON'T HAVE THE PARSE " + round);
            return false;
        }
        rc.setIndicatorString(1, "we have the parts " + round);
        // is it my "turn" to spawn?
        // we do this so that all archons get a turn to spawn
        // otherwise the one with the lowest ID almost always spawns first
        if ((id + round) % 2 == 0) {
            return false;
        }
        rc.setIndicatorString(1, "the right round " + round);

        if (numSoliders < 15 && rc.hasBuildRequirements(RobotType.SOLDIER)) {
            Direction spawnDir = nextSpawnDir();
            if (trySpawn(spawnDir, RobotType.SOLDIER, Bots.CASTLESOLDIER)) {
                waitTill = round + RobotType.SOLDIER.buildTurns + 4;
                rc.setIndicatorString(1, "spawn3d a soldier " + round);
                return true;
            }
        }

        if (numTurrets < 4 && rc.hasBuildRequirements(RobotType.TURRET) && round > 2000) {
            Direction spawnDir = nextTurretSpawnDir();
            if (trySpawn(spawnDir, RobotType.TURRET, Bots.CASTLETURRET)) {
                waitTill = round + RobotType.TURRET.buildTurns + 4;
                rc.setIndicatorString(1, "spawn3d a turret " + round);
                return true;
            }
        }

        rc.setIndicatorString(1, "HERE");

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
        soldiersDamaged = false;
        soldiersFiring = false;
        RobotInfo[] buddies = rc.senseNearbyRobots(8, us);
        for (int i = buddies.length; --i >= 0;) {
            if (buddies[i].type == RobotType.SOLDIER) {
                numS += 1;
                if (buddies[i].weaponDelay > buddies[i].coreDelay) {
                    soldiersFiring = true;
                }
                if (buddies[i].health < buddies[i].maxHealth) {
                    soldiersDamaged = true;
                }
            } else if (buddies[i].type == RobotType.TURRET || buddies[i].type == RobotType.TTM) {
                numT += 1;
            }
        }
        numSoliders = numS;
        numTurrets = numT;
    }
}