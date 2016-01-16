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
import team037.Units.ScoutBomb.ScoutBombArchon;
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
        // this will check your ./bc.conf file for a line like this:
        // bc.testing.strat=foo
        // and strategy will be foo
        strategy = System.getProperty("bc.testing.strat");
        // IT DOESN'T WORK CURRENTLY :(
        // BUT THEY ARE FIXING IT!

        // hardcode disabled for now
        strategy = Strategies.TURRET_RUSH;


        RobotType type = rc.getType();

        if (type == RobotType.ARCHON)
        {
            if (strategy.equals(Strategies.CASTLE)) {
                unit = new CastleArchon(rc);
            } else if (strategy.equals(Strategies.TURTLE)) {
                unit = new TurtleArchon(rc);
                Unit.thisBot = Bots.TURTLEARCHON;
            } else if (strategy.equals(Strategies.SCOUT_BOMB)) {
                unit = new ScoutBombArchon(rc);
                Unit.thisBot = Bots.SCOUTBOMBARCHON;
            } else { // default to alpha archons
                unit = new AlphaArchon(rc);
                Unit.thisBot = Bots.ALPHAARCHON;
            }
        }
        else if (type == RobotType.GUARD)
        {
            unit = new BaseGaurd(rc);
            Unit.thisBot = Bots.BASEGAURD;
        }
        else if (type == RobotType.SCOUT)
        {
            unit = new BaseScout(rc);
            Unit.thisBot = Bots.BASESCOUT;
        }
        else if (type == RobotType.SOLDIER)
        {
            if (strategy.equals(Strategies.CASTLE)) {
                unit = new CastleSoldier(rc);
            } else {
                unit = new DenKillerSoldier(rc);
                Unit.thisBot = Bots.DENKILLERSOLDIER;
            }

        }
        else if (type == RobotType.TURRET || type == RobotType.TTM)
        {
            if (strategy.equals(Strategies.CASTLE)) {
                unit = new CastleTurret(rc);
            } else {
                unit = new BaseTurret(rc);
                Unit.thisBot = Bots.BASETURRET;
            }
        }
        else if (type == RobotType.VIPER)
        {
            unit = new BaseViper(rc);
            Unit.thisBot = Bots.BASEVIPER;
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

        if (rc.getRoundNum() == 0 && rc.getType() == RobotType.ARCHON)
        {
            BaseArchon.updateStartingMap();
            BaseArchon.sendOutInitialLocation();
            BaseArchon.mKnowledge.addArchon(new SimpleRobotInfo(rc.getID(), rc.getLocation(), RobotType.ARCHON, rc.getTeam()), true);
        }


        // Game loop that will execute very round
        while (true)
        {
            Unit.msgsSent = 0;
            try
            {
                unit.collectData();
                unit.handleMessages();
                unit.sendMessages();

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
