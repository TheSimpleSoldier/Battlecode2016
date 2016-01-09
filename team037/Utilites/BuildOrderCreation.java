package team037.Utilites;

import team037.DataStructures.BuildOrder;
import team037.Enums.Bots;

/**
 * Created by joshua on 1/5/16.
 */
public class BuildOrderCreation
{
    public static BuildOrder createBuildOrder()
    {
        Bots[][] buildOrder = {
                {Bots.SCOUTINGSCOUT, Bots.ATTACKINGSOLDIER},
                {Bots.BASESCOUT, Bots.ATTACKINGSOLDIER},
                {Bots.BASEVIPER, Bots.ATTACKINGSOLDIER},
                {Bots.ATTACKINGSOLDIER, null}
        };
        int[] times = {2, 1, 3, 1000};

        Bots[][] buildOrderSoldiers = {
                {Bots.ATTACKINGSOLDIER}
        };
        int[] times2 = {10000};

        return new BuildOrder(buildOrder, times2);
    }
}
