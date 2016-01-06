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
                {Bots.BASESCOUT, Bots.BASESOLDIER},
                {Bots.BASEVIPER, Bots.BASESOLDIER},
                {Bots.BASESOLDIER}
        };
        int[] times = {2, 3, 1000};

        return new BuildOrder(buildOrder, times);
    }
}
