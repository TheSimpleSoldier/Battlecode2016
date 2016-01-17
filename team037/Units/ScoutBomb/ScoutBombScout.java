package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.FlyingSlugNavigator;
import team037.ScoutMapKnowledge;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.MapUtils;

public class ScoutBombScout extends BaseScout
{
    private static int closestEnemy;
    private static MapLocation closestEnemyLoc;
    private static int closestAlliedArchon;
    private static MapLocation closestAlliedArchonLoc;
    private static int closestZombie;
    private static MapLocation closestZombieLoc;
    private static RobotInfo closestZombieInfo;
    private static int possibleEnemyDamageNextTurn;
    private static int idxForEnemyLocations = 0;
    private static MapLocation enemyCore;
    private static boolean nonDenZombies = false;

    private static final int MIN_AVOID_DIST = (int)(RobotType.RANGEDZOMBIE.attackRadiusSquared * 1.5);


    private static final int ZERO_MELEE_HERD_DIST = 2;
    private static final int MIN_MELEE_HERD_DIST = 4;
    private static final int MAX_MELEE_HERD_DIST = 8;
    private static final int MELEE_MOVE_IN_HERD_DIST = 16; // range at which is it safe to close gap with melee enemy
    private static final int ZERO_RANGED_HERD_DIST = 8;
    private static final int MIN_RANGED_HERD_DIST = 16;
    private static final int MAX_RANGED_HERD_DIST = 25;
    private static final int RANGED_MOVE_IN_HERD_DIST = 36;

    private static RobotInfo[] veryCloseZombies;



    private static final String HERD_AWAY_FROM_ARCHON = "herding away from allied archon";
    private static final String BRING_ZOMBIES_TO_ENEMEY = "bringing zombies to enemey";
    private static final String FIND_ENEMIES = "finding enemies";
    private static final String HERD_AND_FIND_ENEMIES = "herding and finding enemies";
    private static final String MOVE_INTO_POSITION_AROUND_ENEMEY = "move into position around enemey";
    private static final String NOTHING = "NOTHING :(";

    private static String lastAction = NOTHING;


    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();

    public ScoutBombScout(RobotController rc)
    {
        super(rc);
        mapKnowledge = mKnowledge;
        navigator = new FlyingSlugNavigator(rc);
        navigator.setTarget(nextPlaceToLookForEnemies());
        enemyCore = MapUtils.getCenterOfMass(enemyArchonStartLocs);
    }


    @Override
    public void collectData() throws GameActionException {
        super.collectData();

        // zombie info
        // for now only look at close zombies
        veryCloseZombies = rc.senseNearbyRobots(RANGED_MOVE_IN_HERD_DIST, Team.ZOMBIE);
        nonDenZombies = false;
        closestZombie = Integer.MAX_VALUE;
        closestZombieLoc = null;
        for (int i = veryCloseZombies.length; --i >= 0;) {
            RobotInfo zombie = zombies[i];
            if (!zombie.type.equals(RobotType.ZOMBIEDEN)) {
                if (zombie.location.distanceSquaredTo(currentLocation) < closestZombie) {
                    closestZombie = zombie.location.distanceSquaredTo(currentLocation);
                    closestZombieLoc = zombie.location;
                    closestZombieInfo = zombie;
                }
                nonDenZombies = true;
            }
        }


        // enemy info
        possibleEnemyDamageNextTurn = 0;
        closestEnemy = Integer.MAX_VALUE;
        closestEnemyLoc = null;
        for (int i = enemies.length; --i>=0;) {
            int distance =  enemies[i].location.distanceSquaredTo(currentLocation);
            if (distance < closestEnemy) {
                closestEnemy = distance;
                closestEnemyLoc = enemies[i].location;
            }
        }

        // closestAlliedArchon
        closestAlliedArchon = Integer.MAX_VALUE;
        closestAlliedArchonLoc = null;
        for (int i = allies.length; --i>=0;) {
            if (allies[i].type.equals(RobotType.SCOUT)) {
                int distance =  allies[i].location.distanceSquaredTo(currentLocation);
                if (distance < closestAlliedArchon) {
                    closestAlliedArchon = distance;
                    closestAlliedArchonLoc = allies[i].location;
                }
            }
        }
    }

    @Override
    public void handleMessages() {
        return;
    }

    @Override
    public void sendMessages() {
        return;
    }

    @Override
    public boolean act() throws GameActionException {
        suicideIfNeeded();

        if (!rc.isCoreReady()) {
            rc.setIndicatorString(0, "core cooldown");
            return false;
        } else if (herdAwayFromArchon()) {
            // if you are near an allied archon and see zombies, help them!
            lastAction = HERD_AWAY_FROM_ARCHON;
        } else if (herdAndFindEnemy()) {
            lastAction = HERD_AND_FIND_ENEMIES;
        } else if (bringZombiesToEnemy()) {
            // if you are near the enemy and see a zombie, let's do it!
            lastAction = BRING_ZOMBIES_TO_ENEMEY;
        } else if (findEnemies()) {
            // if there are no enemies in sight, find them!
            lastAction = FIND_ENEMIES;
        } else if (moveInPositionAroundEnemey()) {
            lastAction = MOVE_INTO_POSITION_AROUND_ENEMEY;
        } else {
            lastAction = NOTHING;
            rc.setIndicatorString(0, "NOTHING! :(");
            return false;
        }
        rc.setIndicatorString(0, lastAction);
        return true;
    }

