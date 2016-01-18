package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Utilites.MapUtils;
import team037.Units.BaseUnits.BaseGaurd;

public class ScoutBombGuard extends BaseGaurd {
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
    }

    @Override
    public boolean updateTarget() throws GameActionException {
        return archonMoved;
    }

    @Override
    public boolean carryOutAbility() throws GameActionException {

        if (rc.isCoreReady() && archonLastMoved > 10 && currentLocation.distanceSquaredTo(archonLoc) < 16) {

            // move out the way!
            if (rc.senseNearbyRobots(2, us).length > 2) {
                Direction rand = MapUtils.getRCCanMoveDirection(this);
                if (rc.canMove(rand)) {
                    rc.move(rand);
                    return true;
                }
            }

            // clear rubble
            for (int i = dirs.length; --i>=0; ) {
                MapLocation next = currentLocation.add(dirs[i]);
                if (rc.canSense(next) && rc.senseRubble(next) > 0) {
                    rc.clearRubble(dirs[i]);
                    return true;
                }
            }

            // move randomly
            Direction rand = MapUtils.getRCCanMoveDirection(this);
            if (rc.canMove(rand)) {
                rc.move(rand);
                return true;
            }
        }

        return false;
    }

    @Override
    public MapLocation getNextSpot() {
        if (archonLoc != null) {
            return archonLoc.add(archonLoc.directionTo(currentLocation), 2);
        }
        if (lastArchonLoc != null) {
            return lastArchonLoc.add(lastArchonLoc.directionTo(currentLocation), 2);
        }
        return null;
    }

}
