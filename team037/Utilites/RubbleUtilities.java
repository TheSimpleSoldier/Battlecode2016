package team037.Utilites;

import battlecode.common.*;
import battlecode.common.GameConstants;


public class RubbleUtilities
{
    /**
     * This method returns the number of clear actions necessary to remove rubble
     */
    public static int calculateClearActionsToClear(double rubble)
    {
        double I = GameConstants.RUBBLE_CLEAR_PERCENTAGE;
        double W = GameConstants.RUBBLE_CLEAR_FLAT_AMOUNT;

        return (int)Math.ceil(Math.log10(W / (W - rubble * (I - 1))) / Math.log10(I));
    }
}
