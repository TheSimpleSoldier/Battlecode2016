package team037;

import battlecode.common.*;
import team037.Units.*;

import java.util.Random;

public class RobotPlayer
{
    private static Unit unit;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    public static void run(RobotController rc)
    {
        RobotType type = rc.getType();

        if (type == RobotType.ARCHON)
        {
            //unit = new BaseArchon(rc);
            unit = new AlphaArchon(rc);
        }
        else if (type == RobotType.GUARD)
        {
            unit = new BaseGaurd(rc);
        }
        else if (type == RobotType.SCOUT)
        {
            //unit = new BaseScout(rc);
            unit = new ScoutingScout(rc);
        }
        else if (type == RobotType.SOLDIER)
        {
            unit = new BaseSoldier(rc);
        }
        else if (type == RobotType.TURRET)
        {
            unit = new BaseTurret(rc);
        }
        else if (type == RobotType.TTM)
        {
            unit = new BaseTTM(rc);
        }

        // initial update to strategy
        try
        {
            unit = unit.getNewStrategy(unit);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        // Game loop that will execute very round
        while (true)
        {
            try
            {
                unit.collectData();
                unit.handleMessages();

                // default is fight, fightZombie, carryOutAbility, takeNextStep
                unit.act();

                unit = unit.getNewStrategy(unit);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Clock.yield();
        }
    }
}