    private boolean herdAndFindEnemy() throws GameActionException {
        // precontidions
        if (!nonDenZombies) {
            return false;
        }

        int distToZombie = currentLocation.distanceSquaredTo(closestZombieLoc);
        RobotInfo friends = rc.senseRobotAtLocation(closestZombieLoc, distToZombie - 1);

        if (friends.)

    }

    public boolean heard(int distToZombie) throws GameActionException {
        if (distToZombie > MELEE_MOVE_IN_HERD_DIST) {
            rc.setIndicatorString(1, "moving to herd");
            if (rc.canMove(currentLocation.directionTo(closestZombieLoc))) {
                rc.move(currentLocation.directionTo(closestZombieLoc));
                return true;
            }
        } else if (distToZombie <= ZERO_MELEE_HERD_DIST && (closestZombieInfo.coreDelay <= 2 || closestZombieInfo.weaponDelay <= 2)) {
            rc.setIndicatorString(1, "moving away");
            Direction toMove = currentLocation.directionTo(navigator.getTarget());
            Direction away = closestZombieLoc.directionTo(currentLocation);
            toMove = MapUtils.getDirectionFromDxDy(toMove.dx + away.dx, toMove.dy + away.dy);
            if (toMove.equals(Direction.NONE)) {
                toMove = MapUtils.getDirectionFromDxDy(toMove.dx + away.dx, toMove.dy + away.dy);
                if (rc.canMove(away)) {
                    rc.move(away);
                    return true;
                }
                if (rc.canMove(away.rotateLeft())) {
                    rc.move(away.rotateLeft());
                    return true;
                }
                if (rc.canMove(away.rotateRight())) {
                    rc.move(away.rotateRight());
                    return true;
                }
            } else {
                if (rc.canMove(toMove)) {
                    rc.move(toMove);
                    return true;
                }
            }
            toMove = MapUtils.getRCCanMoveDirection(this);
            if (!toMove.equals(Direction.NONE)) {
                rc.move(toMove);
                return true;
            }
            return false;
        } else {
            rc.setIndicatorString(1, "chillin");
            return true;
        }
        return false;

    }

    private boolean avoid(MapLocation zombieCenter) throws GameActionException {
        Direction toMove;
        boolean left = true;
        if (left) {
            toMove = currentLocation.directionTo(zombieCenter).rotateLeft().rotateLeft();
            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
                rc.move(toMove);
                return true;
            }
            toMove = toMove.rotateLeft();
            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
                rc.move(toMove);
                return true;
            }
            toMove = toMove.rotateLeft();
            if (rc.canMove(toMove)) {
                rc.move(toMove);
                return true;
            }
        } else {
            toMove = currentLocation.directionTo(zombieCenter).rotateRight().rotateRight();
            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
                rc.move(toMove);
                return true;
            }

            toMove = toMove.rotateRight();
            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
                rc.move(toMove);
                return true;
            }

            toMove = toMove.rotateRight();
            if (rc.canMove(toMove)) {
                rc.move(toMove);
                return true;
            }
        }
        return false;
    }

    private boolean herd(MapLocation zombie, RobotType zType) {


        return false;
    }

    /*
    ===============================
    MOVE INTO POSITION AROUND ENEMY
    ===============================
     */
    private boolean moveInPositionAroundEnemey() {
        return false;
    }



    /*
    ===============================
    FIND ENEMIES
    ===============================
     */
    private boolean findEnemies() throws GameActionException {
        // precondition
        if (enemies.length > 0) {
            return false;
        }

        if (currentLocation.isAdjacentTo(navigator.getTarget())) {
            navigator.setTarget(nextPlaceToLookForEnemies());
        }

        return navigator.takeNextStep();
    }

    private MapLocation nextPlaceToLookForEnemies() {
        idxForEnemyLocations++;
        if (idxForEnemyLocations > enemyArchonStartLocs.length) {
            idxForEnemyLocations = 0;
        }
        if (idxForEnemyLocations == enemyArchonStartLocs.length) {
            return enemyCore;
        }
        return enemyArchonStartLocs[idxForEnemyLocations];
    }

    private boolean bringZombiesToEnemy() {
        return false;
    }

    public boolean herdAwayFromArchon() {
        return false;
    }


    private void suicideIfNeeded() {
        if (rc.getInfectedTurns() == 1 && rc.getHealth() < type.maxHealth / 2) {
            if (closestEnemy < closestAlliedArchon) {
                // TODO: Spam messages here before dying?
                rc.disintegrate();
            }
        }

    }


}
