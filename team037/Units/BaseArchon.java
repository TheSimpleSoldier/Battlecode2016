package team037.Units;

import battlecode.common.*;
import team037.Unit;

public class BaseArchon extends Unit
{
    public BaseArchon(RobotController rc)
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

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }

    // maybe spawn a unit?
    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
