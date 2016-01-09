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
                {Bots.SCOUTINGSCOUT, Bots.BASESOLDIER},
                {Bots.PATROLSCOUT, Bots.BASESOLDIER},
                {Bots.BASETURRET, Bots.BASESOLDIER},
                {Bots.BASESOLDIER, null}
        };
        int[] times = {2, 2, 3, 1000};

        Bots[][] buildOrderSoldiers = {
                {Bots.BASESOLDIER}
        };
        int[] times2 = {10000};

        return new BuildOrder(buildOrder, times);
    }
}
