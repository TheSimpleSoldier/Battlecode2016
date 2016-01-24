package team037;

/**
 * Navigation based on Jump Point Search (a flavor of A*) utilizing arrays of
 * bits to store the map in memory. This enables the use of efficient bit
 * manipulation to determine the number of trailing zeroes, leading zeroes,
 * trailing ones, and leading ones between any two points. The number of
 * trailing or leading zeroes represents the walkable distance from one point in
 * the direction of another point, and the number of trailing or leading ones
 * represents the number of non-walkable spaces from one point in the direction
 * to another point.
 * <p>
 * Jump Point Search can be more efficient than vanilla A* when the cost of
 * creating, adding and removing nodes to a queue is high and the cost of
 * checking many spaces is low. In the case of Battlecode, it should be far more
 * efficient in terms of bytecode use, able to find paths in a fraction of turns
 * relative to the path's length.
 *
 * @author David
 */

import battlecode.common.*;
import battlecode.world.signal.RubbleChangeSignal;
import team037.Utilites.RubbleUtilities;

public class Navigation {

    public static Map map;    // Map object to hold the bit arrays and the origin.
    static Multimap multimap;  // Priority multimap for the current search.
    static boolean searching, reachedGoal;  // Flags for search status
    static final int bytecodeLimit = 4000; // Bytecode limit
    static int pathLength;
    static RobotController rc = null;
    static JumpPoint pathStart, lastPt, myLoc, goalJP;
    public static MapLocation lastScan, currentGoal;

    /**
     * Initializes variables navigation relies on. Will only initialize once per game.
     * @param rc_in unit's RobotController instance
     */
    public static void initialize(RobotController rc_in) {
        if (rc == null) {
            rc = rc_in;
            map = new Map(rc);
            lastScan = rc.getLocation();
            try {
                map.scanImmediateVicinity(lastScan);
            } catch (Exception e) {e.printStackTrace();}

            reachedGoal = false;
            searching = false;
            multimap = null;
            lastPt = null;
            goalJP = null;
            int[] getMyLoc = map.mapToArray(lastScan);
            myLoc = new JumpPoint(getMyLoc[0], getMyLoc[1]);
            currentGoal = lastScan;
            pathLength = Integer.MAX_VALUE;
        }
    }

    /**
     * Reset variables for new search.
     */
    public static void reset() {
        reachedGoal = false;
        searching = false;
        multimap = null;
        lastPt = null;
        goalJP = null;
        MapLocation location = rc.getLocation();
        int[] getMyLoc = map.mapToArray(location);
        myLoc = new JumpPoint(getMyLoc[0], getMyLoc[1]);
        pathLength = Integer.MAX_VALUE;
    }

    /**
     * Attempt to bug towards the goal. If rubble blocks the way, do not move.
     *
     * @param currentLoc current location of unit
     * @param goal unit's target location
     * @return True if unit moved, false otherwise
     */
    public static boolean tryBug(MapLocation currentLoc, MapLocation goal) throws GameActionException {

        boolean moved;

        Direction forward = currentLoc.directionTo(goal);
        Direction right = forward.rotateRight();
        Direction left = forward.rotateLeft();

        MapLocation front = currentLoc.add(forward);
        MapLocation frontRight = currentLoc.add(right);
        MapLocation frontLeft = currentLoc.add(left);

        if (rc.canMove(forward)) {
            rc.move(forward);
            moved = true;
        } else if (rc.canMove(right)) {
            rc.move(right);
            moved = true;
        } else if (rc.canMove(left)) {
            rc.move(left);
            moved = true;
        } else if (rc.isLocationOccupied(front) && rc.isLocationOccupied(frontRight) && rc.isLocationOccupied(frontLeft)) {
            // front three locations are occupied, back up if possible
            left = left.rotateLeft();
            right = right.rotateRight();
            if (rc.canMove(right)) {
                rc.move(right);
                moved = true;
            } else if (rc.canMove(left)) {
                rc.move(left);
                moved = true;
            } else if (rc.canMove(right.rotateRight())) {
                rc.move(right.rotateRight());
                moved = true;
            } else if (rc.canMove(forward.opposite())) {
                rc.move(forward.opposite());
                moved = true;
            } else {
                moved = false;
            }
        } else {
            pathLength = Integer.MAX_VALUE;
            moved = false;
        }

        return moved;
    }

    /**
     * Attempt to move along a path returned by a completed search.
     * @param currentLoc unit's current location
     * @return True if unit moved, false otherwise
     * @throws GameActionException
     */
    public static boolean tryPath(MapLocation currentLoc) throws GameActionException {
        boolean moved = false;

        if (lastPt != null && lastPt.mapNext != null) {

            JumpPoint nextPt = lastPt.mapNext;

            if (myLoc.equals(nextPt)) {
                lastPt = nextPt;
                nextPt = nextPt.mapNext;
                if (nextPt != null && myLoc.equals(nextPt)) {
                    lastPt = nextPt;
                    nextPt = nextPt.mapNext;
                }
            }

            if (nextPt != null) {

                int direction = myLoc.directionTo(nextPt);
                if (direction < 0) {
                    return false;
                }
                Direction forward = Unit.dirs[direction];

                if (rc.canMove(forward)) {
                    rc.move(forward);
                    myLoc.move(direction);
                    moved = true;
                } else if (rc.isLocationOccupied(currentLoc.add(forward))) {
                    moved = handleTrafficOnPath(currentLoc, forward, direction, nextPt);
                }
            }
        }
        return moved;
    }

