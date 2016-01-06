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
        if (navigator.getTarget() == null) {
            MapLocation currentLoc = rc.getLocation();
            navigator.setTarget(new MapLocation(currentLoc.x, currentLoc.y + 17));
        }
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
        if (rc.hasBuildRequirements(RobotType.SOLDIER) && rc.getCoreDelay() < 1)
        {
            for (int i = dirs.length; --i>=0; )
            {
                if (rc.canBuild(dirs[i], RobotType.SOLDIER))
                {
                    rc.build(dirs[i], RobotType.SOLDIER);
                }
            }
        }
        return false;
    }
}
