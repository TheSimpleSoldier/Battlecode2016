package team037.Units;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.Enums.Bots;
import team037.Unit;
import team037.Utilites.BuildOrderCreation;
import team037.Utilites.Utilities;

public class BaseArchon extends Unit
{
    private BuildOrder buildOrder;
    Bots nextBot;
    RobotType nextType;

    public BaseArchon(RobotController rc)
    {
        super(rc);
        buildOrder = BuildOrderCreation.createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Utilities.typeFromBot(nextBot);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }

    // maybe spawn a unit or repair a damaged unit
    public boolean carryOutAbility() throws GameActionException
    {
        if (nearByAllies.length > 0)
        {
            double weakestHealth = 9999;
            RobotInfo weakest = null;

            for (int i = nearByAllies.length; --i>=0; )
            {
                double health = nearByAllies[i].health;
                if (health < nearByAllies[i].maxHealth)
                {
                    if (health < weakestHealth)
                    {
                        weakestHealth = health;
                        weakest = nearByAllies[i];
                    }
                }
            }

            if (weakest != null)
            {
                rc.repair(weakest.location);
            }
        }

        if(rc.hasBuildRequirements(nextType) && rc.getCoreDelay() < 1)
        {
            for (int i = dirs.length; --i>=0; )
            {
                if (rc.canBuild(dirs[i], nextType))
                {
                    rc.build(dirs[i], nextType);
                    nextBot = buildOrder.nextBot();
                    nextType = Utilities.typeFromBot(nextBot);
                    return true;
                }
            }
        }

        return false;
    }
}
