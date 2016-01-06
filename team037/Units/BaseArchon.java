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
        return false;
    }

    public boolean fightZombies() throws GameActionException
    {
        return false;
    }

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
    }

    // maybe spawn a unit?
    public boolean carryOutAbility() throws GameActionException
    {
        if (rc.hasBuildRequirements(RobotType.SOLDIER) && rc.hasBuildRequirements(RobotType.GUARD) && rc.getCoreDelay() < 1)
        {
            double choice = Math.random();
            for (int i = dirs.length; --i>=0; )
            {
                if (choice < 1 && rc.canBuild(dirs[i], RobotType.SOLDIER))
                {
                    rc.build(dirs[i], RobotType.SOLDIER);
                    return true;
                }
                else if (choice > 0.5 && rc.canBuild(dirs[i], RobotType.GUARD))
                {
                    rc.build(dirs[i], RobotType.GUARD);
                    return true;
                }
            }
        }
        return false;
    }
}
