package team037.Units;

import battlecode.common.*;
import team037.Unit;

public class BaseTurret extends Unit
{
    public BaseTurret(RobotController rc)
    {
        super(rc);
    }

    // Turrets don't move
    public boolean takeNextStep() throws GameActionException
    {
        return false;
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByEnemies);
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
