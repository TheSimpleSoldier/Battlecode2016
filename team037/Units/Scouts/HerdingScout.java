package team037.Units.Scouts;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Utilites.MapUtils;
import team037.Units.BaseUnits.BaseScout;

public class HerdingScout extends BaseScout
{
    private int minDist = 24;
    private boolean herding;

    public HerdingScout(RobotController rc)
    {
        super(rc);
        herding = false;
        target = getTargetToWait();
    }

    @Override
    public boolean act() throws GameActionException
    {
        return takeNextStep();
    }

    @Override
    public boolean takeNextStep() throws GameActionException
    {
        if(!herding)
        {
            if(currentLocation.equals(target) || zombies.length > 0)
            {
                herding = true;
                target = getTargetForHerding();
                navigator.setTarget(target);
            }
        }

        if(herding)
        {
            target = getTargetForHerding();
            navigator.setTarget(target);
            if(target == null || !rc.isCoreReady() || currentLocation.equals(target) || zombies.length == 0)
            {
                return false;
            }
            int x = 0;
            int y = 0;
            double dist = Integer.MAX_VALUE;
            for(int k = zombies.length - 1; k >= 0; k--)
            {
                    double tempDist = zombies[k].location.distanceSquaredTo(currentLocation);
                    if(tempDist < dist)
                    {
                        dist = tempDist;
                    }

                    x += zombies[k].location.x;
                    y += zombies[k].location.y;
            }

            x /= zombies.length;
            y /= zombies.length;
            MapLocation center = new MapLocation(x, y);
            Direction dir = currentLocation.directionTo(target);
            Direction dirCenter = currentLocation.directionTo(center);

            //We are on the wrong side!
            if(dirCenter == dir || dirCenter.rotateLeft() == dir || dirCenter.rotateRight() == dir)
            {
                navigator.setTarget(currentLocation.add(dir.rotateRight(), 10));
                navigator.takeNextStep();
                navigator.setTarget(target);
                return true;
            }
            else
            {
                //time to move
                if(dist < minDist)
                {
                    return navigator.takeNextStep();
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return super.takeNextStep();
        }
    }

    /**
     * Updates everyone on the map where the zombies are
     * @return returns whether it broadcast or not
     * @throws GameActionException
     */
    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    @Override
    public void sendMessages()
    {
        return;
    }

    private MapLocation getTargetForHerding()
    {
        MapLocation loc = enemyArchonStartLocs[0];
        if(loc != null && currentLocation.distanceSquaredTo(loc) > 50)
        {
            return loc;
        }

        loc = mapKnowledge.getOppositeCorner(start);
        return loc.add(start.directionTo(loc), 100);
    }

    private MapLocation getTargetToWait()
    {
        MapLocation loc = MapUtils.getNearestLocation(mKnowledge.dens.array, currentLocation);
        if(loc == null)
        {
            loc = currentLocation.add(currentLocation.directionTo(getTargetForHerding()), 5);
        }
        return loc;
    }
}
