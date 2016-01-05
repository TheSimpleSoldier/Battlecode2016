package team037.Units;

import battlecode.common.*;
import team037.Unit;

public class BaseViper extends Unit
{
    public BaseViper(RobotController rc)
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
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByZombies);
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
