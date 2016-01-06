package team037.Utilites;

import battlecode.common.*;
import battlecode.common.GameConstants;


public class RubbleUtilities
{
    public static final int OBSTR_TO_ZERO = calculateClearActionsToReduceToZero(GameConstants.RUBBLE_OBSTRUCTION_THRESH);
    public static final int SLOW_TO_ZERO = calculateClearActionsToReduceToZero(GameConstants.RUBBLE_SLOW_THRESH);

    /**
     * This method returns the number of clear actions necessary to remove rubble
     */
    public static int calculateClearActionsToReduceToZero(double rubble)
    {
        // RUBBLE_CLEAR_PERCENTAGE is something like .05, for the formula we need it to be .95
        double I = 1 - GameConstants.RUBBLE_CLEAR_PERCENTAGE;
        double W = GameConstants.RUBBLE_CLEAR_FLAT_AMOUNT;

        return (int)Math.ceil(Math.log10(W / (W - rubble * (I - 1))) / Math.log10(I));
    }

    public static int calculateClearActionsToPassableButSlow(double rubble) {
        if (rubble < GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
            return 0;
        } else {
            return Math.max(calculateClearActionsToReduceToZero(rubble) - OBSTR_TO_ZERO, 1);
        }
    }

    public static int calculateClearActionsToClear(double rubble) {
        if (rubble < GameConstants.RUBBLE_SLOW_THRESH) {
            return 0;
        } else {
            return Math.max(calculateClearActionsToReduceToZero(rubble) - SLOW_TO_ZERO, 1);
        }
    }
}
