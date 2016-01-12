package team037.Units.Rushers;

import battlecode.common.*;
import team037.Units.BaseViper;

public class RushingViper extends BaseViper
{
    private boolean rushing = true;

    public RushingViper(RobotController rc)
    {
        super(rc);
    }

    @Override
    public boolean act() throws GameActionException {
        if(fight() || fightZombies()) {
            return true;
        }

        if (!rushing && rallyPoint != null)
        {
//            System.out.println("Rally Point x: " + rallyPoint.x + " y: " + rallyPoint.y);
            navigator.setTarget(rallyPoint);
        }
        else if (rushing && rushTarget != null && (navigator.getTarget() == null || rc.getLocation().equals(navigator.getTarget())))
        {
            Direction dir = currentLocation.directionTo(rushTarget).rotateRight();
            target = currentLocation.add(dir, 5);
            navigator.setTarget(target);
            System.out.println("Target x: " + target.x + " y: " + target.y);
        }
        else if (target == null || currentLocation.equals(target))
        {
//            // move randomly
//            Direction dir = dirs[(int) (Math.random() * 8)];
//            target = currentLocation.add(dir, 3);
//            navigator.setTarget(target);
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
            return fightMicro.aggressiveFightMicro(nearByEnemies, nearByAllies, enemies);
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
