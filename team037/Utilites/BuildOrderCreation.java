package team037.Utilites;

import team037.DataStructures.BuildOrder;
import team037.Enums.Bots;
import team037.Enums.Strategies;
import team037.RobotPlayer;
import team037.Unit;
import team037.Units.TurtleUnits.TurtleTurret;

public class BuildOrderCreation
{
    private static final int multiplier = Unit.alliedArchonStartLocs.length;

    public static BuildOrder createBuildOrder()
    {
        //////////////////////////////////////////////////////////////////////////////
        //                                                                          //
        // Put any build orders for a new strategy here                             //
        //                                                                          //
        //////////////////////////////////////////////////////////////////////////////

        if (RobotPlayer.strategy.equals(Strategies.SCOUT_BOMB)) {
            Bots[][] buildOrderBombs = {
                    {Bots.SCOUTBOMBSCOUT}
            };
            int[] times2 = {10000};
            return new BuildOrder(buildOrderBombs, times2);
        }

        if (RobotPlayer.strategy.equals(Strategies.RUSH))
        {
            Bots[][] buildOrderRush = {
                    {Bots.RUSHINGSOLDIER, Bots.RUSHINGSOLDIER, Bots.RUSHSCOUT, Bots.RUSHINGVIPER},
                    {Bots.RUSHGUARD, Bots.RUSHINGSOLDIER, Bots.RUSHINGVIPER, Bots.RUSHINGSOLDIER},
            };

            int[] timesRush = {1,1000};

            return new BuildOrder(buildOrderRush, timesRush);
        }

        if (RobotPlayer.strategy.equals(Strategies.CASTLE))
        {
            Bots[][] buildOrderSoldiers = {
                    {Bots.CASTLESOLDIER}
            };
            int[] times2 = {10000};
            return new BuildOrder(buildOrderSoldiers, times2);
        }

        if (RobotPlayer.strategy.equals(Strategies.TURTLE))
        {
            Bots[][] buildOrderTurtle = {
                    {Bots.TURTLEGUARD, Bots.TURTLEGUARD},
                    {Bots.TURTLESCOUT, Bots.TURTLETURRET},
                    {Bots.TURTLETURRET, Bots.TURTLEGUARD},
                    {Bots.SCOUTINGSCOUT, Bots.TURTLEGUARD},
                    {Bots.TURTLETURRET, Bots.TURTLETURRET},
                    {Bots.TURTLETURRET, Bots.TURTLESCOUT},
                    {Bots.TURTLETURRET, Bots.TURTLEGUARD},
                    {Bots.TURTLETURRET, Bots.RUSHINGVIPER},
            };

            int[] timesTurtle = {4/multiplier, 4/multiplier, 4/multiplier, 1, 4/multiplier, 4/multiplier, 8/multiplier, 1000};

            return new BuildOrder(buildOrderTurtle, timesTurtle);
        }

        if (RobotPlayer.strategy.equals(Strategies.TURRET_RUSH)) {
            Bots[][] buildOrderSoldiers = {
                    {Bots.BASETURRET}
            };
            int[] times2 = {10000};
            return new BuildOrder(buildOrderSoldiers, times2);
        }

        if (RobotPlayer.strategy.equals(Strategies.PACMAN)) {
            Bots[][] buildOrderPacMan = {
                    {Bots.RUSHGUARD, Bots.SCOUTBOMBSCOUT},
            };

            int[] timesPacMan = {0};

            return new BuildOrder(buildOrderPacMan,timesPacMan);
        }


        // This is the default strategy
        Bots[][] buildOrder = {
                {Bots.SCOUTINGSCOUT, Bots.DENKILLERSOLDIER},
                {Bots.DENKILLERSOLDIER, Bots.CASTLESOLDIER},
                {Bots.DENKILLERSOLDIER, Bots.HERDINGSCOUT},
                {Bots.SCOUTINGSCOUT, Bots.DENKILLERSOLDIER},
                {Bots.DENKILLERSOLDIER, Bots.CASTLESOLDIER},
                {Bots.DENKILLERSOLDIER, Bots.RUSHINGVIPER},
                {Bots.DENKILLERSOLDIER, Bots.HERDINGSCOUT},
                {Bots.PATROLSCOUT, Bots.BASETURRET},
                {Bots.CASTLESOLDIER, Bots.BASETURRET},
                {Bots.DENKILLERSOLDIER, null}
        };

        int[] times = {1, 3, 1, 1, 3, 1, 1, 1, 1, 1000};

        return new BuildOrder(buildOrder, times);
    }
}
