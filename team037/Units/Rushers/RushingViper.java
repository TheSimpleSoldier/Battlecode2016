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

        if (updateTarget())
        {
            if (rushTarget != null)
            {
                lastTarget = rushTarget;

                Direction dir = currentLocation.directionTo(rushTarget).rotateRight();

                navigator.setTarget(currentLocation.add(dir, 5));
            }
            else
            {
                rushTarget = mapKnowledge.getOppositeCorner(start);
            }
        }

        if (rc.isCoreReady())
        {
            return navigator.takeNextStep();
        }

        return false;
    }

    public boolean updateTarget()
    {
        MapLocation target = navigator.getTarget();

        if (target == null)
            return true;

        if (currentLocation.equals(target))
            return true;

        if (currentLocation.isAdjacentTo(target))
            return true;

        if (rc.canSenseLocation(target) && rc.senseRubble(target) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            return true;

        if (rushTarget != null && !rushTarget.equals(lastTarget))
            return true;

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
