package team037;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Enums.Bots;
import team037.Enums.Strategies;
import team037.Units.*;

public class RobotPlayer
{
    private static Unit unit;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    public static void run(RobotController rc)
    {
        // this will check your ./bc.conf file for a line like this:
        // bc.testing.strat=foo
        // and strategy will be foo
        String strategy = System.getProperty("bc.testing.strat");
        // IT DOESN'T WORK CURRENTLY :(
        // BUT THEY ARE FIXING IT!

        // hardcode disabled for now
        strategy = "castle";


        RobotType type = rc.getType();

        if (type == RobotType.ARCHON)
        {
            if (strategy.equals(Strategies.CASTLE)) {
                unit = new CastleArchon(rc);
            } else {
                //unit = new BaseArchon(rc);
                unit = new AlphaArchon(rc);
                unit.thisBot = Bots.ALPHAARCHON;
            }
        }
        else if (type == RobotType.GUARD)
        {
            unit = new BaseGaurd(rc);
            unit.thisBot = Bots.BASEGAURD;
        }
        else if (type == RobotType.SCOUT)
        {
            unit = new BaseScout(rc);
            unit.thisBot = Bots.BASESCOUT;
        }
        else if (type == RobotType.SOLDIER)
        {
            if (strategy.equals(Strategies.CASTLE)) {
                unit = new CastleSoldier(rc);
            } else {
                unit = new BaseSoldier(rc);
                unit.thisBot = Bots.BASESOLDIER;
            }

        }
        else if (type == RobotType.TURRET)
        {
            if (strategy.equals(Strategies.CASTLE)) {
                unit = new CastleTurret(rc);
            } else {
                unit = new BaseTurret(rc);
                unit.thisBot = Bots.BASETURRET;
            }
        }
        else if (type == RobotType.TTM)
        {
            unit = new BaseTTM(rc);
            unit.thisBot = Bots.BASETTM;
        }
        else if (type == RobotType.VIPER)
        {
            unit = new BaseViper(rc);
            unit.thisBot = Bots.BASEVIPER;
        }
        else if (type == RobotType.VIPER)
        {
            unit = new BaseViper(rc);
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
