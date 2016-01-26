package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.DataStructures.RobotTypeTracker;
import team037.Units.BaseUnits.BaseSoldier;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.MapUtils;
import team037.Utilites.MoveUtils;

public class TurtleSoldier extends BaseSoldier
{

    public TurtleSoldier(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, enemyArchonStartLocs);
    }


    private static int nearestEnemyArchon;
    private static RobotInfo nearestEnemyArchonInfo;
    private static int nearestCombatEnemy;
    private static RobotInfo nearestCombatEnemyInfo;
    private static boolean nonScoutEnemies;

    private static int nearestMeleeZombie;
    private static RobotInfo nearestMeleeZombieInfo;
    private static int nearestBigZombie;
    private static RobotInfo nearestBigZombieInfo;
    private static int nearestRangedZombie;
    private static RobotInfo nearestRangedZombieInfo;
    private static int nearestDen;
    private static RobotInfo nearestDenInfo;
    private static boolean justDen;

    private static int nearestArchon;
    private static RobotInfo nearestArchonInfo;
    private static int nearestScout;
    private static RobotInfo nearestScoutInfo;
    private static int nearestTurret;
    private static RobotInfo nearestTurretInfo;

    private static boolean coreReady;
    private static boolean weaponReady;

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        coreReady = rc.isCoreReady();
        weaponReady = rc.isWeaponReady();

        nearestCombatEnemy = Integer.MAX_VALUE;
        nearestCombatEnemyInfo = null;
        nearestCombatEnemy = Integer.MAX_VALUE;
        nearestCombatEnemyInfo = null;
        nearestEnemyArchon = Integer.MAX_VALUE;
        nearestEnemyArchonInfo = null;

        nonScoutEnemies = false;
        for (int i = enemies.length; --i >= 0; ) {
            int dist = enemies[i].location.distanceSquaredTo(currentLocation);
            switch (enemies[i].type) {
                case SOLDIER:
                case VIPER:
                case GUARD:
                case TTM:
                case TURRET:
                    nonScoutEnemies = true;
                    if (dist < nearestCombatEnemy) {
                        nearestCombatEnemy = dist;
                        nearestCombatEnemyInfo = enemies[i];
                    }
                    break;

                case ARCHON:
                    if (dist < nearestEnemyArchon) {
                        nearestEnemyArchon = dist;
                        nearestEnemyArchonInfo = enemies[i];
                    }
                    nonScoutEnemies = true;
                    break;
                case SCOUT:
                    break;

            }
        }

        nearestBigZombie = Integer.MAX_VALUE;
        nearestBigZombieInfo = null;
        nearestMeleeZombie = Integer.MAX_VALUE;
        nearestMeleeZombieInfo = null;
        nearestRangedZombie = Integer.MAX_VALUE;
        nearestRangedZombieInfo = null;
        nearestDen = Integer.MAX_VALUE;
        nearestDenInfo = null;
        justDen = true;

        for (int i = zombies.length; --i >= 0;) {
            int dist = zombies[i].location.distanceSquaredTo(currentLocation);
            switch (zombies[i].type) {
                case FASTZOMBIE:
                case STANDARDZOMBIE:
                    justDen = false;
                    if (dist < nearestMeleeZombie) {
                        nearestMeleeZombie = dist;
                        nearestMeleeZombieInfo = zombies[i];
                    }
                    break;
                case BIGZOMBIE:
                    justDen = false;
                    if (dist < nearestBigZombie) {
                        nearestBigZombie = dist;
                        nearestBigZombieInfo = zombies[i];
                    }
                    break;
                case RANGEDZOMBIE:
                    justDen = false;
                    if (dist < nearestRangedZombie) {
                        nearestRangedZombie = dist;
                        nearestRangedZombieInfo = zombies[i];
                    }
                case ZOMBIEDEN:
                    if (dist < nearestDen) {
                        nearestDen = dist;
                        nearestDenInfo = zombies[i];
                    }
            }
        }

        nearestArchon = Integer.MAX_VALUE;
        nearestArchonInfo = null;
        nearestTurret = Integer.MAX_VALUE;
        nearestTurretInfo = null;
        nearestScout = Integer.MAX_VALUE;
        nearestScoutInfo = null;

        for (int i = allies.length; --i >= 0;) {
            int dist = allies[i].location.distanceSquaredTo(currentLocation);
            switch(allies[i].type) {
                case TURRET:
                case TTM:
                    if (dist < nearestTurret) {
                        nearestTurret = dist;
                        nearestTurretInfo = allies[i];
                    }
                break;
                case SCOUT:
                    if (dist < nearestScout) {
                        nearestScout = dist;
                        nearestScoutInfo = allies[i];
                    }
                case ARCHON:
                    if (dist < nearestArchon) {
                        nearestArchon = dist;
                        nearestArchonInfo = allies[i];
                    }
            }
        }

    }

    // additional methods with default behavior
    public void handleMessages() throws GameActionException
    {
        communications = communicator.processCommunications();
        for (int k = communications.length; --k >= 0; )
        {
            switch (communications[k].opcode)
            {
                case SARCHON:
                case SENEMY:
                case SZOMBIE:
                case SDEN:
                case SPARTS:
                    int values[] = communications[k].getValues();

                    if (values.length >= 6)
                    {
                        int id = values[3];

                        if (RobotTypeTracker.contains(id))
                        {
                            int x = values[4];
                            int y = values[5];

                            MapLocation target = new MapLocation(x, y);
                            rc.setIndicatorString(2, "received msg from turrets: " + target.x + " y: " + target.y);
                            navigator.setTarget(target);
                        }
                    }

                    break;

                case CHANGEMISSION:
                    if (missionComs)
                    {
                        interpretMissionChange(communications[k]);
                    }
                    break;
                case ATTACK:
                case RALLY_POINT:
                    if (archonComs)
                    {
                        interpretLocFromArchon(communications[k]);
                    }
                    break;
                case ARCHON_DISTRESS:
                    if (archonDistressComs)
                    {
                        interpretDistressFromArchon(communications[k]);
                    }
                    break;
            }
        }
    }


    private static final String ENEMIES_IN_OPEN = "enemies in the open";
    private static final String ZOMBIES_IN_OPEN = "zombies in the open";
    private static final String MOVE_TO_TURTLE_POINT = "zombies in the open";
    private static final String ZOMBIES_NEAR_RALLY_POINT = "zombies near rally point";
    private static final String ENEMIES_NEAR_RALLY_POINT = "enemies near rally point";
    private static final String MOVE_TO_COMBAT = "move to combat";
    private static final String PATROL_TURTLE_POINT = "patrol turtle point";

    @Override
    public boolean act() throws GameActionException {
        String lastAction = "NONE";
        if (turtlePoint != null) {
            rc.setIndicatorLine(currentLocation, turtlePoint, 0, 255, 0);
        }
        if (nearRallyPoint()) {
            if (zombiesNearTurtlePoint()) {
                lastAction = ZOMBIES_NEAR_RALLY_POINT;
            } else if (enemiesNearTurtlePoint()) {
                lastAction = ENEMIES_NEAR_RALLY_POINT;
            } else if (moveToCombat()) {
                lastAction = MOVE_TO_COMBAT;
            } else if (patrolTurtlePoint()) {
                lastAction = PATROL_TURTLE_POINT;
            }
        } else {
            if (enemiesInOpen()) {
                lastAction = ENEMIES_IN_OPEN;
            } else if (zombiesInOpen()) {
                lastAction = ZOMBIES_IN_OPEN;
            } else if (moveToTurtlePoint()) {
                lastAction = MOVE_TO_TURTLE_POINT;
            }

        }
        rc.setIndicatorString(0, lastAction + " " + round);
        return !lastAction.equals("NONE");

    }

    /*
    PATROL_TURTLE POINT
     */
    private boolean patrolTurtlePoint() throws GameActionException {
        // prereq
        if (!coreReady) {
            return false;
        }
        if (nearestTurret > 2 && nearestScout > 2)  {
            if (nearestTurret < nearestScout) {
                return MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestTurretInfo.location).rotateLeft(), true);
            }
        }

        Direction toMove = currentLocation.directionTo(turtlePoint).rotateRight().rotateRight().rotateRight();
        return MoveUtils.tryMoveForwardOrLeftRight(toMove, false);
    }

    /*
    MOVE TO COMBAT
     */
    private boolean moveToCombat() {
        return false;
    }

    /*
    ENEMIES NEAR TURTLE POINT
     */
    private boolean enemiesNearTurtlePoint() throws GameActionException {
        // prereqs
        if (!nonScoutEnemies) {
            return false;
        }
        if (weaponReady && nearestCombatEnemy <= type.attackRadiusSquared) {
            rc.attackLocation(nearestCombatEnemyInfo.location);
            return true;
        }
        if (!coreReady) {
            return false;
        }
        Direction forward = currentLocation.directionTo(turtlePoint).rotateLeft().rotateLeft();
        if (currentLocation.add(forward).distanceSquaredTo(nearestCombatEnemyInfo.location) < nearestCombatEnemy) {
            if (rc.canMove(forward)) {
                rc.move(forward);
                return true;
            }
        }

        Direction back = forward.opposite();
        if (currentLocation.add(back).distanceSquaredTo(nearestCombatEnemyInfo.location) < nearestCombatEnemy) {
            if (rc.canMove(back)) {
                rc.move(back);
                return true;
            }
        }
        return false;
    }

    /*
    ZOMBIES NEAR TURTLE POINT
     */
    private boolean zombiesNearTurtlePoint() throws GameActionException {
        if (zombies.length == 0) {
            return false;
        }

        if (justDen) {
            return zombiesInOpen();
        }
        if (weaponReady) {
            if (nearestBigZombie <= type.attackRadiusSquared) {
                rc.attackLocation(nearestBigZombieInfo.location);
                return true;
            }
            if (nearestMeleeZombie == type.attackRadiusSquared) {
                rc.attackLocation(nearestMeleeZombieInfo.location);
                return true;
            }
            if (nearestRangedZombie == type.attackRadiusSquared) {
                rc.attackLocation(nearestRangedZombieInfo.location);
                return true;
            }
        }
        if (coreReady) {
            if (nearestBigZombie > 2 || nearestMeleeZombie > 2 || nearestRangedZombie > 2) {

            }
            else if (nearestBigZombie <= 2) {
                return MoveUtils.tryMoveForwardOrLeftRight(nearestBigZombieInfo.location.directionTo(currentLocation), false);
            }

            if (nearestTurret > 2 && nearestTurret < Integer.MAX_VALUE) {
                return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestTurretInfo.location), false);
            } else if (nearestScout > 2 && nearestScout < Integer.MAX_VALUE) {
                return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestScoutInfo.location), false);
            }
        }

        return patrolTurtlePoint();
    }

    /*
    MOVE_TO_TURTLE_POINT
     */
    private boolean moveToTurtlePoint() throws GameActionException {
        // prereqs
        if (!coreReady) {
            return false;
        }
        if (nearestArchon < 2) {
            if (MoveUtils.tryMoveForwardOrLeftRight(nearestArchonInfo.location.directionTo(currentLocation), true)) {
                return true;
            }
        }
        if (nearestArchon > 9 && nearestArchon > Integer.MAX_VALUE) {
            if (MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(nearestArchonInfo.location), true)) {
                return true;
            }
        }
        if (turtlePoint != null) {
            return MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(turtlePoint), true);
        }
        return false;
    }

    /*
    ZOMBIES IN OPEN
      Fight the zombies in the wild!
     */
    private boolean zombiesInOpen() throws GameActionException {
        // precondition
        if (zombies.length == 0) {
            return false;
        }

        if (justDen) {
            if (weaponReady && rc.canAttackLocation(nearestDenInfo.location)) {
                rc.attackLocation(nearestDenInfo.location);
            }
            if (coreReady && currentLocation.distanceSquaredTo(nearestDenInfo.location) > 8 && MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(nearestDenInfo.location), true)) {
                return true;
            }
        }

        return fightMicro.soldierZombieFightMicro(zombies, nearByZombies, allies);
    }


    /*
    NEAR RALLY POINT
       Are we close to the rally point?
     */
    private boolean nearRallyPoint() {
        return currentLocation.distanceSquaredTo(turtlePoint) > 100;
    }

    /*
    ENEMIES_IN_OPEN
       What should we do if we see an enemy in the open?
     */
    private boolean enemiesInOpen() throws GameActionException {
        // precondition
        if (!nonScoutEnemies) {
            return false;
        }

        return fightMicro.basicNetFightMicro(nearByEnemies, nearByAllies, enemies, allies, turtlePoint);
    }

}
