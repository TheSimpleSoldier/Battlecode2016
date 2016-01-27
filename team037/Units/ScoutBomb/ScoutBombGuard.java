package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Utilites.MapUtils;
import team037.Units.BaseUnits.BaseGaurd;
import team037.Utilites.MoveUtils;

public class ScoutBombGuard extends BaseGaurd {

    private static int closestEnemy;
    private static boolean nonScoutEnemies = false;
    private static MapLocation closestEnemyLoc;
    private static MapLocation lastArchonLoc;
    private static MapLocation archonLoc;
    private static int archonLastMoved = 0;
    private static boolean archonMoved = false;
    private static int archonId = Integer.MAX_VALUE;


    public ScoutBombGuard(RobotController rc) {
        super(rc);
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        // keep track of our archon
        archonMoved = false;
        if (archonId == Integer.MAX_VALUE) {
            RobotInfo[] bots = rc.senseNearbyRobots(2, us);
            for (int i = bots.length; --i >=0;) {
                if (bots[i].type.equals(RobotType.ARCHON)) {
                    archonId = bots[i].ID;
                    archonLoc = bots[i].location;
                    lastArchonLoc = bots[i].location;
                    archonLastMoved = round;
                    archonMoved = true;
                }
            }
        } else {
            if (rc.canSenseRobot(archonId)) {
                RobotInfo archon = rc.senseRobot(archonId);
                if (!archon.location.equals(archonLoc)) {
                    lastArchonLoc = archonLoc;
                    archonLoc = archon.location;
                    archonLastMoved = round;
                    archonMoved = true;
                }
            }
        }

        // enemy info
        closestEnemy = Integer.MAX_VALUE;
        closestEnemyLoc = null;
    }

    @Override
    public boolean act() throws GameActionException {
        // attack!
        if (rc.isWeaponReady() && closestEnemyLoc != null && closestEnemyLoc.isAdjacentTo(currentLocation)) {
            if (rc.canAttackLocation(closestEnemyLoc)) {
                rc.attackLocation(closestEnemyLoc);
                rc.setIndicatorString(0, "attacking enemy");
                return true;
            }
        }

        // if we see zombies, fight 'em!
        if (zombies.length > 0) {
            // if our archon is getting attack, move to them
            if (rc.isCoreReady() && rc.canSenseRobot(archonId) && rc.senseRobot(archonId).zombieInfectedTurns >= 8) {
                if (MoveUtils.tryMoveForwardOrLeftRight(currentLocation.directionTo(archonLoc), false)) {
                    rc.setIndicatorString(0, "moving to save");
                    return true;
                }
            }
            rc.setIndicatorString(0, "fight micro");
            return fightMicro.guardZombieMicro(zombies, nearByZombies, allies);
        }

        if (!rc.isCoreReady()) {
            rc.setIndicatorString(0, "waiting for core");
            return false;
        }

        // if we see enemies charge at them to lead them away from archon (hopefully they kite us)
        if (nonScoutEnemies) {
            Direction toMove = currentLocation.directionTo(closestEnemyLoc);
            if (MoveUtils.tryMoveForwardOrSideways(toMove, false)) {
                rc.setIndicatorString(0, "trying to get to enemy");
                return true;
            }
            if (archonLoc != null && MoveUtils.tryMoveForwardOrLeftRight(archonLoc.directionTo(currentLocation), false)) {
                rc.setIndicatorString(0, "can't get to enemy, trying to get away from archon");
                return true;
            }
            if (MoveUtils.tryClearAnywhere(toMove)) {
                rc.setIndicatorString(0, "can't get anywhere, clearing rubble");
                return true;
            }
        }

        if (archonLoc == null) {
            Direction toMove = currentLocation.directionTo(alliedArchonCenterOfMass).opposite();
            MoveUtils.tryMoveForwardOrSideways(toMove, true);
            return true;
        }

        // if we are too far away, move back
        if (currentLocation.distanceSquaredTo(archonLoc) > 8) {
            if (MoveUtils.tryMoveForwardOrSideways(currentLocation.directionTo(archonLoc), true)) {
                rc.setIndicatorString(0, "moving back to my archon!");
                return true;
            }
        }

        // move out the way!
        if (rc.senseNearbyRobots(2, us).length > 3) {
            Direction rand = MapUtils.getRCCanMoveDirection();
            rc.setIndicatorString(0, "moving out the way!");
            if (rc.canMove(rand)) {
                rc.move(rand);
                return true;
            }
        }

        // clear rubble
        for (int i = dirs.length; --i>=0; ) {
            MapLocation next = currentLocation.add(dirs[i]);
            rc.setIndicatorString(0, "clearing rubble!");
            if (rc.canSense(next) && rc.senseRubble(next) > 0) {
                rc.clearRubble(dirs[i]);
                return true;
            }
        }

        // move randomly
        Direction rand = MapUtils.getRCCanMoveDirection();
        if (rc.canMove(rand)) {
            rc.setIndicatorString(0, "moving randomly!");
            rc.move(rand);
            return true;
        }

        return false;
    }


    @Override
    public void sendMessages() {
        return;
    }

    @Override
    public void handleMessages() throws GameActionException {
        return;
    }
}
