package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.Enums.Strategies;
import team037.FlyingSlugNavigator;
import team037.FlyingWallHuggerNavigator;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.RobotPlayer;
import team037.ScoutMapKnowledge;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.MapUtils;
import team037.Utilites.MoveUtils;

public class ScoutBombScout extends BaseScout
{
    public ScoutBombScout(RobotController rc) {
        super(rc);
    }
//    private static int closestEnemy;
//    private static MapLocation closestEnemyLoc;
//    private static int closestEnemyArchon;
//    private static RobotInfo closestEnemyArchonInfo;
//    private static int closestEnemyViper;
//    private static MapLocation closestEnemyViperLoc;
//    private static RobotInfo closestEnemyViperInfo;
//    private static int closestEnemyTurret;
//    private static RobotInfo closestEnemyTurretInfo;
//    private static boolean onlyEnemyIsArchon;
//
//    private static int closestAlliedArchon;
//    private static MapLocation closestAlliedArchonLoc;
//    private static RobotInfo closestAlliedArchonInfo;
//    private static int numberAlliedScouts;
//
//    private static int closestZombie;
//    private static MapLocation closestZombieLoc;
//    private static RobotInfo closestZombieInfo;
//
//    private static int closestRangedZombie;
//    private static MapLocation closestRangedZombieLoc;
//    private static RobotInfo closestRangedZombieInfo;
//
//    private static int possibleEnemyDamageNextTurn;
//    private static int idxForEnemyLocations = -1;
//    private static MapLocation enemyCore;
//    private static boolean nonDenZombies = false;
//    private static boolean nonScoutEnemies = false;
//
//    // changing this to true will make the bots suicidal
//    private static boolean rushForward = false;
//    private static MapLocation alliedCenter;
//
//    private static final int MIN_AVOID_DIST = (int)(RobotType.RANGEDZOMBIE.attackRadiusSquared * 1.5);
//
//
//    // see https://docs.google.com/spreadsheets/d/1BK9TgwPGKFMwqvOIgQjwwkYKi8Say01jNNd7MB6syXA/edit#gid=210938007
//    private static final int MELEE_MOVE_AWAY_DISTANCE = 2; // if you are less than this, move away
//    private static final int MELEE_DONT_MOVE_DISTANCE = 8; // if you are here, don't move
//    private static final int MELEE_MOVE_TOWARD_DISTANCE = 9;  // if you are here, move toward to herd
//    private static final int MELEE_AVOID_DISTANCE = MELEE_MOVE_TOWARD_DISTANCE;
//
//    private static final int RANGED_MOVE_AWAY_DISTANCE = 13; // if you are less than this, move away
//    private static final int RANGED_DONT_MOVE_DISTANCE = 25; // if you are here, don't move
//    private static final int RANGED_MOVE_TOWARD_DISTANCE = 26;  // if you are here, move toward to herd
//    private static final int RANGED_AVOID_DISTANCE = RANGED_MOVE_TOWARD_DISTANCE;
//
//    private static RobotInfo[] veryCloseZombies;
//
//
//
//    private static final String HERD_AWAY_FROM_ARCHON = "herding away from allied archon";
//    private static final String BRING_ZOMBIES_TO_ENEMEY = "bringing zombies to enemey";
//    private static final String FIND_ENEMIES = "finding enemies";
//    private static final String HERD_AND_FIND_ENEMIES = "herding and finding enemies";
//    private static final String MOVE_INTO_POSITION_AROUND_ENEMEY = "move into position around enemey";
//    private static final String CHASE_ENEMY_VIPER = "going to enemy viper";
//    private static final String SURROUND_ENEMY_ARCHON = "surrounding enemy archon";
//    private static final String NOTHING = "NOTHING :(";
//
//    private static String lastAction = NOTHING;
//
//
//    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();
//    public static FlyingSlugNavigator navigator;
//    public static FlyingWallHuggerNavigator wallNavigator;
//
//    public ScoutBombScout(RobotController rc)
//    {
//        super(rc);
//        mapKnowledge = mKnowledge;
//        navigator = new FlyingSlugNavigator(rc);
//        wallNavigator = new FlyingWallHuggerNavigator(rc);
//        enemyCore = MapUtils.getCenterOfMass(enemyArchonStartLocs);
//        alliedCenter = MapUtils.getCenterOfMass(alliedArchonStartLocs);
//        updateTarget(nextPlaceToLookForEnemies());
//    }
//
//    private boolean useWallHugger() {
//        return id % 5 <= 1;
//    }
//
//    private void updateTarget(MapLocation target) {
//        navigator.setTarget(target);
//        wallNavigator.setTarget(target);
//    }
//
//    @Override
//    public void collectData() throws GameActionException {
//        super.collectData();
//
//
//        if (navigator.getTarget() == null) {
//            updateTarget(nextPlaceToLookForEnemies());
//        }
//        if (currentLocation.isAdjacentTo(navigator.getTarget())) {
//            updateTarget(nextPlaceToLookForEnemies());
//        }
//
//        // zombie info
//        // for now only look at close zombies
//        veryCloseZombies = rc.senseNearbyRobots(RANGED_AVOID_DISTANCE, Team.ZOMBIE);
//        nonDenZombies = false;
//        closestZombie = Integer.MAX_VALUE;
//        closestZombieLoc = null;
//        closestZombieInfo = null;
//        closestRangedZombie = Integer.MAX_VALUE;
//        closestRangedZombieLoc = null;
//        closestRangedZombieInfo = null;
//        for (int i = veryCloseZombies.length; --i >= 0;) {
//            RobotInfo zombie = veryCloseZombies[i];
//            int dist = zombie.location.distanceSquaredTo(currentLocation);
//            if (!zombie.type.equals(RobotType.ZOMBIEDEN)) {
//                if (dist < closestZombie) {
//                    closestZombie = dist;
//                    closestZombieLoc = zombie.location;
//                    closestZombieInfo = zombie;
//                }
//                nonDenZombies = true;
//            }
//            if (zombie.type.equals(RobotType.RANGEDZOMBIE)) {
//                if (dist < closestRangedZombie) {
//                    closestRangedZombie = dist;
//                    closestRangedZombieLoc = zombie.location;
//                    closestRangedZombieInfo = zombie;
//                }
//            }
//        }
//
//
//        // enemy info
//        nonScoutEnemies = false;
//        possibleEnemyDamageNextTurn = 0;
//        closestEnemy = Integer.MAX_VALUE;
//        closestEnemyLoc = null;
//        closestEnemyArchon = Integer.MAX_VALUE;
//        closestEnemyArchonInfo = null;
//        closestEnemyViper = Integer.MAX_VALUE;
//        closestEnemyViperLoc = null;
//        closestEnemyTurret = Integer.MAX_VALUE;
//        closestEnemyTurretInfo = null;
//        onlyEnemyIsArchon = true;
//        for (int i = enemies.length; --i>=0;) {
//            int distance =  enemies[i].location.distanceSquaredTo(currentLocation);
//            if (distance < closestEnemy && !enemies[i].type.equals(RobotType.SCOUT)) {
//                closestEnemy = distance;
//                closestEnemyLoc = enemies[i].location;
//            }
//            switch(enemies[i].type) {
//                case ARCHON:
//                    nonScoutEnemies = true;
//                    rc.setIndicatorLine(currentLocation, enemies[i].location, 0, 0, 0);
//                    if (distance < closestEnemyArchon) {
//                        closestEnemyArchon = distance;
//                        closestEnemyArchonInfo = enemies[i];
//                    }
//                    updateTarget(enemies[i].location);
//                    break;
//                case SCOUT:
//                    break;
//                case GUARD:
//                    onlyEnemyIsArchon = false;
//                    nonScoutEnemies = true;
//                    if (distance <= 7 + RobotType.GUARD.attackRadiusSquared) {
//                        possibleEnemyDamageNextTurn += RobotType.GUARD.attackPower;
//                    }
//                    break;
//                case SOLDIER:
//                    onlyEnemyIsArchon = false;
//                    nonScoutEnemies = true;
//                    if (distance <= 2 * RobotType.SOLDIER.attackRadiusSquared) {
//                        possibleEnemyDamageNextTurn += RobotType.SOLDIER.attackPower;
//                    }
//                    break;
//                case TTM:
//                    onlyEnemyIsArchon = false;
//                    nonScoutEnemies = true;
//                    break;
//                case TURRET:
//                    onlyEnemyIsArchon = false;
//                    nonScoutEnemies = true;
//                    if (distance < closestEnemyTurret) {
//                        closestEnemyTurret = distance;
//                        closestEnemyTurretInfo = enemies[i];
//                    }
//                    if (distance <= 30 + RobotType.TURRET.attackRadiusSquared) {
//                        possibleEnemyDamageNextTurn += RobotType.TURRET.attackPower;
//                    }
//                    break;
//                case VIPER:
//                    onlyEnemyIsArchon = false;
//                    nonScoutEnemies = true;
//                    if (distance <= 30 + RobotType.VIPER.attackRadiusSquared) {
//                        possibleEnemyDamageNextTurn += RobotType.VIPER.attackPower + 20;
//                    }
//                    if (distance < closestEnemyViper) {
//                        closestEnemyViper = distance;
//                        closestEnemyViperLoc = enemies[i].location;
//                        closestEnemyViperInfo = enemies[i];
//                    }
//                    break;
//
//            }
//        }
//
//        // closestAlliedArchon
//        closestAlliedArchon = Integer.MAX_VALUE;
//        closestAlliedArchonLoc = null;
//        numberAlliedScouts = 0;
//        for (int i = allies.length; --i>=0;) {
//            if (allies[i].type.equals(RobotType.ARCHON)) {
//                int distance =  allies[i].location.distanceSquaredTo(currentLocation);
//                if (distance < closestAlliedArchon) {
//                    closestAlliedArchon = distance;
//                    closestAlliedArchonLoc = allies[i].location;
//                    closestAlliedArchonInfo = allies[i];
//                }
//            } else if (allies[i].type.equals(RobotType.SCOUT)) {
//                numberAlliedScouts += 1;
//            }
//        }
//    }
//
//    @Override
//    public void handleMessages() {
//        Signal[] signals = rc.emptySignalQueue();
//        for (int i = signals.length; --i >= 0;) {
//            if (signals[i].getTeam().equals(us)) {
//                int[] message = signals[i].getMessage();
//                if (message != null && message[0] == Integer.MIN_VALUE && message[1] == Integer.MIN_VALUE) {
//                    rc.setIndicatorDot(currentLocation, 255, 0, 0);
//                    if (closestAlliedArchonLoc == null) {
//                        rushForward = true;
//                    }
//                    return;
//                }
//            }
//
//        }
//    }
//
//    @Override
//    public void sendMessages() throws GameActionException {
//        if (!FightMicroUtilites.offensiveEnemies(zombies)) {
//            msgDens();
//            msgParts();
//        }
//
//        return;
//    }
//
//    @Override
//    public boolean act() throws GameActionException {
//        suicideIfNeeded();
//
//        if (!rc.isCoreReady()) {
//            rc.setIndicatorString(0, "core cooldown");
//            return false;
//        }  else if (suicideCall()) {
//            lastAction = "heard the suicideScout message";
//        } else if (herdAwayFromArchon()) {
//            // if you are near an allied archon and see zombies, help them!
//            lastAction = HERD_AWAY_FROM_ARCHON;
//        } else if (infectEnemyArchon()) {
//
//        } else if (suicideOnEnemyViper()) {
//            lastAction = CHASE_ENEMY_VIPER;
//        } else if (surroundEnemyArchon()) {
//            lastAction = SURROUND_ENEMY_ARCHON;
//        } else if (herdFastZombies()) {
//            lastAction = "herdingFastZombie";
//        } else if (herdAndFindEnemy()) {
//            lastAction = HERD_AND_FIND_ENEMIES;
//        } else if (bringZombiesToEnemy()) {
//            // if you are near the enemy and see a zombie, let's do it!
//            lastAction = BRING_ZOMBIES_TO_ENEMEY;
//        } else if (findEnemies()) {
//            // if there are no enemies in sight, find them!
//            lastAction = FIND_ENEMIES;
//        } else if (moveInPositionAroundEnemey()) {
//            lastAction = MOVE_INTO_POSITION_AROUND_ENEMEY;
//        } else {
//            lastAction = NOTHING;
//            rc.setIndicatorString(0, "NOTHING! :(");
//            //TODO: randomly moving to "shake" the zombie out of the horrible situation it finds itself in
//            return false;
//        }
//        rc.setIndicatorString(0, lastAction);
//        rc.setIndicatorLine(currentLocation, navigator.getTarget(), 255, 255, 255);
//        return true;
//    }
//
//    private boolean infectEnemyArchon() throws GameActionException {
//        if (rc.getInfectedTurns()  == 0 || closestEnemyArchonInfo == null) {
//            return false;
//        }
//        if (currentLocation.isAdjacentTo(closestEnemyArchonInfo.location) || rc.getInfectedTurns() <= 2) {
//            rc.disintegrate();
//        }
//        if (rc.getInfectedTurns() > 2) {
//            return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(closestEnemyArchonInfo.location), false);
//        }
//        return false;
//    }
//
//    /*
//    ===============================
//    SURROUND_ENEMY_ARCHON
//    If you see an enemy archon alone, try to surround it!
//    ===============================
//    */
//    private boolean surroundEnemyArchon() throws GameActionException {
//        // preconditions
//        if (!onlyEnemyIsArchon || numberAlliedScouts < 5) {
//            return false;
//        }
//
//        if (closestEnemyArchonInfo == null) {
//            return false;
//        }
//
//        boolean moved =  MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(closestEnemyArchonInfo.location), false);
//        if (currentLocation.isAdjacentTo(closestEnemyArchonInfo.location)) {
//            // try and make the archon more confused
//            for (int i = 5; --i > 0;) {
//                rc.broadcastSignal(2);
//            }
//            Communication communication = new BotInfoCommunication();
//            communication.setValues(new int[] {CommunicationType.toInt(CommunicationType.ENEMY), 0, 0, 0, closestEnemyArchonInfo.location.x, closestEnemyArchonInfo.location.y});
//            communicator.sendCommunication(2500, communication);
//            for (int i = 19; --i > 0;) {
//                rc.broadcastMessageSignal(2, 2, 2);
//            }
//        }
//        if (!moved && currentLocation.isAdjacentTo(closestEnemyArchonInfo.location)) {
//            return true;
//        }
//        return moved;
//    }
//
//    /*
//    ===============================
//    CHASE_ENEMY_VIPER
//    If it's late game, or you are far enough away from your archon, dive onto the enemy viper!
//    ===============================
//     */
//    private boolean suicideOnEnemyViper() throws GameActionException {
//        // preconditions
//        if (closestEnemyViper == Integer.MAX_VALUE) {
//            return false;
//        }
//        if (closestAlliedArchon < Integer.MAX_VALUE && round < 2450) {
//            return false;
//        }
//        // wait for it's spawn cooldown
//        if (closestEnemyViperInfo.coreDelay > 4 || closestEnemyViperInfo.weaponDelay > 4) {
//            return false;
//        }
//        if (currentLocation.isAdjacentTo(closestEnemyViperLoc) && rc.getInfectedTurns() > 0) {
//            suicideScout();
//        }
//        // if we're on the other side of the map, or it's lategame and we see a viper, close the distance!
//        return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(closestEnemyViperLoc), false);
//    }
//
//    /*
//    ===============================
//    HERD FAST ZOMBIES
//    So if you see fast zombies, drop everything and RUN!
//    ===============================
//     */
//    private boolean herdFastZombies() throws GameActionException {
//        // precondition
//        if (!nonDenZombies || !RobotType.FASTZOMBIE.equals(closestZombieInfo.type)) {
//            return false;
//        }
//        if (nonScoutEnemies) {
//            rushForward = true;
//        }
//        if (rc.getInfectedTurns() > 5) {
//            if (useWallHugger()) {
//                wallNavigator.takeNextStep();
//            } else {
//                navigator.takeNextStep(id % 5 <= 2, id % 2 == 0);
//            }
//        }
//        return true;
//    }
//
//    /*
//    ===============================
//    SUICIDE CALL
//    ===============================
//     */
//    private boolean suicideCall() throws GameActionException {
//        // precondition
//        if (!rushForward) {
//            return false;
//        }
//        return turnASAP();
//    }
//
//
//
//    /*
//    ===============================
//    HERD AND FIND ENEMY
//    Assumes that you are herding a zombie and can't see an enemy.
//    Try to make you herd that zombie toward the enemy
//    Probably should call this move towards enemy and herd zombies if needed function
//    ===============================
//     */
//    private boolean herdAndFindEnemy() throws GameActionException {
//        // precontidions
//        if (!nonDenZombies || nonScoutEnemies) {
//            return false;
//        }
//
//        RobotInfo[] friends = rc.senseNearbyRobots(closestZombieLoc, closestZombie - 1, us);
//
//        if (friends.length > 0) {
//            return avoid(closestZombieLoc);
//        } else {
//            return herd();
//        }
//
//    }
//
//
//    public boolean herdZombie(int distToZombie, MapLocation zombieLoc, RobotInfo zombieInfo, int moveAwayDistance, int dontMoveDistance, int moveTowardDistance) throws GameActionException {
//        rc.setIndicatorLine(currentLocation, zombieLoc, 0, 255, 0);
//        MapLocation target = navigator.getTarget();
//
//        if (distToZombie > moveTowardDistance) {
//            rc.setIndicatorString(1, "moving to herd " + String.valueOf(zombieLoc));
//            if (MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(zombieLoc), false)) {
//                return true;
//            }
//
//        } else if (distToZombie <= moveAwayDistance && zombieInfo.weaponDelay <= 2) {
//
//            rc.setIndicatorString(1, "moving away from " + String.valueOf(zombieLoc));
//            Direction toMove = currentLocation.directionTo(target);
//            Direction away = zombieLoc.directionTo(currentLocation);
//            toMove = MapUtils.addDirections(toMove, away);
//
//            if (toMove.equals(Direction.NONE)) {
//                toMove = away;
//                if (rc.canMove(toMove.rotateLeft())) {
//                    rc.move(toMove.rotateLeft());
//                    return true;
//                }
//                if (rc.canMove(toMove.rotateRight())) {
//                    rc.move(toMove.rotateRight());
//                    return true;
//                }
//                if (rc.canMove(away)) {
//                    rc.move(away);
//                    return true;
//                }
//            } else {
//                if (rc.canMove(toMove)) {
//                    rc.move(toMove);
//                    return true;
//                }
//                // TODO: consider moving, right, check to see what the distance would be after moving that direction
//            }
//            toMove = MapUtils.getRCCanMoveDirection();
//            if (!toMove.equals(Direction.NONE)) {
//                rc.move(toMove);
//                return true;
//            }
//            return false;
//        } else {
//
//            if (MapUtils.canZombieMoveTowardMe(rc, currentLocation, zombieLoc)) {
//                // if we are on the right side of the zombie
//                rc.setIndicatorString(1, "waiting for zombie to move " + String.valueOf(zombieLoc));
//                return true;
//            } else {
//                // looks like the zombie is stuck on terrain or other zombies or neutrals
//                if (zombieLoc.distanceSquaredTo(target) < currentLocation.distanceSquaredTo(target)) {
//                    rc.setIndicatorString(1, "zombie can't move toward me and is in my way!");
//                    // lets try and move around the zombie!
//                    // lots of real bad code follows
//                    Direction toMove = MapUtils.addDirections(currentLocation.directionTo(target), zombieLoc.directionTo(currentLocation));
//                    if (!toMove.equals(Direction.NONE)) {
//                        if (rc.canMove(toMove)) {
//                            int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
//                            if (dist > moveAwayDistance && dist <= dontMoveDistance ) {
//                                rc.move(toMove);
//                                return true;
//                            }
//                        }
//                    }
//                    toMove = currentLocation.directionTo(target);
//                    if (id % 2 == 0) {
//                        toMove.rotateLeft().rotateLeft();
//                    } else {
//                        toMove.rotateRight().rotateRight();
//                    }
//                    if (rc.canMove(toMove)) {
//                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
//                        if (dist > moveAwayDistance && dist <= dontMoveDistance ) {
//                            rc.move(toMove);
//                            return true;
//                        }
//                    }
//                    rc.setIndicatorString(1, "zombie can't move toward me and is in my way! (no way around)");
//                    return true;
//                } else {
//                    // zombie can't move, but I'm closer to my target than it is.
//                    // let's try and move even CLOSER to my target, without leaving the zone
//                    int distToTarget= currentLocation.distanceSquaredTo(navigator.getTarget());
//                    rc.setIndicatorString(1, "zombie can't move toward me but I should be good!");
//                    Direction toMove = currentLocation.directionTo(target);
//                    if (rc.canMove(toMove)) {
//                        boolean closer = currentLocation.add(toMove).distanceSquaredTo(navigator.getTarget()) < distToTarget;
//                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
//                        if (closer && dist > moveAwayDistance && dist <= dontMoveDistance ) {
//                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
//                            rc.move(toMove);
//                            return true;
//                        }
//                    }
//                    toMove = toMove.rotateLeft();
//                    if (rc.canMove(toMove)) {
//                        boolean closer = currentLocation.add(toMove).distanceSquaredTo(navigator.getTarget()) < distToTarget;
//                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
//                        if (closer && dist > moveAwayDistance && dist <= dontMoveDistance ) {
//                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
//                            rc.move(toMove);
//                            return true;
//                        }
//                    }
//                    toMove = toMove.rotateRight().rotateRight();
//                    if (rc.canMove(toMove)) {
//                        boolean closer = currentLocation.add(toMove).distanceSquaredTo(navigator.getTarget()) < distToTarget;
//                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
//                        if (closer && dist > moveAwayDistance && dist <= dontMoveDistance ) {
//                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
//                            rc.move(toMove);
//                            return true;
//                        }
//                    }
//                    toMove = toMove.rotateRight();
//                    if (rc.canMove(toMove)) {
//                        boolean closer = currentLocation.add(toMove).distanceSquaredTo(navigator.getTarget()) < distToTarget;
//                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
//                        if (closer && dist > moveAwayDistance && dist <= dontMoveDistance ) {
//                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
//                            rc.move(toMove);
//                            return true;
//                        }
//                    }
//                    toMove = toMove.opposite();
//                    if (rc.canMove(toMove)) {
//                        boolean closer = currentLocation.add(toMove).distanceSquaredTo(navigator.getTarget()) < distToTarget;
//                        int dist = zombieLoc.distanceSquaredTo(currentLocation.add(toMove));
//                        if (closer && dist > moveAwayDistance && dist <= dontMoveDistance ) {
//                            rc.setIndicatorString(1, "zombie can't move toward me but I should be good! " + dist);
//                            rc.move(toMove);
//                            return true;
//                        }
//                    }
//
//                    Direction toZomb = currentLocation.directionTo(zombieLoc);
//                    if (rc.senseRubble(currentLocation.add(toZomb)) > 0) {
//                        rc.clearRubble(toZomb);
//                        rc.setIndicatorString(1, "there is rubble in the way, helping the zombie move by clearing it ");
//                        return true;
//                    }
//
//                    if (rc.senseRubble(currentLocation) > 0) {
//                        rc.clearRubble(Direction.NONE);
//                        rc.setIndicatorString(1, "there is rubble on my square, helping the zombie move by clearing it ");
//                        return true;
//                    }
//
//
//                    if (MoveUtils.tryClearAnywhere(toZomb)) {
//                        rc.setIndicatorString(1, "there is rubble on my around, helping the zombie move by clearing it ");
//                        return true;
//
//                    }
//                    rc.setIndicatorString(1, "nothing to do but wait for our zombie friend to figure hist **** out");
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//
//    public boolean herd() throws GameActionException {
//
//        if (closestZombieInfo.type.equals(RobotType.BIGZOMBIE) && closestZombie < MELEE_AVOID_DISTANCE) {
//            rc.setIndicatorString(2, "melee");
//            return herdZombie(closestZombie, closestZombieLoc, closestZombieInfo, MELEE_MOVE_AWAY_DISTANCE, MELEE_DONT_MOVE_DISTANCE, MELEE_MOVE_TOWARD_DISTANCE);
//        }
//
//        // if you can be attacked by a ranged or melee
//        if (closestRangedZombie <= RobotType.RANGEDZOMBIE.attackRadiusSquared) {
//            rc.setIndicatorString(2, "ranged");
//            return herdZombie(closestRangedZombie, closestRangedZombieLoc, closestRangedZombieInfo, RANGED_MOVE_AWAY_DISTANCE, RANGED_DONT_MOVE_DISTANCE, RANGED_MOVE_TOWARD_DISTANCE);
//        }
//        if (closestZombie <= 2) {
//            rc.setIndicatorString(2, "melee");
//            return herdZombie(closestZombie, closestZombieLoc, closestZombieInfo, MELEE_MOVE_AWAY_DISTANCE, MELEE_DONT_MOVE_DISTANCE, MELEE_MOVE_TOWARD_DISTANCE);
//        }
//
//
//        if (closestRangedZombie < RANGED_AVOID_DISTANCE) {
//            rc.setIndicatorString(2, "ranged");
//            return herdZombie(closestRangedZombie, closestRangedZombieLoc, closestRangedZombieInfo, RANGED_MOVE_AWAY_DISTANCE, RANGED_DONT_MOVE_DISTANCE, RANGED_MOVE_TOWARD_DISTANCE);
//        }
//
//        rc.setIndicatorString(2, "melee");
//        return herdZombie(closestZombie, closestZombieLoc, closestZombieInfo, MELEE_MOVE_AWAY_DISTANCE, MELEE_DONT_MOVE_DISTANCE, MELEE_MOVE_TOWARD_DISTANCE);
//    }
//
//
//    /**
//     * Avoid tries to stay SAFE DISTANCE away from all zombies
//     * // TODO: this HAS NOT been updated to tell the difference between ranged and melee
//     * @param zombieToAvoidLocation
//     * @return
//     * @throws GameActionException
//     *
//     *      Z
//     *
//     *      S               T
//     *        *
//     *
//     * toMove = East
//     * away = South
//     *
//     * toMove + away = SOUTH_EAST
//     *  * = MOVE THERE
//     *
//     *
//     *  Sometimes DIRECTION.NONE
//     *
//     *      S    Z       T
//     *      toMove = East
//     *      away = West
//     *        => NONE
//     *
//     */
//    private boolean avoid(MapLocation zombieToAvoidLocation) throws GameActionException {
//        Direction toMove = currentLocation.directionTo(navigator.getTarget());
//        Direction away = closestZombieLoc.directionTo(currentLocation);
//        toMove = MapUtils.addDirections(toMove, away);
//
//
//        if (!toMove.equals(Direction.NONE)) {
//            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieToAvoidLocation) > MIN_AVOID_DIST + 3) {
//                rc.move(toMove);
//                return true;
//            }
//        } else {
//
//            if (id % 2 == 0) {
//                toMove = away.rotateLeft();
//            } else {
//                toMove = away.rotateRight();
//            }
//            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieToAvoidLocation) > MIN_AVOID_DIST + 3) {
//                rc.move(toMove);
//                return true;
//            }
//
//            if (id % 2 == 0) {
//                toMove = away.rotateRight();
//            } else {
//                toMove = away.rotateLeft();
//            }
//            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieToAvoidLocation) > MIN_AVOID_DIST + 3) {
//                rc.move(toMove);
//                return true;
//            }
//
//            if (id % 2 == 0) {
//                toMove = away.rotateRight().rotateRight();
//            } else {
//                toMove = away.rotateLeft().rotateLeft();
//            }
//            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieToAvoidLocation) > MIN_AVOID_DIST + 3) {
//                rc.move(toMove);
//                return true;
//            }
//
//            if (id % 2 == 0) {
//                toMove = away.rotateLeft().rotateLeft();
//            } else {
//                toMove = away.rotateRight().rotateRight();
//            }
//            if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieToAvoidLocation) > MIN_AVOID_DIST + 3) {
//                rc.move(toMove);
//                return true;
//            }
//
//
//
//
//        }
//        return false;
//    }
//
//
//    /*
//    ===============================
//    MOVE INTO POSITION AROUND ENEMY
//    the idea is:
//        you see some enemies, but there are no zombies! :(
//        you need to wait for zombies to appear
//     //TODO: message if you see an archon?
//     // TODO: get a viper over here?
//     // TODO: try and move in range of an enemy viper?
//    ===============================
//     */
//    private boolean moveInPositionAroundEnemey() throws GameActionException {
//        if (!nonScoutEnemies) {
//            return false;
//        }
//
//        if (possibleEnemyDamageNextTurn > 0) {
//            rc.setIndicatorString(1, "Trying to move away");
//            Direction toMove = Direction.NONE;
//            // need to move away
//            for (int i = enemies.length; --i >= 0;) {
//                if (currentLocation.distanceSquaredTo(enemies[i].location) <= enemies[i].type.attackRadiusSquared) {
//                    toMove = MapUtils.addDirections(toMove, enemies[i].location.directionTo(currentLocation));
//                }
//            }
//            if (!toMove.equals(Direction.NONE)) {
//                if (rc.canMove(toMove)) {
//                    rc.move(toMove);
//                    return true;
//                }
//            }
//            toMove = closestEnemyLoc.directionTo(currentLocation);
//            if (MoveUtils.tryMoveForwardOrSideways(toMove, false)) {
//                return true;
//            }
//            //TODO: check out barracade. Notice that this code doesn't work very well
//            // TODO: we should definitely try and move in other directions (left, right, left.left, right.right)
//        } else {
//            rc.setIndicatorString(1, "We are fine!");
//            // TODO: possibly start circling the enemy location?
//            // TODO: possibly "herd" around  the enemy if you don't see an archon?
//            // TODO: more
//        }
//
//        return true;
//    }
//
//
//
//    /*
//    ===============================
//    FIND ENEMIES
//    ===============================
//     */
//    private boolean findEnemies() throws GameActionException {
//        // precondition
//        if (nonScoutEnemies) {
//            return false;
//        }
//        // TODO: consider a herder, that hugs the edge of the map if able to find the enemy
//
//
//        if (useWallHugger()) {
//            return wallNavigator.takeNextStep();
//        } else {
//            return navigator.takeNextStep(id % 5 <= 2, id % 2 == 0);
//        }
//    }
//
//    /**
//     * Gets the next archon loc to rush
//     * @return
//     */
//    private MapLocation nextPlaceToLookForEnemies() {
//        //TODO: play around with randomizing this
//        //TODO: consider changing this to closest archon loc (from map utils)
//        //TODO: consider changing this to furthest archon loc (from map utils)
//        idxForEnemyLocations++;
//        if (idxForEnemyLocations > enemyArchonStartLocs.length) {
//            idxForEnemyLocations = 0;
//        }
//        if (idxForEnemyLocations == enemyArchonStartLocs.length) {
//            return enemyCore;
//        }
//        return enemyArchonStartLocs[idxForEnemyLocations];
//    }
//
//    /*
//    ===============================
//    BRING ZOMBIES TO ENEMY
//    In this code, you have some zombies behind you that you herding
//    and you have some enemies somewhere
//    //TODO: improve this when not around enemy turtles / archons
//    ===============================
//     */
//    private boolean bringZombiesToEnemy() throws GameActionException {
//        // precondition
//        if (!(nonDenZombies && nonScoutEnemies)) {
//            return false;
//        }
//        // if the zombies are closer to the enemy than I am and we see an enemy archon, turn!
//        if (rc.senseNearbyRobots(closestEnemyLoc, closestEnemy, Team.ZOMBIE).length > 1) {
//            if (closestEnemyArchon < Integer.MAX_VALUE) {
//                return turnASAP();
//            }
//        }
//        if (possibleEnemyDamageNextTurn > 0 && rc.getInfectedTurns() < 5) {
//            if (possibleEnemyDamageNextTurn < 2 * rc.getHealth()) {
//                // wait for zombie to catch up!
//                rc.setIndicatorString(1, "waiting for infection");
//
//                Direction toZomb = currentLocation.directionTo(closestZombieLoc);
//                if (rc.senseRubble(currentLocation.add(toZomb)) > 0) {
//                    rc.clearRubble(toZomb);
//                    rc.setIndicatorString(1, "waiting for infection but there is rubble in the way, helping the zombie move by clearing it ");
//                    return true;
//                }
//
//                rc.setIndicatorString(1, "waiting for infection");
//                return true;
//            } else {
//                rc.setIndicatorString(1, "scared, moving to infection");
//                Direction toZombie = currentLocation.directionTo(closestZombieLoc);
//                if (rc.canMove(toZombie)) {
//                    rc.move(toZombie);
//                    return true;
//                }
//                if (rc.canMove(toZombie.rotateLeft())) {
//                    rc.move(toZombie.rotateLeft());
//                    return true;
//                }
//                if (rc.canMove(toZombie.rotateRight())) {
//                    rc.move(toZombie.rotateRight());
//                    return true;
//                }
//                return false;
//            }
//        } else {
//            rc.setIndicatorString(1, "good to go " + possibleEnemyDamageNextTurn);
//            return herd();
//        }
//    }
//
//    private boolean turnASAP() throws GameActionException {
//        if (rc.getInfectedTurns() > 0 && closestEnemyLoc != null && closestEnemyLoc.isAdjacentTo(currentLocation)) {
//            suicideScout();
//            return true;
//        }
//        if (rc.getInfectedTurns() == 2) {
//            suicideScout();
//            return true;
//        }
//        if (closestZombieLoc == null) {
//            return false;
//        }
//        if (rc.getInfectedTurns() == 0) {
//            if (closestZombieLoc.isAdjacentTo(currentLocation)) {
//                rc.setIndicatorString(1, "we want to turn and the zombie is adjacent, wait for infection!");
//                return true;
//            }
//            Direction toZombie = currentLocation.directionTo(closestZombieLoc);
//            if (MoveUtils.tryMoveForwardOrSideways(toZombie, false)) {
//                rc.setIndicatorString(1, "moving to get infected!");
//                return true;
//            }
//            rc.setIndicatorString(1, "can't move toward a zombie, waiting my turn!");
//            return true;
//        } else {
//            // we are infected and we want to turn, but we aren't adjacent to an enemy yet
//            if (!rc.isCoreReady()) {
//                // wait for next turn
//                rc.setIndicatorString(1, "I'm infected and want to move to an enemy but core isn't ready");
//                return true;
//            } else {
//                Direction toEnemy = currentLocation.directionTo(closestEnemyLoc);
//                if (MoveUtils.tryMoveForwardOrLeftRight(toEnemy, false)) {
//                    return true;
//                }
//                rc.setIndicatorString(1, "Can't move toward enemy, but I'll keep trying till infectino = 1");
//                return true;
//            }
//
//        }
//    }
//
//
//    /*
//    ===============================
//    HERD AWAY FROM ARCHON
//    ===============================
//     */
//    public boolean herdAwayFromArchon() {
//        // preconditions
//        /*
//        if (closestAlliedArchon == Integer.MAX_VALUE) {
//            return false;
//        }
//        RobotInfo[] zambies = rc.senseNearbyRobots(closestAlliedArchonLoc, closestAlliedArchon + 3, Team.ZOMBIE);
//        if (zombies.length == 0) {
//            return false;
//        }
//        */
//        // TODO: should we try to improve this?
//        // the guards drastically negate the necessity of this
//
//
//        return false;
//    }
//
//
//
//
//    private void suicideIfNeeded() throws GameActionException {
//        if (rc.getViperInfectedTurns() > 0) {
//            RobotInfo[] adjacent = rc.senseNearbyRobots(2, us);
//            if (adjacent.length > 2) {
//                suicideScout();
//            }
//        }
//
//
//
//        if (rc.getInfectedTurns() == 2 && rc.getHealth() < type.maxHealth / 2) {
//            if (closestEnemy < closestAlliedArchon) {
//                suicideScout();
//            }
//        }
//        if (rc.getInfectedTurns() == 5 && closestEnemyLoc != null && closestEnemyLoc.isAdjacentTo(currentLocation)) {
//            if (closestEnemy < closestAlliedArchon) {
//                suicideScout();
//            }
//        }
//    }
//
//    private void suicideScout() throws GameActionException {
//        int i = 0;
//        try {
//            while (Clock.getBytecodesLeft() > 200 && i < 15) {
//                rc.broadcastMessageSignal(Integer.MIN_VALUE, Integer.MIN_VALUE, 25);
//                i++;
//            }
//        } catch (GameActionException e) {
//
//        }
//
//        rc.disintegrate();
//    }
//
//    /**
//     * We WANT to turn into zombies so override this method
//     *
//     * @throws GameActionException
//     */
//    @Override
//    public void suicide() throws GameActionException
//    {
//        return;
//    }

}
