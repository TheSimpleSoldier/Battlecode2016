package team037.Utilites;

import team037.DataStructures.BuildOrder;
import team037.Enums.Bots;

public class BuildOrderCreation
{
    public static BuildOrder createBuildOrder()
    {
        Bots[][] buildOrder = {
                {Bots.SCOUTINGSCOUT, Bots.BASESOLDIER},
                {Bots.BASESOLDIER, Bots.BASESOLDIER},
                {Bots.BASESCOUT, Bots.BASESOLDIER},
                {Bots.BASESOLDIER, null}
        };
        int[] times = {2, 6, 1, 1000};

        Bots[][] buildOrderSoldiers = {
                {Bots.BASESOLDIER}
        };
        int[] times2 = {10000};

        return new BuildOrder(buildOrder, times);
    }
}
