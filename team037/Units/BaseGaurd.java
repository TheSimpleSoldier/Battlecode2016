package team037.Units;

import battlecode.common.*;
import team037.Unit;

public class BaseGaurd extends Unit
{
    public BaseGaurd(RobotController rc)
    {
        super(rc);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByEnemies);
        //return fightMicro.basicNetFightMicro(nearByEnemies, nearByAllies, enemies, allies, target);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByZombies);
        //return fightMicro.basicNetFightMicro(nearByZombies, nearByAllies, zombies, allies, target);
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
