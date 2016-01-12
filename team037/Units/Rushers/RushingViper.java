package team037.Units.Rushers;

import battlecode.common.*;
import team037.Units.BaseViper;

public class RushingViper extends BaseViper
{
    private boolean rushing = true;
    private MapLocation lastTarget = null;

    public RushingViper(RobotController rc)
    {
        super(rc);
    }

    @Override
    public boolean act() throws GameActionException {
        if(fight() || fightZombies()) {
            return true;
        }

        if (navigator.getTarget() == null || currentLocation.equals(navigator.getTarget()) || (rushTarget != null && !rushTarget.equals(lastTarget)))
        {
            if (rushTarget != null)
            {
                lastTarget = rushTarget;

                Direction dir = currentLocation.directionTo(rushTarget).rotateRight();

                navigator.setTarget(currentLocation.add(dir, 5));
            }
        }

        if (rc.isCoreReady())
        {
            return navigator.takeNextStep();
        }

        return false;
    }

    public boolean fight() throws GameActionException
    {
        if (rushing)
        {
            return fightMicro.aggressiveFightMicro(nearByEnemies, enemies, nearByAllies);
        }
        return fightMicro.basicNetFightMicro(nearByEnemies, nearByAllies, enemies, allies, target);
    }

    public boolean fightZombies() throws GameActionException
    {
        if (rushing)
            return false;

        return fightMicro.basicNetFightMicro(nearByZombies, nearByAllies, zombies, allies, target);
    }
}
