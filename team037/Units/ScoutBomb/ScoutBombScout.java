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
    private static int closestEnemyArchon;
    private static MapLocation closestEnemyArchonLoc;

    private static int closestAlliedArchon;
    private static MapLocation closestAlliedArchonLoc;
    private static RobotInfo closestAlliedArchonInfo;

    private static int closestZombie;
    private static MapLocation closestZombieLoc;
    private static RobotInfo closestZombieInfo;

    private static int closestRangedZombie;
    private static MapLocation closestRangedZombieLoc;
    private static RobotInfo closestRangedZombieInfo;

    private static int possibleEnemyDamageNextTurn;
    private static int idxForEnemyLocations = -1;
    private static MapLocation enemyCore;
    private static boolean nonDenZombies = false;
    private static boolean nonScoutEnemies = false;
    private static boolean rushForward = false;
    private static MapLocation alliedCenter;

    private static final int MIN_AVOID_DIST = (int)(RobotType.RANGEDZOMBIE.attackRadiusSquared * 1.5);


    // see https://docs.google.com/spreadsheets/d/1BK9TgwPGKFMwqvOIgQjwwkYKi8Say01jNNd7MB6syXA/edit#gid=210938007
    private static final int MELEE_MOVE_AWAY_DISTANCE = 2; // if you are less than this, move away
    private static final int MELEE_DONT_MOVE_DISTANCE = 8; // if you are here, don't move
    private static final int MELEE_MOVE_TOWARD_DISTANCE = 9;  // if you are here, move toward to herd
    private static final int MELEE_AVOID_DISTANCE = MELEE_MOVE_TOWARD_DISTANCE;

    private static final int RANGED_MOVE_AWAY_DISTANCE = 13; // if you are less than this, move away
    private static final int RANGED_DONT_MOVE_DISTANCE = 25; // if you are here, don't move
    private static final int RANGED_MOVE_TOWARD_DISTANCE = 26;  // if you are here, move toward to herd
    private static final int RANGED_AVOID_DISTANCE = RANGED_MOVE_TOWARD_DISTANCE;

    private static RobotInfo[] veryCloseZombies;



    private static final String HERD_AWAY_FROM_ARCHON = "herding away from allied archon";
    private static final String BRING_ZOMBIES_TO_ENEMEY = "bringing zombies to enemey";
    private static final String FIND_ENEMIES = "finding enemies";
    private static final String HERD_AND_FIND_ENEMIES = "herding and finding enemies";
    private static final String MOVE_INTO_POSITION_AROUND_ENEMEY = "move into position around enemey";
    private static final String NOTHING = "NOTHING :(";

    private static String lastAction = NOTHING;


    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();
    public static FlyingSlugNavigator navigator;

    public ScoutBombScout(RobotController rc)
    {
        super(rc);
        mapKnowledge = mKnowledge;
        navigator = new FlyingSlugNavigator(rc);
        enemyCore = MapUtils.getCenterOfMass(enemyArchonStartLocs);
        alliedCenter = MapUtils.getCenterOfMass(alliedArchonStartLocs);
        navigator.setTarget(nextPlaceToLookForEnemies());

    }


    @Override
    public void collectData() throws GameActionException {
        super.collectData();

        // zombie info
        // for now only look at close zombies
        veryCloseZombies = rc.senseNearbyRobots(RANGED_AVOID_DISTANCE, Team.ZOMBIE);
        nonDenZombies = false;
        closestZombie = Integer.MAX_VALUE;
        closestZombieLoc = null;
        closestZombieInfo = null;
        closestRangedZombie = Integer.MAX_VALUE;
        closestRangedZombieLoc = null;
        closestRangedZombieInfo = null;
        for (int i = veryCloseZombies.length; --i >= 0;) {
            RobotInfo zombie = veryCloseZombies[i];
            int dist = zombie.location.distanceSquaredTo(currentLocation);
            if (!zombie.type.equals(RobotType.ZOMBIEDEN)) {
                if (dist < closestZombie) {
                    closestZombie = dist;
                    closestZombieLoc = zombie.location;
                    closestZombieInfo = zombie;
                }
                nonDenZombies = true;
            }
            if (zombie.type.equals(RobotType.RANGEDZOMBIE)) {
                if (dist < closestRangedZombie) {
                    closestRangedZombie = dist;
                    closestRangedZombieLoc = zombie.location;
                    closestRangedZombieInfo = zombie;
                }
            }
        }


        // enemy info
        nonScoutEnemies = false;
        possibleEnemyDamageNextTurn = 0;
        closestEnemy = Integer.MAX_VALUE;
        closestEnemyLoc = null;
        closestEnemyArchon = Integer.MAX_VALUE;
        closestEnemyArchonLoc = null;
        for (int i = enemies.length; --i>=0;) {
            int distance =  enemies[i].location.distanceSquaredTo(currentLocation);
            if (distance < closestEnemy) {
                closestEnemy = distance;
                closestEnemyLoc = enemies[i].location;
            }
            switch(enemies[i].type) {
                case ARCHON:
                    nonScoutEnemies = true;
                    rc.setIndicatorLine(currentLocation, enemies[i].location, 0, 0, 0);
                    navigator.setTarget(enemies[i].location);
                    break;
                case SCOUT:
                    break;
                case GUARD:
                    nonScoutEnemies = true;
                    if (distance <= 7 + RobotType.GUARD.attackRadiusSquared) {
                        possibleEnemyDamageNextTurn += RobotType.GUARD.attackPower;
                    }
                    break;
                case SOLDIER:
                    nonScoutEnemies = true;
                    if (distance <= 2 * RobotType.SOLDIER.attackRadiusSquared) {
                        possibleEnemyDamageNextTurn += RobotType.SOLDIER.attackPower;
                    }
                    break;
                case TTM:
                    nonScoutEnemies = true;
                    break;
                case TURRET:
                    nonScoutEnemies = true;
                    if (distance <= 30 + RobotType.TURRET.attackRadiusSquared) {
                        possibleEnemyDamageNextTurn += RobotType.TURRET.attackPower;
                    }
                    break;
                case VIPER:
                    nonScoutEnemies = true;
                    if (distance <= 30 + RobotType.VIPER.attackRadiusSquared) {
                        possibleEnemyDamageNextTurn += RobotType.VIPER.attackPower + 20;
                    }
                    break;

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
                    closestAlliedArchonInfo = allies[i];
                }
            }
        }
    }

    @Override
    public void handleMessages() {
        Signal[] signals = rc.emptySignalQueue();
        for (int i = signals.length; --i >= 0;) {
            if (signals[i].getTeam().equals(us)) {
                int[] message = signals[i].getMessage();
                if (message[0] == Integer.MIN_VALUE && message[1] == Integer.MIN_VALUE) {
                    rc.setIndicatorDot(currentLocation, 255, 0, 0);
                    if (closestAlliedArchonLoc == null) {
                        rushForward = true;
                    }
                    return;
                }
            }

        }
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
        }  else if (suicideCall()) {
            lastAction = "heard the suicideScout message";
        } else if (herdAwayFromArchon()) {
            // if you are near an allied archon and see zombies, help them!
            lastAction = HERD_AWAY_FROM_ARCHON;
        } else if (herdFastZombies()) {
            lastAction = "heardingFastZombie";
        }  else if (herdAndFindEnemy()) {
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
        rc.setIndicatorLine(currentLocation, navigator.getTarget(), 255, 255, 255);
        return true;
    }

    /*
    ===============================
    HRED FAST ZOMBIES
    ===============================
     */
    private boolean herdFastZombies() throws GameActionException {
        // precondition
        if (!nonDenZombies || !RobotType.FASTZOMBIE.equals(closestZombieInfo.type)) {
            return false;
        }
        if (nonScoutEnemies) {
            rushForward = true;
        }
        if (rc.getInfectedTurns() > 5) {
            navigator.takeNextStep(id % 5 <= 1, id % 2 == 0);
        }
        return true;
    }

    /*
    ===============================
    SUICIDE CALL
    ===============================
     */
    private boolean suicideCall() throws GameActionException {
        if (!rushForward) {
            return false;
        }
        return turnASAP();
    }



    /*
    ===============================
    HERD AND FIND ENEMY
    ===============================
     */
    private boolean herdAndFindEnemy() throws GameActionException {
        // precontidions
        if (!nonDenZombies || nonScoutEnemies) {
            return false;
        }

        RobotInfo[] friends = rc.senseNearbyRobots(closestZombieLoc, closestZombie - 1, us);

        if (friends.length > 0) {
            return avoid(closestZombieLoc);
        } else {
            return herd();
        }

    }


    public boolean herdZombie(int distToZombie, MapLocation zombieLoc, RobotInfo zombieInfo, int moveAwayDistance, int dontMoveDistance, int moveTowardDistance) throws GameActionException {
        rc.setIndicatorLine(currentLocation, zombieLoc, 0, 255, 0);
        MapLocation target = navigator.getTarget();

        if (distToZombie > moveTowardDistance) {
            rc.setIndicatorString(1, "moving to herd " + String.valueOf(zombieLoc));
            if (rc.canMove(currentLocation.directionTo(zombieLoc))) {
                rc.move(currentLocation.directionTo(zombieLoc));
                return true;
            }
        } else if (distToZombie <= moveAwayDistance && zombieInfo.weaponDelay <= 2) {
            rc.setIndicatorString(1, "moving away from " + String.valueOf(zombieLoc));
            Direction toMove = currentLocation.directionTo(target);
            Direction away = zombieLoc.directionTo(currentLocation);
            toMove = MapUtils.addDirections(toMove, away);
            if (toMove.equals(Direction.NONE)) {
                toMove = MapUtils.addDirections(toMove, away);
                if (!toMove.equals(away) && rc.canMove(toMove)) {
                    rc.move(toMove);
                    return true;
                }
                if (rc.canMove(toMove.rotateLeft())) {
                    rc.move(toMove.rotateLeft());
                    return true;
                }
                if (rc.canMove(toMove.rotateRight())) {
                    rc.move(toMove.rotateRight());
                    return true;
                }
                if (rc.canMove(away)) {
                    rc.move(away);
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
            if (MapUtils.canZombieMoveTowardMe(rc, currentLocation, zombieLoc)) {
                // if we are on the right side of the zombie
                rc.setIndicatorString(1, "waiting for zombie to move " + String.valueOf(zombieLoc));
                return true;
            } else {
                if (zombieLoc.distanceSquaredTo(target) < currentLocation.distanceSquaredTo(target)) {
                    rc.setIndicatorString(1, "zombie can't move toward me and is in my way!");
                    Direction toMove = MapUtils.addDirections(currentLocation.directionTo(target), zombieLoc.directionTo(currentLocation));
                    if (!toMove.equals(Direction.NONE)) {
                        if (rc.canMove(toMove)) {
                            int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
                            if (dist > moveAwayDistance && dist <= dontMoveDistance ) {
                                rc.move(toMove);
                                return true;
                            }
                        }
                    }
                    toMove = currentLocation.directionTo(target);
                    if (id % 2 == 0) {
                        toMove.rotateLeft().rotateLeft();
                    } else {
                        toMove.rotateRight().rotateRight();
                    }
                    if (rc.canMove(toMove)) {
                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
                        if (dist > moveAwayDistance && dist <= dontMoveDistance ) {
                            rc.move(toMove);
                            return true;
                        }
                    }
                    rc.setIndicatorString(1, "zombie can't move toward me and is in my way! (no way around)");
                    return true;
                } else {
                    rc.setIndicatorString(1, "zombie can't move toward me but I should be good!");
                    Direction toMove = currentLocation.directionTo(target);
                    if (rc.canMove(toMove)) {
                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
                        if (dist > moveAwayDistance && dist <= dontMoveDistance ) {
                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
                            rc.move(toMove);
                            return true;
                        }
                    }
                    toMove = toMove.rotateLeft();
                    if (rc.canMove(toMove)) {
                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
                        if (dist > moveAwayDistance && dist <= dontMoveDistance ) {
                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
                            rc.move(toMove);
                            return true;
                        }
                    }
                    toMove = toMove.rotateRight().rotateRight();
                    if (rc.canMove(toMove)) {
                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
                        if (dist > moveAwayDistance && dist <= dontMoveDistance ) {
                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
                            rc.move(toMove);
                            return true;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }


    public boolean herd() throws GameActionException {
        rc.setIndicatorLine(currentLocation, closestZombieLoc, 0, 255, 0);
        if (closestZombieInfo.type.equals(RobotType.BIGZOMBIE) && closestZombie < MELEE_AVOID_DISTANCE) {
            rc.setIndicatorString(2, "melee");
            return herdZombie(closestZombie, closestZombieLoc, closestZombieInfo, MELEE_MOVE_AWAY_DISTANCE, MELEE_DONT_MOVE_DISTANCE, MELEE_MOVE_TOWARD_DISTANCE);
        }
        // if you can be attacked by a ranged or melee
        if (closestRangedZombie <= RobotType.RANGEDZOMBIE.attackRadiusSquared) {
            rc.setIndicatorString(2, "ranged");
            return herdZombie(closestRangedZombie, closestRangedZombieLoc, closestRangedZombieInfo, RANGED_MOVE_AWAY_DISTANCE, RANGED_DONT_MOVE_DISTANCE, RANGED_MOVE_TOWARD_DISTANCE);
        }
        if (closestZombie <= RobotType.STANDARDZOMBIE.attackRadiusSquared) {
            rc.setIndicatorString(2, "melee");
            return herdZombie(closestZombie, closestZombieLoc, closestZombieInfo, MELEE_MOVE_AWAY_DISTANCE, MELEE_DONT_MOVE_DISTANCE, MELEE_MOVE_TOWARD_DISTANCE);
        }


        if (closestRangedZombie < RANGED_AVOID_DISTANCE) {
            rc.setIndicatorString(2, "ranged");
            return herdZombie(closestRangedZombie, closestRangedZombieLoc, closestRangedZombieInfo, RANGED_MOVE_AWAY_DISTANCE, RANGED_DONT_MOVE_DISTANCE, RANGED_MOVE_TOWARD_DISTANCE);
        }

        rc.setIndicatorString(2, "melee");
        return herdZombie(closestZombie, closestZombieLoc, closestZombieInfo, MELEE_MOVE_AWAY_DISTANCE, MELEE_DONT_MOVE_DISTANCE, MELEE_MOVE_TOWARD_DISTANCE);
    }

    private boolean avoid(MapLocation zombieCenter) throws GameActionException {
        Direction toMove = currentLocation.directionTo(navigator.getTarget());
        Direction away = closestZombieLoc.directionTo(currentLocation);
        toMove = MapUtils.addDirections(toMove, away);
        if (!toMove.equals(Direction.NONE)) {
            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
                rc.move(toMove);
                return true;
            }
        } else {
            toMove = MapUtils.addDirections(toMove, away);
            if (id % 2 == 0) {
                toMove = toMove.rotateLeft();
            } else {
                toMove = toMove.rotateRight();
            }
            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
                rc.move(toMove);
                return true;
            }
        }
        return false;
    }


    /*
    ===============================
    MOVE INTO POSITION AROUND ENEMY
    ===============================
     */
    private boolean moveInPositionAroundEnemey() throws GameActionException {
        if (!nonScoutEnemies) {
            return false;
        }
        if (possibleEnemyDamageNextTurn > 0) {
            rc.setIndicatorString(1, "Trying to move away");
            Direction toMove = Direction.NONE;
            // need to move away
            for (int i = enemies.length; --i >= 0;) {
                if (currentLocation.distanceSquaredTo(enemies[i].location) <= enemies[i].type.attackRadiusSquared) {
                    toMove = MapUtils.addDirections(toMove, enemies[i].location.directionTo(currentLocation));
                }
            }
            if (!toMove.equals(Direction.NONE)) {
                if (rc.canMove(toMove)) {
                    rc.move(toMove);
                    return true;
                }
            }
            if (rc.canMove(closestEnemyLoc.directionTo(currentLocation))) {
                rc.move(closestEnemyLoc.directionTo(currentLocation));
                return true;
            }
        } else {
            rc.setIndicatorString(1, "We are fine!");
        }

        return true;
    }



    /*
    ===============================
    FIND ENEMIES
    ===============================
     */
    private boolean findEnemies() throws GameActionException {
        // precondition
        if (nonScoutEnemies) {
            return false;
        }

        if (currentLocation.isAdjacentTo(navigator.getTarget())) {
            navigator.setTarget(nextPlaceToLookForEnemies());
        }

        return navigator.takeNextStep(id % 5 <= 1, id % 2 == 0);
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

    /*
    ===============================
    BRING ZOMBIES TO ENEMY
    ===============================
     */
    private boolean bringZombiesToEnemy() throws GameActionException {
        // precondition
        if (!(nonDenZombies && nonScoutEnemies)) {
            return false;
        }
        // if the zombies are closer to the enemy than I am, turn!
        if (rc.senseNearbyRobots(closestEnemyLoc, closestEnemy, Team.ZOMBIE).length > 1) {
            if (closestEnemyArchon < Integer.MAX_VALUE) {
                return turnASAP();
            }
        }
        if (possibleEnemyDamageNextTurn > 0 && rc.getInfectedTurns() < 5) {
            if (possibleEnemyDamageNextTurn < rc.getHealth()) {
                // wait for zombie to catch up!
                rc.setIndicatorString(1, "waiting for infection");
                return true;
            } else {
                rc.setIndicatorString(1, "scared, moving to infection");
                Direction toZombie = currentLocation.directionTo(closestZombieLoc);
                if (rc.canMove(toZombie)) {
                    rc.move(toZombie);
                    return true;
                }
                if (rc.canMove(toZombie.rotateLeft())) {
                    rc.move(toZombie.rotateLeft());
                    return true;
                }
                if (rc.canMove(toZombie.rotateRight())) {
                    rc.move(toZombie.rotateRight());
                    return true;
                }
                return false;
            }
        } else {
            rc.setIndicatorString(1, "good to go " + possibleEnemyDamageNextTurn);
            return herd();
        }
    }

    private boolean turnASAP() throws GameActionException {
        if (rc.getInfectedTurns() > 0) {
            suicideScout();
            return true;
        }
        if (closestZombieLoc == null) {
            return false;
        }
        Direction toZombie = currentLocation.directionTo(closestZombieLoc);
        if (rc.canMove(toZombie)) {
            rc.move(toZombie);
            return true;
        }
        if (rc.canMove(toZombie.rotateLeft())) {
            rc.move(toZombie.rotateLeft());
            return true;
        }
        if (rc.canMove(toZombie.rotateRight())) {
            rc.move(toZombie.rotateRight());
            return true;
        }
        return false;
    }


    /*
    ===============================
    HERD AWAY FROM ARCHON
    ===============================
     */
    public boolean herdAwayFromArchon() {
        // preconditions
        if (closestAlliedArchon == Integer.MAX_VALUE) {
            return false;
        }
        RobotInfo[] zambies = rc.senseNearbyRobots(closestAlliedArchonLoc, closestAlliedArchon + 3, Team.ZOMBIE);
        if (zombies.length == 0) {
            return false;
        }



        return false;
    }




    private void suicideIfNeeded() throws GameActionException {
        if (rc.getInfectedTurns() == 1 && rc.getHealth() < type.maxHealth / 2) {
            if (closestEnemy < closestAlliedArchon) {
                suicideScout();
            }
        }
    }

    private void suicideScout() throws GameActionException {
        int i = 0;
        while (Clock.getBytecodesLeft() > 200 && i < 20) {
            rc.broadcastMessageSignal(Integer.MIN_VALUE, Integer.MIN_VALUE, 25);
            i++;
        }
        rc.disintegrate();
    }


}
