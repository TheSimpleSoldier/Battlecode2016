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
        rc.setIndicatorString(2, "Rushing viper");
    }

    @Override
    public boolean act() throws GameActionException {
        if(fight() || fightZombies()) {
            return true;
        }


//        if (!rushing && rallyPoint != null)
//        {
////            System.out.println("Rally Point x: " + rallyPoint.x + " y: " + rallyPoint.y);
//            navigator.setTarget(rallyPoint);
//        }
//        else if (rushing && rushTarget != null && (navigator.getTarget() == null || rc.getLocation().equals(navigator.getTarget()) || rc.getLocation().isAdjacentTo(navigator.getTarget())))
//        {
//            Direction dir = currentLocation.directionTo(rushTarget).rotateRight();
//            target = rushTarget; //currentLocation.add(dir, 3);
//            navigator.setTarget(rushTarget);
//            System.out.println("Target x: " + target.x + " y: " + target.y);
//            rc.setIndicatorString(2, "Target x: " + target.x + " y: " + target.y);
//        }
//        else if (target == null || currentLocation.equals(target))
//        {
////            // move randomly
////            Direction dir = dirs[(int) (Math.random() * 8)];
////            target = currentLocation.add(dir, 3);
////            navigator.setTarget(target);
//        }

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
