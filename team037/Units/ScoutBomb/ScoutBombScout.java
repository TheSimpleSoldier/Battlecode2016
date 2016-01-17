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
    private static int idxForEnemyLocations = -1;
    private static MapLocation enemyCore;
    private static boolean nonDenZombies = false;
    private static boolean nonScoutEnemies = false;
    private static boolean rushForward = false;
    private static MapLocation alliedCenter;

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
        enemyCore = MapUtils.getCenterOfMass(enemyArchonStartLocs);
        alliedCenter = MapUtils.getCenterOfMass(alliedArchonStartLocs);
        navigator.setTarget(nextPlaceToLookForEnemies());

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
            RobotInfo zombie = veryCloseZombies[i];
            if (!zombie.type.equals(RobotType.ZOMBIEDEN)) {
                int dist = zombie.location.distanceSquaredTo(currentLocation);
                if (dist < closestZombie) {
                    closestZombie = dist;
                    closestZombieLoc = zombie.location;
                    closestZombieInfo = zombie;
                }
                nonDenZombies = true;
            }
        }


        // enemy info
        nonScoutEnemies = false;
        possibleEnemyDamageNextTurn = 0;
        closestEnemy = Integer.MAX_VALUE;
        closestEnemyLoc = null;
        boolean archon = false;
        for (int i = enemies.length; --i>=0;) {
            int distance =  enemies[i].location.distanceSquaredTo(currentLocation);
            if (distance < closestEnemy) {
                closestEnemy = distance;
                closestEnemyLoc = enemies[i].location;
            }
            switch(enemies[i].type) {
                case ARCHON:
                    nonScoutEnemies = true;
                    navigator.setTarget(enemies[i].location);
                    break;
                case SCOUT:
                    break;
                case GUARD:
                    nonScoutEnemies = true;
                    if (distance <= 2 * RobotType.GUARD.attackRadiusSquared) {
                        possibleEnemyDamageNextTurn += RobotType.GUARD.attackPower;
                    }
                    break;
                case SOLDIER:
                    nonScoutEnemies = true;
                    if (distance <= RobotType.SOLDIER.attackRadiusSquared + 8) {
                        possibleEnemyDamageNextTurn += RobotType.SOLDIER.attackPower;
                    }
                    break;
                case TTM:
                    nonScoutEnemies = true;
                    break;
                case TURRET:
                    nonScoutEnemies = true;
                    if (distance <= RobotType.TURRET.attackRadiusSquared + 1) {
                        possibleEnemyDamageNextTurn += RobotType.TURRET.attackPower;
                    }
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
        Signal[] signals = rc.emptySignalQueue();
        for (int i = signals.length; --i >= 0;) {
            if (signals[i].getTeam().equals(us)) {
                int[] message = signals[i].getMessage();
                if (message[0] == Integer.MIN_VALUE && message[1] == Integer.MIN_VALUE) {
                    rc.setIndicatorDot(currentLocation, 255, 0, 0);
                    rushForward = true;
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
            navigator.takeNextStep();
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

        int distToZombie = currentLocation.distanceSquaredTo(closestZombieLoc);
        RobotInfo[] friends = rc.senseNearbyRobots(closestZombieLoc, distToZombie - 1, us);

        if (friends.length > 0) {
            return avoid(closestZombieLoc);
        } else {
            return herd(distToZombie);
        }

    }

    public boolean herd(int distToZombie) throws GameActionException {
        if (distToZombie > MELEE_MOVE_IN_HERD_DIST) {
            rc.setIndicatorString(1, "moving to herd " + String.valueOf(closestZombieLoc));
            if (rc.canMove(currentLocation.directionTo(closestZombieLoc))) {
                rc.move(currentLocation.directionTo(closestZombieLoc));
                return true;
            }
        } else if (distToZombie <= ZERO_MELEE_HERD_DIST && (closestZombieInfo.coreDelay <= 2 || closestZombieInfo.weaponDelay <= 2)) {
            rc.setIndicatorString(1, "moving away from " + String.valueOf(closestZombieLoc));
            Direction toMove = currentLocation.directionTo(navigator.getTarget());
            Direction away = closestZombieLoc.directionTo(currentLocation);
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
            rc.setIndicatorString(1, "chillin " + String.valueOf(closestZombieLoc));
            return true;
        }
        return false;

    }

    private boolean avoid(MapLocation zombieCenter) throws GameActionException {
        Direction toMove = currentLocation.directionTo(navigator.getTarget());
        Direction away = closestZombieLoc.directionTo(currentLocation);
        toMove = MapUtils.addDirections(toMove, away);
        if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
            rc.move(toMove);
            return true;
        }

        toMove = MapUtils.addDirections(toMove, away);
        if (rc.canMove(toMove) && currentLocation.add(toMove).distanceSquaredTo(zombieCenter) > MIN_AVOID_DIST) {
            rc.move(toMove);
            return true;
        }

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
            return turnASAP();
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
            int distToZombie = currentLocation.distanceSquaredTo(closestZombieLoc);
            return herd(distToZombie);
        }
    }

    private boolean turnASAP() throws GameActionException {
        if (rc.getInfectedTurns() > 0) {
            suicideScout();
            return true;
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

    public boolean herdAwayFromArchon() {
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
