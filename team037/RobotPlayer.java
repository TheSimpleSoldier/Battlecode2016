package team037;

import battlecode.common.*;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Enums.Bots;
import team037.Enums.Strategies;
import team037.Units.AlphaArchon;
import team037.Units.BaseUnits.*;
import team037.Units.CastleUnits.CastleArchon;
import team037.Units.Scavenger.ScavengerArchon;
import team037.Units.ScoutBomb.ScoutBombArchon;
import team037.Units.SuperRush.SuperRushArchon;
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
        try
        {
            // this will check your ./bc.conf file for a line like this:
            // bc.testing.strat=foo
            // and strategy will be foo
            strategy = System.getProperty("bc.testing.strat");
            // IT DOESN'T WORK CURRENTLY :(
            // BUT THEY ARE FIXING IT!

            // hardcode disabled for now
            strategy = Strategies.SCOUT_BOMB;

            /*MapLocation[] us = rc.getInitialArchonLocations(rc.getTeam());
            MapLocation[] them = rc.getInitialArchonLocations(rc.getTeam().opponent());
            ZombieTracker zombieTracker = new ZombieTracker(rc);
            int[] size = StrategyUtilities.estimatedSize(us, them);
            int[] schedule = rc.getZombieSpawnSchedule().getRounds();

            if (((StrategyUtilities.averageDistToEnemyArchons(us, them) > 100) &&
                    size[0] * size[1] > 400 &&
                    !StrategyUtilities.enemyBetweenBuddies(us, them) &&
                    schedule[0] < 300 &&
                zombieTracker.getZombieStrength() >= 2))
            {
                strategy = Strategies.TURTLE;
            }
            else
            {
                strategy = Strategies.RUSH;
            }*/




            /*
            FOR TESTING ONLY remove if merging with master
             */
            if (rc.getTeam().equals(Team.A)) {
                strategy = Strategies.PACMAN;
            }





            if(rc.getRoundNum() == 0)
            {
                System.out.println(strategy);
            }

            RobotType type = rc.getType();

            //TODO: archon switch for round > 1 to differentite activated archons
            if(type == RobotType.ARCHON)
            {
                if(shouldRush(rc.getInitialArchonLocations(rc.getTeam().opponent()),
                        rc.getInitialArchonLocations(rc.getTeam()), rc.getLocation(), rc))
                {
                    strategy = Strategies.RUSH;
                }
                if(strategy.equals(Strategies.CASTLE))
                {
                    unit = new CastleArchon(rc);
                }
                else if(strategy.equals(Strategies.TURTLE))
                {
                    unit = new TurtleArchon(rc);
                    Unit.thisBot = Bots.TURTLEARCHON;
                }
                else if(strategy.equals(Strategies.SCOUT_BOMB))
                {
                    unit = new ScoutBombArchon(rc);
                    Unit.thisBot = Bots.SCOUTBOMBARCHON;
                }
                else if(strategy.equals(Strategies.PACMAN))
                {
                    unit = new ScavengerArchon(rc);
                    Unit.thisBot = Bots.SCAVENGERARCHON;
                }
                else if(strategy.equals(Strategies.RUSH))
                {
                    unit = new SuperRushArchon(rc);
                    Unit.thisBot = Bots.SUPERRUSHARCHON;
                }
                else
                { // default to alpha archons
                    unit = new AlphaArchon(rc);
                    Unit.thisBot = Bots.ALPHAARCHON;
                }
            }
            else if(type == RobotType.GUARD)
            {
                unit = new BaseGaurd(rc);
                Unit.thisBot = Bots.BASEGAURD;
            }
            else if(type == RobotType.SCOUT)
            {
                unit = new BaseScout(rc);
                Unit.thisBot = Bots.BASESCOUT;
            }
            else if(type == RobotType.SOLDIER)
            {
                unit = new BaseSoldier(rc);
                Unit.thisBot = Bots.BASESOLDIER;
            }
            else if(type == RobotType.TURRET || type == RobotType.TTM)
            {
                unit = new BaseTurret(rc);
                Unit.thisBot = Bots.BASETURRET;
            }
            else if(type == RobotType.VIPER)
            {
                unit = new BaseViper(rc);
                Unit.thisBot = Bots.BASEVIPER;
            }

            // initial update to strategy
            try
            {
                unit = unit.getNewStrategy(unit);
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        catch(Exception e){e.printStackTrace();}


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

                unit.suicide();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Clock.yield();
        }
    }

    private static boolean shouldRush(MapLocation[] enemyArchonStartLocs, MapLocation[] alliedArchonStartLocs,
                                   MapLocation currentLocation, RobotController rc)
    {
        int[] archons = new int[enemyArchonStartLocs.length];

        for(int k = archons.length; --k >= 0;)
        {
            int dist = currentLocation.distanceSquaredTo(enemyArchonStartLocs[k]);
            archons[k] += dist;
            for(int a = alliedArchonStartLocs.length; --a >= 0;)
            {
                if(!alliedArchonStartLocs[a].equals(currentLocation))
                {
                    if(alliedArchonStartLocs[a].distanceSquaredTo(enemyArchonStartLocs[k]) <= dist)
                    {
                        archons[k] += dist;
                    }
                }
            }

            MapLocation current = currentLocation;
            while(rc.canSenseLocation(current) && !current.equals(enemyArchonStartLocs[k]))
            {
                double rubble = rc.senseRubble(current);
                if(rubble >= 100)
                {
                    archons[k] += 1000;
                }
                current = current.add(current.directionTo(enemyArchonStartLocs[k]));
            }
        }

        int index = 0;
        int min = 9999999;
        for(int k = archons.length; --k >= 0;)
        {
            if(archons[k] < min)
            {
                min = archons[k];
                index = k;
            }
        }

        if(min < 100)
        {
            return true;
        }
        return false;
    }
}
