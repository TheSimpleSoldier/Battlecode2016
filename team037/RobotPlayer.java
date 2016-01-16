package team037;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.DataStructures.SimpleRobotInfo;
import team037.Enums.Bots;
import team037.Enums.Strategies;
import team037.Units.*;
import team037.Units.BaseUnits.*;
import team037.Units.CastleUnits.CastleArchon;
import team037.Units.CastleUnits.CastleSoldier;
import team037.Units.CastleUnits.CastleTurret;
import team037.Units.DenKillers.DenKillerSoldier;
import team037.Units.TurtleUnits.TurtleArchon;

public class RobotPlayer
{
    private static Unit unit;
    public static String strategy;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    public static void run(RobotController rc)
    {
        // Game loop that will execute very round
        while (true)
        {
            Clock.yield();
        }
    }
}
