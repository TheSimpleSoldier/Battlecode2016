package team037.Utilites;

import team037.DataStructures.BuildOrder;
import team037.Enums.Bots;

public class BuildOrderCreation
{
    public static BuildOrder createBuildOrder()
    {
        Bots[][] buildOrder = {
                {Bots.SCOUTINGSCOUT, Bots.CASTLESOLDIER},
                {Bots.RUSHINGVIPER, Bots.CASTLESOLDIER},
                {Bots.SCOUTINGSCOUT, Bots.DENKILLERSOLDIER},
                {Bots.DENKILLERSOLDIER, Bots.CASTLESOLDIER},
                {Bots.RUSHINGSOLDIER, Bots.RUSHINGVIPER},
                {Bots.DENKILLERSOLDIER, null}
        };
        int[] times = {1, 1, 1, 3, 2, 1000};

        Bots[][] buildOrderSoldiers = {
                {Bots.BASESOLDIER}
        };
        int[] times2 = {10000};

        return new BuildOrder(buildOrder, times);
    }
}
