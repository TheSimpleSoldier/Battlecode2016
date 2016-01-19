package team037.Utilites;

import battlecode.common.*;
import static team037.Unit.*;


public class MoveUtils {

    public static boolean tryMove(Direction toMove) throws GameActionException {
        if (rc.canMove(toMove)) {
            rc.move(toMove);
            return true;
        }
        return false;
    }

    public static boolean tryClear(Direction toClear) throws GameActionException {
        if (rc.senseRubble(currentLocation.add(toClear)) > 0) {
            rc.clearRubble(toClear);
            return true;
        }
        return false;
    }

    /**
     * Tries to move anywhere! If tryClearing, will try clearing if it can't move
     * @param toMove
     * @param tryClearing
     * @return
     * @throws GameActionException
     */
    public static boolean tryMoveAnywhere(Direction toMove, boolean tryClearing) throws GameActionException {
        if (move(toMove, 4)) {
            return true;
        } else if (tryClearing && clear(toMove, 4)) {
            return true;
        }
        return false;
    }

    /**
     * Tries to clear anywhere!
     * @return
     * @throws GameActionException
     */
    public static boolean tryClearAnywhere(Direction toClear) throws GameActionException {
        return clear(toClear, 4);
    }

    /**
     * Tries to move anywhere except backwards! If tryClearing, will try clearing if it can't move
     * @param toMove
     * @param tryClearing
     * @return
     * @throws GameActionException
     */
    public static boolean tryMoveAnywhereExceptBackwards(Direction toMove, boolean tryClearing) throws GameActionException {
        if (move(toMove, 3)) {
            return true;
        } else if (tryClearing && clear(toMove, 3)) {
            return true;
        }
        return false;
    }

    /**
     * Tries to clear anywhere except backwards!
     * @param toClear
     * @return
     * @throws GameActionException
     */
    public static boolean tryClearAnywhereExceptBackwards(Direction toClear) throws GameActionException {
        return clear(toClear, 3);
    }

    /**
     * Tries to move toMove, .left, .right, .left.left, .right.right
     * @param toMove
     * @param tryClearing makes it try clearing those directions too
     * @return
     * @throws GameActionException
     */
    public static boolean tryMoveForwardOrSideways(Direction toMove, boolean tryClearing) throws GameActionException {
        if (move(toMove, 2)) {
            return true;
        } else if (tryClearing && clear(toMove, 2)) {
            return true;
        }
        return false;
    }

    /**
     * Tries to clear toMove, .left, .right, .left.left, .right.right
     * @param toClear
     * @return
     * @throws GameActionException
     */
    public static boolean tryClearForwardOrSideways(Direction toClear) throws GameActionException {
        return clear(toClear, 2);
    }

    /**
     * Tries to move toMove, .left, .right
     * @param toMove
     * @param tryClearing makes it try clearing those directions too
     * @return
     * @throws GameActionException
     */
    public static boolean tryMoveForwardOrLeftRight(Direction toMove, boolean tryClearing) throws GameActionException {
        if (move(toMove, 1)) {
            return true;
        } else if (tryClearing && clear(toMove, 1)) {
            return true;
        }
        return false;
    }

    /**
     * Tries to clear toMove, .left, .right
     * @param toClear
     * @return
     * @throws GameActionException
     */
    public static boolean tryClearForwardOrLeftRight(Direction toClear) throws GameActionException {
        return clear(toClear, 2);
    }

    /**
     * Starting in toClear, tries to find a square to clear rubble
     * @param toClear
     * @param numTurns
     * @return
     * @throws GameActionException
     */
    private static boolean clear(Direction toClear, int numTurns) throws GameActionException {
        if (toClear == null) {
            return false;
        }
        if (tryClear(toClear)) {
            return true;
        }

        Direction left = null;
        Direction right = null;
        if (numTurns >= 1) {
            left = toClear.rotateLeft();
            right = toClear.rotateRight();
            if (tryClear(left)) {
                return true;
            } else if (tryClear(right)) {
                return true;
            }
        }
        if (numTurns >= 2) {
            left = left.rotateLeft();
            right = right.rotateRight();
            if (tryClear(left)) {
                return true;
            } else if (tryClear(right)) {
                return true;
            }
        }
        if (numTurns >= 3) {
            left = left.rotateLeft();
            right = right.rotateRight();
            if (tryClear(left)) {
                return true;
            } else if (tryClear(right)) {
                return true;
            }
        }
        if (numTurns >= 4) {
            if (tryClear(toClear.opposite())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Starting in toMove, tries to find a square to move to
     * @param toMove
     * @param numTurns
     * @return
     * @throws GameActionException
     */
    private static boolean move(Direction toMove, int numTurns) throws GameActionException {
        if (toMove == null) {
            return false;
        }
        if (tryMove(toMove)) {
            return true;
        }

        Direction left = null;
        Direction right = null;
        if (numTurns >= 1) {
            left = toMove.rotateLeft();
            right = toMove.rotateRight();
            if (tryMove(left)) {
                return true;
            } else if (tryMove(right)) {
                return true;
            }
        }
        if (numTurns >= 2) {
            left = left.rotateLeft();
            right = right.rotateRight();
            if (tryMove(left)) {
                return true;
            } else if (tryMove(right)) {
                return true;
            }
        }
        if (numTurns >= 3) {
            left = left.rotateLeft();
            right = right.rotateRight();
            if (tryMove(left)) {
                return true;
            } else if (tryMove(right)) {
                return true;
            }
        }
        if (numTurns >= 4) {
            if (tryMove(toMove.opposite())) {
                return true;
            }
        }
        return false;
    }




}