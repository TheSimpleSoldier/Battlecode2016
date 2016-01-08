package team037.Units;

import battlecode.common.*;
import team037.Unit;

public class BaseScout extends Unit
{
    public BaseScout(RobotController rc)
    {
        super(rc);
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

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