    public static boolean dig(MapLocation currentLoc, MapLocation goal) throws GameActionException {

        Direction forward = currentLoc.directionTo(goal);

        if (forward == Direction.NONE || forward == Direction.OMNI)
            return false;

        Direction right = forward.rotateRight();
        Direction left = forward.rotateLeft();

        MapLocation front = currentLoc.add(forward);
        MapLocation frontRight = currentLoc.add(right);
        MapLocation frontLeft = currentLoc.add(left);

        double rubbleFront = rc.senseRubble(front);
        double rubbleRight = rc.senseRubble(frontRight);
        double rubbleLeft = rc.senseRubble(frontLeft);

        if (rubbleRight > 50 && rubbleRight < rubbleFront) {
            rc.clearRubble(right);
            return true;
        } else if (rubbleLeft > 50 && rubbleLeft < rubbleFront) {
            rc.clearRubble(left);
            return true;
        } else if (rubbleFront > 50) {
            rc.clearRubble(forward);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempt to move. Scans local vicinity if unit moves.
     * @param goal this unit's target location
     * @return True if unit moved, false otherwise
     * @throws GameActionException
     */
    public static boolean moveTTM(MapLocation goal) throws GameActionException {
        boolean moved;
        MapLocation currentLoc = rc.getLocation();

        if (!currentLoc.equals(lastScan)) {
            if (searching) {
                map.scanImmediateVicinity(currentLoc);
            } else {
                map.scan(currentLoc);
            }
            lastScan = currentLoc;
        }

        if (goal != null) {
            if (currentGoal != null && !goal.equals(currentGoal)) {
                reset();
                currentGoal = goal;
            }

            if (!searching) {
                if (rc.isCoreReady()) {
                    // We can move this round; attempt to move.
                    if ((reachedGoal && tryPath(currentLoc)) || tryBug(currentLoc, goal)) {
                        // We moved
                        currentLoc = rc.getLocation();
                        int[] loc = map.mapToArray(currentLoc);
                        myLoc = new JumpPoint(loc[0], loc[1]);
                        moved = true;
                    } else {
                        // We did not move; verify location and initiate search.
                        currentLoc = rc.getLocation();
                        reset();

                        int[] loc = map.mapToArray(currentLoc);
                        myLoc = new JumpPoint(loc[0], loc[1]);

                        pathStart = getPath(loc, map.mapToArray(goal));
                        lastPt = pathStart;
                        moved = false;
                    }
                } else {
                    // Cannot move this round, perform perimeter
//                map.perimeter(currentLoc);
                    moved = false;
                }
            } else {
                // continue search
                pathStart = getPath(null, null);
                moved = false;
            }
        } else {
            moved = false;
        }

        return moved;
    }

    /**
     * Attempt to move. Scans local vicinity if unit moves.
     * @param goal this unit's target location
     * @return True if unit moved, false otherwise
     * @throws GameActionException
     */
    public static boolean move(MapLocation goal) throws GameActionException {
        boolean moved;
        MapLocation currentLoc = rc.getLocation();

        if (!currentLoc.equals(lastScan)) {
            int bytecodes = Clock.getBytecodesLeft();
            if (searching || bytecodes < 2500) {
                map.scanImmediateVicinity(currentLoc);
                lastScan = currentLoc;
            } else {
                map.scan(currentLoc);
                lastScan = currentLoc;
            }
        }

        if (goal != null) { // Only move if we have a goal

            if (currentGoal != null && !goal.equals(currentGoal)) { // Reset the search if our goal has changed
                reset();
                currentGoal = goal;
            } else if (currentLoc.equals(goal)) { // We don't need to move if we're already at the goal.
                reset();
                currentGoal = goal;
                return false;
            }

            if (!digging()) { // If there is no rubble in our way or it is best to go around, then move
                if (searching) { // If we are searching for a path around rubble, it is best to wait. Maybe dig?
                    pathStart = getPath(null, null);
                    if (rc.isCoreReady() && rc.senseRubble(currentLoc) >= GameConstants.RUBBLE_SLOW_THRESH) {
                        // Rubble is on our current location; clear it for future bots.
                        try {
                            rc.clearRubble(Direction.NONE);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    return false;
                } else if (rc.isCoreReady()) {
                    // We can move this round; attempt to move.
                    if ((reachedGoal && tryPath(currentLoc)) || tryBug(currentLoc, goal)) {
                        // We moved
                        currentLoc = rc.getLocation();
                        int[] loc = map.mapToArray(currentLoc);
                        myLoc = new JumpPoint(loc[0], loc[1]);
                        moved = true;
                    } else {
                        // We did not move; verify location and initiate search.
                        currentLoc = rc.getLocation();
                        reset();

                        int[] loc = map.mapToArray(currentLoc);
                        myLoc = new JumpPoint(loc[0], loc[1]);

                        pathStart = getPath(loc, map.mapToArray(goal));
                        lastPt = pathStart;
                        moved = false;
                    }
                } else {
                    // Cannot move this round
                    moved = false;
                }
            } else if (rc.isCoreReady()) {
                if (searching) {
                    pathStart = getPath(null, null);
                }
                // We are digging.
                try {
                    moved = dig(currentLoc, goal);
                    if (!moved) {
                        reset();
                        currentGoal = goal;
                    }
                } catch (Exception e) {
                    moved = false;
                }
            } else {
                // Can't dig this round
                if (searching) {
                    pathStart = getPath(null, null);
                }
                moved = false;
            }
        } else {
            // No goal, no need to go anywhere
            moved = false;
        }

        return moved;
    }

    private static boolean digging() throws GameActionException {

        if (pathLength == 0 && reachedGoal) {
            // If pathLength = 0, we have determined that a complete path is the best way to go.
            return false;
        }

        double thresh = GameConstants.RUBBLE_OBSTRUCTION_THRESH;

        MapLocation currentLoc = Unit.currentLocation;

        Direction toGoal = currentLoc.directionTo(currentGoal);
        Direction goalRight = toGoal.rotateRight();
        Direction goalLeft = toGoal.rotateLeft();

        MapLocation forward = currentLoc.add(toGoal);
        MapLocation right = currentLoc.add(goalRight);
        MapLocation left = currentLoc.add(goalLeft);

        double rubbleFront = rc.senseRubble(forward);
        double rubbleRight = rc.senseRubble(right);
        double rubbleLeft = rc.senseRubble(left);

        boolean blockedFront = rubbleFront >= thresh;
        boolean blockedRight = rubbleRight >= thresh;
        boolean blockedLeft = rubbleLeft >= thresh;

        if (!(blockedFront || blockedRight || blockedLeft)) {
            // If rubble is not blocking us in the 3 forward directions.
            if (!(rc.canMove(toGoal) || rc.canMove(goalRight) || rc.canMove(goalLeft))) {
                // ... but if we can't move in the 3 forward directions.
                thresh = GameConstants.RUBBLE_SLOW_THRESH;
                if (rubbleFront >= thresh || rubbleRight >= thresh || rubbleLeft >= thresh) {
                    // Let's clear some rubble under our allies.
                    return true;
                }
            }
            // No rubble is blocking the way, no need to dig.
            return false;
        } else if (blockedFront && blockedRight && blockedLeft) {
            // Rubble is blocking the way in all 3 forward directions, let's see if it's faster to go around or dig.

            // Get the cheapest three rubble piles in front of me.
            int turnsToDig, rubblePiles = 0;
            if (rubbleRight > rubbleFront && rubbleLeft > rubbleFront) { // Start with the front.
                double nextRubble = rc.senseRubble(forward.add(forward.directionTo(currentGoal)));
                if (nextRubble >= thresh) { // check for more rubble in the direction of our goal.
                    rubbleFront += nextRubble;
                    rubblePiles++;
                    nextRubble = rc.senseRubble(forward.add(forward.directionTo(currentGoal),2));
                    if (nextRubble >= thresh) { // check for a 3rd rubble pile in the direction of our goal.
                        rubbleFront += nextRubble;
                        rubblePiles++;
                    }
                }
            } else if (rubbleLeft > rubbleRight) { // Front wasn't the smallest pile, check the right.
                rubbleFront = rubbleRight;
                double nextRubble = rc.senseRubble(right.add(right.directionTo(currentGoal)));
                if (nextRubble >= thresh) { // check for a 3rd rubble pile in the direction of our goal.
                    rubbleFront += nextRubble;
                    rubblePiles++;
                    nextRubble = rc.senseRubble(right.add(right.directionTo(currentGoal),2));
                    if (nextRubble >= thresh) { // check for a 3rd rubble pile in the direction of our goal.
                        rubbleFront += nextRubble;
                        rubblePiles++;
                    }
                }
            } else { // Front and right are not the smallest, so it has to be the left.
                rubbleFront = rubbleLeft;
                double nextRubble = rc.senseRubble(left.add(left.directionTo(currentGoal)));
                if (nextRubble >= thresh) { // check for a 3rd rubble pile in the direction of our goal.
                    rubbleFront += nextRubble;
                    rubblePiles++;
                    nextRubble = rc.senseRubble(left.add(left.directionTo(currentGoal),2));
                    if (nextRubble >= thresh) { // check for a 3rd rubble pile in the direction of our goal.
                        rubbleFront += nextRubble;
                        rubblePiles++;
                    }
                }
            }

            // Calculate the number of rounds it will take to dig through the rubble.
            turnsToDig = RubbleUtilities.calculateClearActionsToPassableButSlow(rubbleFront) - (rubblePiles*RubbleUtilities.OBSTR_TO_ZERO);

            if (turnsToDig > pathLength && reachedGoal) {
                // We have a path to the goal and it's faster to take it; set pathLength to 0 so we don't check again.
                pathLength = 0;
                return false;
            } else if (turnsToDig < pathLength) {
                // The incomplete path would already take longer than digging and it cannot improve with more search.
                searching = false;
                return true;
            } else {
                // Otherwise, set pathLength to max value so we check again next time.
                pathLength = Integer.MAX_VALUE;
                return true;
            }
        } else if (!blockedFront && !rc.canMove(toGoal)) {
            // If no rubble is blocking our front and we cannot move, there is a unit blocking the way.
            if (rc.canMove(goalRight) || rc.canMove(goalLeft)) {
                // If we can move right or left of the unit, then we don't need to dig.
                return false;
            } else {
                // Otherwise, we should probably dig while we wait for the unit blocking our way.
                return true;
            }
        } else if (!blockedRight && !rc.canMove(goalRight)) {
            // If no rubble is blocking our right and we cannot move, there is a unit blocking the way.
            if (rc.canMove(toGoal) || rc.canMove(goalLeft)) {
                // If we can move left of the unit, then we don't need to dig.
                return false;
            } else {
                // Otherwise, we should probably dig while we wait for the unit blocking our way.
                return true;
            }
        } else if (!blockedLeft && !rc.canMove(goalLeft)) {
            // If no rubble is blocking our left and we cannot move, there is a unit blocking the way.
            if (rc.canMove(toGoal) || rc.canMove(goalRight)) {
                // If we can move right of the unit, then we don't need to dig.
                return false;
            } else {
                // Otherwise, we should probably dig while we wait for the unit blocking our way.
                return true;
            }
        } else {
            // I think this statement is unreachable, but try to move just in case.
            pathLength = Integer.MAX_VALUE;
            return false;
        }
    }

    /**
     * Handles traffic along a path returned by a successful search.
     * @param currentLoc this unit's current location
     * @param forward desired direction of movement
     * @param direction integer representation of forward
     * @param nextPt next JumpPoint along the path
     * @return True if unit moved, false otherwise
     * @throws GameActionException
     */
    public static boolean handleTrafficOnPath(MapLocation currentLoc, Direction forward, int direction, JumpPoint nextPt) throws GameActionException {

        boolean moved = false;

        MapLocation forwardLoc = currentLoc.add(forward);
        int[] forwardPt = map.mapToArray(forwardLoc);
        Direction left = forward.rotateLeft();
        Direction right = forward.rotateRight();

        // bug toward JumpPoint
        if (rc.canMove(right)) {
            rc.move(right);
            myLoc.move((direction+1)%8);
            moved = true;
        } else if (rc.canMove(left)) {
            rc.move(left);
            myLoc.move((direction-1)%8);
            moved = true;
        } else if (rc.canMove(right.rotateRight())) {
            rc.move(right.rotateRight());
            myLoc.move((direction+2)%8);
            moved = true;
        } else if (rc.canMove(left.rotateLeft())) {
            rc.move(left.rotateLeft());
            myLoc.move((direction-2) % 8);
            moved = true;
        }
        if (moved && nextPt.equals(forwardPt) && rc.getLocation().isAdjacentTo(forwardLoc)) {
            lastPt = nextPt;
        }

        return moved;
    }

    public static int directionToInt(Direction direction) {
        int d;
        switch (direction) {
            case NORTH:
                d = 0;
                break;
            case NORTH_EAST:
                d = 1;
                break;
            case EAST:
                d = 2;
                break;
            case SOUTH_EAST:
                d = 3;
                break;
            case SOUTH:
                d = 4;
                break;
            case SOUTH_WEST:
                d = 5;
                break;
            case WEST:
                d = 6;
                break;
            case NORTH_WEST:
                d = 7;
                break;
            default:
                d = -1;
                break;
        }
        return d;
    }

    public static void test(RobotController rc, int[] start) {
//        int[] start = map.mapToArray(rc.getLocation());
//        int[] end  = new int[]{138,130};     // Search to these coordinates
//
//        Navigation.initialize(rc, new Map(rc));
//
//        // Populate two bit arrays based on a String representation of the map in the TestMaps class.
//        map.mapX = TestMaps.getXMap(test);      // Bit array to represent rows.
//        map.mapY = TestMaps.getYMap(map.mapX);  // Bit array to represent columns.
//
//        move(start,end);
//
//        JumpPoint pathStart = getPath(start, end);
//        JumpPoint lastPath = new JumpPoint(multimap.dfs);
//        JumpPoint myLoc = new JumpPoint(start[0], start[1], -5);
//
//        JumpPoint lastPt = lastPath;
//        int round = 0;
////        System.out.println(Clock.getBytecodesLeft());
//        rc.yield();
//        do {
//            if (round % 3 == 0) {
//                // move
//                JumpPoint commonPoint = lastCommonPoint(lastPath, multimap.dfs);
//                System.out.println(" Common Point: (" + commonPoint.xLoc + "," + commonPoint.yLoc + ")");
//                System.out.print(" Current Path: ");
//                printPath(multimap.dfs);
//                JumpPoint nextPt = lastPt.mapNext;
//                int direction;
//                System.out.print("Branch taken: ");
//                if (backtrack(commonPoint, lastPt)) {
//                    if (myLoc.equals(lastPt)) {
//                        if (commonPoint.equals(lastPt)) {
//                            lastPt = commonPoint;
//                            nextPt = lastPt.mapNext;
//                            direction = myLoc.directionTo(nextPt);
//                            System.out.println("1) backtrack, common point = last point = myLoc");
//                        } else {
//                            lastPt = lastPt.mapLast;
//                            direction = myLoc.directionTo(lastPt);
//                            System.out.println("2) backtrack, common point != last point = myLoc");
//                        }
//                    } else {
//                        direction = myLoc.directionTo(lastPt);
//                        System.out.println("4) backtrack, last point != myLoc");
//                    }
//                } else if (nextPt != null && myLoc.equals(nextPt)) {
//                    lastPt = nextPt;
//                    nextPt = nextPt.mapNext;
//                    direction = myLoc.directionTo(nextPt);
//                    System.out.println("5) myLoc = next point");
//                } else {
//                    direction = myLoc.directionTo(nextPt);
//                    System.out.println("6) myLoc != next point");
//                }
//                System.out.print("Previous Path: ");
//                printPath(lastPath);
//                if (!myLoc.move(direction)) {
//                    round--;
//                    System.out.println("Move failed");
//                    System.out.println();
//                } else {
//
//                    System.out.println("  Current Loc: (" + myLoc.xLoc + "," + myLoc.yLoc + ")");
//                    System.out.println();
//                }
//                lastPath = copyPath(multimap.dfs);
//            }
//
//            pathStart = getPath(null, null);
//
////            System.out.println(Clock.getBytecodesLeft());
//            round++;
//            rc.yield();
//        } while (searching);
//
//        // Print out the map with the jump points of the path marked by their directions of search.
//        for (int i = 119; i < 137; i++) {
//            for (int j = 119; j < 137; j++) {
//                if ((((map.mapX[i][(j) / 64] >>> (63 - (j % 64))) & 1L) == 1)
//                        && (((map.mapY[j][(i) / 64] >>> (63 - (i % 64))) & 1L) == 1)) {
//                    System.out.print("X ");
//                } else {
//                    boolean notVisited = true;
//                    if (pathStart != null) {
//                        JumpPoint path = new JumpPoint(pathStart);
//
//                        while (path != null) {
//                            if (path.xLoc == j && path.yLoc == i) {
//                                if (path.direction == 0) {
//                                    System.out.print("F ");
//                                } else if (path.mapLast == null) {
//                                    System.out.print("S ");
//                                } else {
//                                    System.out.print(path.direction + " ");
//                                }
//                                notVisited = false;
//                                break;
//                            } else {
//                                path = path.mapNext;
//                            }
//                        }
//                    }
//                    if (notVisited) {
//                        System.out.print("- ");
//                    }
//                }
//            }
//            System.out.println();
//        }

    }

    public static JumpPoint copyPath(JumpPoint jp) {
        JumpPoint root = new JumpPoint(jp.xLoc, jp.yLoc);
        JumpPoint build = root;
        JumpPoint next = jp.mapNext;
        while (next != null) {
            build.mapNext = new JumpPoint(next.xLoc, next.yLoc);
            build.mapNext.mapLast = build;
            build = build.mapNext;
            next = next.mapNext;
        }
        return root;
    }

    public static boolean backtrack(JumpPoint commonPoint, JumpPoint lastPoint) {
        JumpPoint checkNext = lastPoint.mapNext;
        boolean goBack;
        try {
            while (!checkNext.equals(commonPoint)) {
                checkNext = checkNext.mapNext;
            }
            goBack = false;
        } catch (NullPointerException e) {
            goBack = true;
        }
        return goBack;
    }

    /**
     * Merge the result of a previous search into the current search.
     *
     * @param lastPath
     * @param currentPath
     * @param nextPoint
     * @return JumpPoint representing the next point to move towards, or null if
     * a next JumpPoint does not exist.
     */
    public static JumpPoint getCommonPoint(JumpPoint lastPath, JumpPoint currentPath, JumpPoint nextPoint) {
        JumpPoint output;
        if (currentPath == null) {
            output = null;
        } else {

            JumpPoint commonPoint = currentPath;

//            onCourse = false;
            /* Iterate through JumpPoints until we reach the end of either path or
             we reach a point further than this unit could have moved to since
             the start of the search. */
            JumpPoint checkLast = lastPath;
            JumpPoint checkCurrent = currentPath;
            int nodes = 1;
            try {
                while (checkLast.equals(checkCurrent)) {
//                    if (nextPoint.equals(commonPoint)) {
//                        onCourse = true;
//                    }
                    checkLast = checkLast.mapNext;
                    commonPoint = checkCurrent;
                    checkCurrent = checkCurrent.mapNext;
                    nodes += 1;
                }
            } catch (NullPointerException e) {
            }

            output = new JumpPoint(commonPoint);
            output.f = nodes;
        }
        return output;
    }

    /**
     * Merge two paths, concatenating the new path from the common point with
     * the reverse of the old path traveled from the common point.
     *
     * @param commonPoint JumpPoint
     * @param lastPath
     * @return
     */
    public static JumpPoint mergePaths(JumpPoint commonPoint, JumpPoint lastPath) {
        JumpPoint checkLast = lastPath;
        JumpPoint retrace = new JumpPoint(checkLast.xLoc, checkLast.yLoc);
        checkLast = checkLast.mapLast;
        while (checkLast != null && !commonPoint.equals(checkLast)) {
            retrace.mapNext = new JumpPoint(checkLast.xLoc, checkLast.yLoc);
            retrace.mapNext.mapLast = retrace;
            retrace = retrace.mapNext;
            checkLast = checkLast.mapLast;
        }

        if (checkLast == null) {
            retrace = lastPath;
        } else {
            retrace.mapNext = new JumpPoint(commonPoint);
            retrace.mapNext.mapLast = retrace;
        }

        return retrace;
    }

    /**
     * Search for a path from point a to point b.
     *
     * @param a start of the path.
     * @param b end of the path.
     * @return the JumpPoint object at the start of this path, or null if there
     * is no path.
     */
    public static JumpPoint getPath(int[] a, int[] b) {
        searching = true;
        long[][] mapX = map.mapX;
        long[][] mapY = map.mapY;

        int maxBytecodes = bytecodeLimit;

        if (rc.isCoreReady()) {
            maxBytecodes += 1500;
        }

        // If a is not null, start a new search. Otherwise resume previous search.
        if (a != null) {
            multimap = new Multimap(a[0], a[1], b[0], b[1]);
            reachedGoal = false;
        }

        // Goal coordinates
        int goalX = multimap.goalX;
        int goalY = multimap.goalY;

        // Previous distances
        int lastStepX, lastStepY;

        try {
            while (multimap.size != 0) {
                // Remove a JumpPoint from the head of the multimap.
                JumpPoint next = multimap.remove();
                // Current position
                int xLoc = next.xLoc;
                int yLoc = next.yLoc;
                // Distances from location to next void
                int stepX, stepY;

                switch (next.direction) {    // Determine the direction of movement

                    case 1: // Direction = NE

                        // Initialize the distance from previous node to first void
                        if (next.xDistance > 0) {
                            lastStepX = next.xDistance;
                        } else {
                            lastStepX = distanceRight(xLoc, yLoc, mapX);
                        }

                        if (next.yDistance > 0) {
                            lastStepY = next.yDistance;
                        } else {
                            lastStepY = distanceLeft(yLoc, xLoc, mapY);
                        }

                        // If previous location was traversable, check this location
                        if (lastStepX != 0) {

                            // Check if the goal is directly reachable
                            if (goalX == xLoc) {
                                if (goalY <= yLoc && goalY >= yLoc - lastStepY) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalY >= yLoc && goalY <= yLoc + distanceRight(yLoc, xLoc, mapY)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            } else if (goalY == yLoc) {
                                if (goalX <= xLoc && goalX >= xLoc - distanceLeft(xLoc, yLoc, mapX)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalX >= xLoc && goalX <= xLoc + lastStepX) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            }

                            // step = distances to voids from the next location
                            stepX = distanceRight(++xLoc, --yLoc, mapX);
                            stepY = distanceLeft(yLoc, xLoc, mapY);

                            // Difference between last and current distances
                            int dX = stepX - lastStepX; // should be -1

                            if (dX >= 0) { // If x difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(xLoc + lastStepX - 1, yLoc + 1, mapX);

                                // if the new difference is -1 or greater, add a new Node to the queue
                                if (dX - voids >= -1) {
                                    int distance = voids + lastStepX - 2;
                                    // New direction is SE
                                    JumpPoint jp = new JumpPoint(
                                            xLoc + distance,
                                            yLoc,
                                            3,
                                            next,
                                            distance);
                                    jp.xDistance = stepX - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dX < -1) { // If x difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(xLoc + stepX, yLoc, mapX);

                                // If the new difference is less than 0, add a new Node to the queue
                                if (dX + voids < 0) {
                                    // New direction is NE
                                    int distance = voids + stepX - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc + distance,
                                            yLoc + 1,
                                            1,
                                            next,
                                            distance);
                                    jp.xDistance = lastStepX - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Difference between last and current distances
                            int dY = stepY - lastStepY; // should be -1

                            if (dY >= 0) { // If y difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(yLoc - lastStepY + 1, xLoc - 1, mapY);

                                // If the new difference is -1 or greater, add a new Node to the queue
                                if (dY - voids >= -1) {
                                    // New direction is NW
                                    int distance = voids + lastStepY - 2;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc,
                                            yLoc - distance,
                                            7,
                                            next,
                                            distance);
                                    jp.yDistance = stepY - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dY < -1) { // If y difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(yLoc - stepY, xLoc, mapY);

                                // If the new difference is less than 0, add a new Node to the queue
                                if (dY + voids < 0) {
                                    // New direction is NE
                                    int distance = voids + stepY - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc - 1,
                                            yLoc - distance,
                                            1,
                                            next,
                                            distance);
                                    jp.yDistance = lastStepY - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Save distances, probably will be used next iteration
                            lastStepX = stepX;
                            lastStepY = stepY;
                        }
                        break;

                    case 3: // Direction = SE

                        // Initialize the distance from previous node to first void
                        if (next.xDistance > 0) {
                            lastStepX = next.xDistance;
                        } else {
                            lastStepX = distanceRight(xLoc, yLoc, mapX);
                        }

                        if (next.yDistance > 0) {
                            lastStepY = next.yDistance;
                        } else {
                            lastStepY = distanceRight(yLoc, xLoc, mapY);
                        }

                        // If previous location was traversable, check this location
                        if (lastStepX != 0) {

                            // Check if the goal is directly reachable
                            if (goalX == xLoc) {
                                if (goalY <= yLoc && goalY >= yLoc - distanceLeft(yLoc, xLoc, mapY)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalY >= yLoc && goalY <= yLoc + lastStepY) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            } else if (goalY == yLoc) {
                                if (goalX <= xLoc && goalX >= xLoc - distanceLeft(xLoc, yLoc, mapX)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalX >= xLoc && goalX <= xLoc + lastStepX) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            }

                            // step = distances to x and y voids & properly increment/decrement new int[]{xLoc,yLoc}
                            stepX = distanceRight(++xLoc, ++yLoc, mapX);
                            stepY = distanceRight(yLoc, xLoc, mapY);

                            // Difference between last and current distances
                            int dX = stepX - lastStepX; // should be -1

                            if (dX >= 0) { // If x difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(xLoc + lastStepX - 1, yLoc - 1, mapX);

                                // If the new difference is -1 or greater, add a new Node to the queue
                                if (dX - voids >= -1) {
                                    // New direction is NE
                                    int distance = voids + lastStepX - 2;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc + distance,
                                            yLoc,
                                            1,
                                            next,
                                            distance);
                                    jp.xDistance = stepX - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dX < -1) { // If x difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(xLoc + stepX, yLoc, mapX);

                                // If the new difference is less than 0, add a new Node to the queue
                                if (dX + voids < 0) {
                                    // New direction is SE
                                    int distance = voids + stepX - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc + distance,
                                            yLoc - 1,
                                            3,
                                            next,
                                            distance);
                                    jp.xDistance = lastStepX - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Difference between last and current distances
                            int dY = stepY - lastStepY; // should be -1

                            if (dY >= 0) { // If y difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(yLoc + lastStepY - 1, xLoc - 1, mapY);

                                // If the new difference is -1 or greater, add a new Node to the queue
                                if (dY - voids >= -1) {
                                    // New direction is SW
                                    int distance = voids + lastStepY - 2;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc,
                                            yLoc + distance,
                                            5,
                                            next,
                                            distance);
                                    jp.yDistance = stepY - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dY < -1) { // If y difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(yLoc + stepY, xLoc, mapY);

                                // If the new difference is less than 0, add a new Node to the queue
                                if (dY + voids < 0) {
                                    // New direction is SE
                                    int distance = voids + stepY - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc - 1,
                                            yLoc + distance,
                                            3,
                                            next,
                                            distance);
                                    jp.yDistance = lastStepY - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Save distances, probably will be used next iteration
                            lastStepX = stepX;
                            lastStepY = stepY;
                        }
                        break;

                    case 5: // Direction = SW

                        // Initialize the distance from previous node to first void
                        if (next.xDistance > 0) {
                            lastStepX = next.xDistance;
                        } else {
                            lastStepX = distanceLeft(xLoc, yLoc, mapX);
                        }

                        if (next.yDistance > 0) {
                            lastStepY = next.yDistance;
                        } else {
                            lastStepY = distanceRight(yLoc, xLoc, mapY);
                        }

                        // If previous location was traversable, check this location
                        if (lastStepX != 0) {

                            // Check if the goal is directly reachable
                            if (goalX == xLoc) {
                                if (goalY <= yLoc && goalY >= yLoc - distanceLeft(yLoc, xLoc, mapY)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalY >= yLoc && goalY <= yLoc + lastStepY) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            } else if (goalY == yLoc) {
                                if (goalX <= xLoc && goalX >= xLoc - lastStepX) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalX >= xLoc && goalX <= xLoc + distanceRight(xLoc, yLoc, mapX)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            }

                            // step = distances to x and y voids & properly increment/decrement new int[]{xLoc,yLoc}
                            stepX = distanceLeft(--xLoc, ++yLoc, mapX);
                            stepY = distanceRight(yLoc, xLoc, mapY);

                            // Difference between last and current distances
                            int dX = stepX - lastStepX; // should be -1

                            if (dX >= 0) { // if x difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(xLoc - lastStepX + 1, yLoc - 1, mapX);

                                // If the new difference is -1 or greater, add a new Node to the queue
                                if (dX - voids >= -1) {
                                    // New direction is NW
                                    int distance = voids + lastStepX - 2;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc - distance,
                                            yLoc,
                                            7,
                                            next,
                                            distance);
                                    jp.xDistance = stepX - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dX < -1) { // If x difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(xLoc - stepX, yLoc, mapX);

                                // if the new difference is less than 0, add a new Node to the queue
                                if (dX + voids < 0) {
                                    // New direction is SW
                                    int distance = voids + stepX - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc - distance,
                                            yLoc - 1,
                                            5,
                                            next,
                                            distance);
                                    jp.xDistance = lastStepX - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Difference between last and current distances
                            int dY = stepY - lastStepY; // should be -1

                            if (dY >= 0) { // If y difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(yLoc + lastStepY - 1, xLoc + 1, mapY);

                                // If the new difference is -1 or greater, add a new Node to the queue
                                if (dY - voids >= -1) {
                                    // New direction is SE
                                    int distance = voids + lastStepY - 2;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc,
                                            yLoc + distance,
                                            3,
                                            next,
                                            distance);
                                    jp.yDistance = stepY - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dY < -1) { // If y difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidRight(yLoc + stepY, xLoc, mapY);

                                // If the new difference is less than 0, add a new Node to the queue
                                if (dY + voids < 0) {
                                    // New direction is SW
                                    int distance = voids + stepY - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc + 1,
                                            yLoc + distance,
                                            5,
                                            next,
                                            distance);
                                    jp.yDistance = lastStepY - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Save distances, probably will be used next iteration
                            lastStepX = stepX;
                            lastStepY = stepY;
                        }
                        break;

                    default: // Direction = NW

                        // Initialize the distance from previous node to first void
                        if (next.xDistance > 0) {
                            lastStepX = next.xDistance;
                        } else {
                            lastStepX = distanceLeft(xLoc, yLoc, mapX);
                        }

                        if (next.yDistance > 0) {
                            lastStepY = next.yDistance;
                        } else {
                            lastStepY = distanceLeft(yLoc, xLoc, mapY);
                        }

                        // If previous location was traversable, check this location
                        if (lastStepX != 0) {

                            // Check if the goal is directly reachable
                            if (goalX == xLoc) {
                                if (goalY <= yLoc && goalY >= yLoc - lastStepY) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalY >= yLoc && goalY <= yLoc + distanceRight(yLoc, xLoc, mapY)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            } else if (goalY == yLoc) {
                                if (goalX <= xLoc && goalX >= xLoc - lastStepX) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                } else if (goalX >= xLoc && goalX <= xLoc + distanceRight(xLoc, yLoc, mapX)) {
                                    next.mapNext = new JumpPoint(goalX, goalY, 0, next, 0);
                                    next.mapNext.mapLast = next;
                                    searching = false;
                                    reachedGoal = true;
                                    goalJP = next.mapNext;
                                    return multimap.retrace(next);
                                }
                            }

                            // step = distances to x and y voids & properly increment/decrement new int[]{xLoc,yLoc}
                            stepX = distanceLeft(--xLoc, --yLoc, mapX);
                            stepY = distanceLeft(yLoc, xLoc, mapY);

                            // Difference between last and current distances
                            int dX = stepX - lastStepX; // should be -1

                            if (dX >= 0) { // If x difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(xLoc - lastStepX + 1, yLoc + 1, mapX);

                                // If the new difference is -1 or greater, add a new Node to the queue
                                if (dX - voids >= -1) {
                                    // New direction is SW
                                    int distance = voids + lastStepX - 2;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc - distance,
                                            yLoc,
                                            5,
                                            next,
                                            distance);
                                    jp.xDistance = stepX - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dX < -1) { // if x difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(xLoc - stepX, yLoc, mapX);

                                // If the new difference is less than 0, add a new Node to the queue
                                if (dX + voids < 0) {
                                    // New direction is NW
                                    int distance = voids + stepX - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc - distance,
                                            yLoc + 1,
                                            7,
                                            next,
                                            distance);
                                    jp.xDistance = lastStepX - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Difference between last and current distances
                            int dY = stepY - lastStepY; // should be -1

                            if (dY >= 0) { // If y difference is greater than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(yLoc - lastStepY + 1, xLoc + 1, mapY);

                                // If the new difference is -1 or greater, add a new Node to the queue
                                if (dY - voids >= -1) {
                                    // New direction is NE
                                    int distance = voids + lastStepY - 2;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc,
                                            yLoc - distance,
                                            1,
                                            next,
                                            distance);
                                    jp.yDistance = stepY - distance;
                                    multimap.insert(jp);
                                }

                            } else if (dY < -1) { // If y difference is less than -1

                                // Check the number of consecutive voids
                                int voids = distanceVoidLeft(yLoc - stepY, xLoc, mapY);

                                // If the new difference is less than 0, add a new Node to the queue
                                if (dY + voids < 0) {
                                    // New direction is NW
                                    int distance = voids + stepY - 1;
                                    JumpPoint jp = new JumpPoint(
                                            xLoc + 1,
                                            yLoc - distance,
                                            7,
                                            next,
                                            distance);
                                    jp.yDistance = lastStepY - distance - 1;
                                    multimap.insert(jp);
                                }
                            }

                            // Save distances, probably will be used next iteration
                            lastStepX = stepX;
                            lastStepY = stepY;
                        }
                        break;
                }

                // If current point is traversable, add it to the queue for search
                if (lastStepX != 0) {
                    JumpPoint jp = new JumpPoint(xLoc, yLoc, next.direction, next, 0);
                    jp.xDistance = lastStepX;
                    jp.yDistance = lastStepY;
                    multimap.insert(jp);
                }

                // If the bytecode limit has been reached, return current best path
                if (Clock.getBytecodesLeft() < maxBytecodes) {
                    JumpPoint out = multimap.peek(multimap.peek());
                    if (out == null) {
                        pathLength = Integer.MAX_VALUE;
                        return out;
                    }
                    if (pathLength != 0) {
                        pathLength = out.f;
                    }
                    out = multimap.retrace(out);
                    return out;
                }
            }

            // ArrayIndexOutOfBoundsException means we have searched outside of possible map
            // Resume search if multimap still holds nodes.
        } catch (ArrayIndexOutOfBoundsException e1) {
            if (Clock.getBytecodesLeft() < maxBytecodes && multimap.size > 0) {
                JumpPoint out = multimap.peek(multimap.peek());
                if (out == null) {
                    pathLength = Integer.MAX_VALUE;
                    return out;
                }
                if (pathLength != 0) {
                    pathLength = out.f;
                }
                out = multimap.retrace(out);
                return out;
            }

            return getPath(null, null);
        }

        pathLength = Integer.MAX_VALUE;
        searching = false;
        return null;
    }

    /**
     * Find the number of trailing zeroes in the long at the current position.
     *
     * @param x current x-coordinate.
     * @param y current y-coordinate & y-index of the first long in the 2D
     * array.
     * @param map 2D array of longs representing the map based on set bits.
     * @return number of bits between current position and the next set bit.
     */
    public static int distanceLeft(int x, int y, long[][] map) {

        // Get indices for our current position - 1.
        int shift = 63 - (x % 64);  // Amount to shift the first long's bits.
        x /= 64;                    // X-index of the first long in the array.

        // Least significant bit of i is our current position in the row - 1.
        long i = map[y][x] >>> (shift);

        if (i != 0) {
            return Long.numberOfTrailingZeros(i);
        }

        if (x == 0) {
            return 64 - shift;
        }
        i = map[y][x - 1];

        if (i != 0) {
            return 64 - shift + Long.numberOfTrailingZeros(i);
        }

        if (x == 1) {
            return 128 - shift;
        }

        i = map[y][x - 2];

        return 128 - shift + Long.numberOfTrailingZeros(i);
    }

    /**
     * Find the number of leading zeroes in the long at the current position.
     *
     * @param x current x-coordinate.
     * @param y current y-coordinate & y-index of the first long in the 2D
     * array.
     * @param map 2D array of longs representing the map based on set bits.
     * @return number of bits between current position and the next set bit.
     */
    public static int distanceRight(int x, int y, long[][] map) {
        int shift = x % 64;
        x /= 64;
        long i = map[y][x] << shift;

        if (i != 0) {
            return Long.numberOfLeadingZeros(i);
        }

        if (x == 3) {
            return 64 - shift;
        }
        i = map[y][x + 1];

        if (i != 0) {
            return 64 - shift + Long.numberOfLeadingZeros(i);
        }

        if (x == 2) {
            return 128 - shift;
        }

        i = map[y][x + 2];

        return 128 - shift + Long.numberOfLeadingZeros(i);
    }

    /**
     * Find the number of trailing ones in the long at the current position.
     *
     * @param x current x-coordinate.
     * @param y current y-coordinate & y-index of the first long in the 2D
     * array.
     * @param map 2D array of longs representing the map based on set bits.
     * @return number of bits between current position and the next set bit.
     */
    public static int distanceVoidLeft(int x, int y, long[][] map) {

        // Get indices for our current position - 1.
        int shift = 63 - (x % 64);  // Amount to shift the first long's bits.
        x /= 64;                    // X-index of the first long in the array.

        // Least significant bit of i is our current position in the row - 1.
        long i = ~map[y][x] >>> (shift);

        if (i != 0) {
            return Long.numberOfTrailingZeros(i);
        }

        if (x == 0) {
            return 64 - shift;
        }

        i = ~map[y][x - 1];

        if (i != 0) {
            return 64 - shift + Long.numberOfTrailingZeros(i);
        }

        if (x == 1) {
            return 128 - shift;
        }

        i = ~map[y][x - 2];

        return 128 - shift + Long.numberOfTrailingZeros(i);
    }

    /**
     * Find the number of leading ones in the long at the current position.
     *
     * @param x current x-coordinate.
     * @param y current y-coordinate & y-index of the first long in the 2D
     * array.
     * @param map 2D array of longs representing the map based on set bits.
     * @return number of bits between current position and the next set bit.
     */
    public static int distanceVoidRight(int x, int y, long[][] map) {
        int shift = x % 64;
        x /= 64;
        long i = ~map[y][x] << shift;

        if (i != 0) {
            return Long.numberOfLeadingZeros(i);
        }

        if (x == 3) {
            return 64 - shift;
        }
        i = ~map[y][x + 1];

        if (i != 0) {
            return 64 - shift + Long.numberOfLeadingZeros(i);
        }

        if (x == 2) {
            return 128 - shift;
        }

        i = ~map[y][x + 2];

        return 128 - shift + Long.numberOfLeadingZeros(i);
    }

    public static void printPath(JumpPoint path) {
        System.out.print("(" + path.xLoc + "," + path.yLoc + "),");
        while (path.mapNext != null) {
            path = path.mapNext;
            System.out.print("(" + path.xLoc + "," + path.yLoc + "),");
        }
        System.out.println();
    }
}

