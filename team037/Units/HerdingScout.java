package team037.Units;

import battlecode.common.*;
import team037.MapKnowledge;

/**
 * Created by joshua on 1/11/16.
 */
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
                move.setTarget(target);
            }
        }

        if(herding)
        {
            /*target = getTargetForHerding();
            move.setTarget(target);*/
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
                move.setTarget(currentLocation.add(dir.rotateRight(), 10));
                move.takeNextStep();
                move.setTarget(target);
                return true;
            }
            else
            {
                //time to move
                if(dist < minDist)
                {
                    return move.takeNextStep();
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
        MapLocation loc = mapKnowledge.getNearestEnemyArchon(rc.getLocation());
        if(loc != null)
        {
            return loc;
        }

        loc = mapKnowledge.getArchonCOM();
        if(loc != null)
        {
            loc = mapKnowledge.getOppositeCorner(loc);
            return loc.add(currentLocation.directionTo(loc), 100);
        }
        loc = mapKnowledge.getOppositeCorner(currentLocation);
        return loc.add(currentLocation.directionTo(loc), 100);
    }

    private MapLocation getTargetToWait()
    {
        MapLocation loc = mapKnowledge.closestDen(currentLocation);
        if(loc == null)
        {
            loc = currentLocation.add(currentLocation.directionTo(getTargetForHerding()), 5);
        }
        return loc;
    }
}
