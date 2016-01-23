package team037.Units.Scavenger;

import battlecode.common.*;
import team037.Navigation;
import team037.Units.BaseUnits.BaseGaurd;
import team037.Units.PacMan.PacMan;
import team037.Units.PacMan.PacManUtils;

public class CountermeasureGuard extends BaseGaurd implements PacMan {

    public static MapLocation archonLastLoc;
    public static RobotInfo myArchon;
    public static MapLocation zombieCenterOfMass, enemyCenterOfMass, lastScan;
    public static int roundsSurvived;
    public static boolean zombieAdjacentToArchon;

    public CountermeasureGuard(RobotController rc) {
        super(rc);
        roundsSurvived = 0;
        zombieCenterOfMass = null;
        lastScan = null;
        zombieAdjacentToArchon = false;
            try {
                myArchon = null;
                for (int i = 8; --i >= 0; ) {
                    MapLocation loc = currentLocation.add(dirs[i]);
                    if (rc.isLocationOccupied(loc)) {
                        RobotInfo checkBot = rc.senseRobotAtLocation(loc);
                        if (checkBot.type.equals(RobotType.ARCHON)) {
                            if (myArchon == null)
                                myArchon = checkBot;
                            else {
                                myArchon = null;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {}

            if (myArchon != null) {
                archonLastLoc = myArchon.location;
            }
    }

    public boolean act() throws GameActionException {
        if (myArchon == null) {
            roundsSurvived++;
            navigator.setTarget(enemyArchonCenterOfMass);
            return super.act();
        } else {
            if (zombieCenterOfMass != null && zombies.length > 0) {
                navigator.setTarget(zombieCenterOfMass);
            } else if (enemyCenterOfMass != null && enemies.length > 0) {
                navigator.setTarget(enemyCenterOfMass);
            } else {
                navigator.setTarget(enemyArchonCenterOfMass);
            }
            if (zombieAdjacentToArchon && currentLocation.isAdjacentTo(myArchon.location)) {
                return fightZombies();
            }

            if (rc.isCoreReady()) {
                if (rc.canMove(currentLocation.directionTo(myArchon.location).opposite())) {
                    try {
                        rc.move(currentLocation.directionTo(myArchon.location).opposite());
                        return true;
                    } catch (Exception e) {
                    }
                } else if (rc.canMove(currentLocation.directionTo(myArchon.location).opposite().rotateLeft())) {
                    try {
                        rc.move(currentLocation.directionTo(myArchon.location).opposite().rotateLeft());
                        return true;
                    } catch (Exception e) {
                    }
                } else if (rc.canMove(currentLocation.directionTo(myArchon.location).opposite().rotateRight())) {
                    try {
                        rc.move(currentLocation.directionTo(myArchon.location).opposite().rotateRight());
                        return true;
                    } catch (Exception e) {
                    }
                }
            }
            roundsSurvived++;
            return fightZombies();
        }
    }

    public int[] applyAdditionalConstants(int[] directions) {
        if (myArchon != null) {
            directions = PacManUtils.applySimpleConstant(currentLocation, directions, myArchon.location, new int[]{999999,9999,0});
        } else if (archonLastLoc != null) {
            directions = PacManUtils.applySimpleConstant(currentLocation, directions, archonLastLoc, new int[]{999999,9999,0});
        } else {
            directions = PacManUtils.applySimpleConstant(currentLocation, directions, enemyArchonCenterOfMass, new int[]{-32,-16,-8});
        }
        return directions;
    }

    public void collectData() throws GameActionException {
        super.collectData();
        if (myArchon != null && !rc.canSenseRobot(myArchon.ID)) {
            myArchon = null;
        }

        if (myArchon == null) {
            for (int i = allies.length; --i >= 0;) {
                if (allies[i].type.equals(RobotType.ARCHON)) {
                    myArchon = allies[i];
                    break;
                }
            }
        }

        // Sense rubble on adjacent locations
        // Scan 24 radius squared for rubble topography
        if (!currentLocation.equals(lastScan)) {
            Navigation.map.scan(currentLocation);
            lastScan = currentLocation;
            Navigation.lastScan = lastScan;
        }

        int x = 0, y = 0;
        if (zombies.length > 0) {
            for (int i = zombies.length; --i >= 0; ) {
                MapLocation zombieLoc = zombies[i].location;
                x += zombieLoc.x;
                y += zombieLoc.y;
                if (myArchon != null && zombieLoc.isAdjacentTo(myArchon.location)) {
                    zombieAdjacentToArchon = true;
                }
            }

            x /= zombies.length;
            y /= zombies.length;

            zombieCenterOfMass = new MapLocation(x, y);
        } else {
            zombieCenterOfMass = null;
        }
        x = 0;
        y = 0;

        if (enemies.length > 0) {
            for (int i = enemies.length; --i >= 0; ) {
                MapLocation enemyLoc = enemies[i].location;
                x += enemyLoc.x;
                y += enemyLoc.y;
            }

            x /= enemies.length;
            y /= enemies.length;

            enemyCenterOfMass = new MapLocation(x, y);
        } else {
            enemyCenterOfMass = null;
        }
    }
}
