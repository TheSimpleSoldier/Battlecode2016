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
            if(target == null || !rc.isCoreReady() || currentLocation.equals(target) || zombies.length == 0)
            {
                return false;
            }
            MapLocation nearest = zombies[0].location;
            int x = 0;
            int y = 0;
            double dist = nearest.distanceSquaredTo(currentLocation);
            boolean counted = false;

            for(int k = zombies.length - 1; k >= 0; k--)
            {
                    double tempDist = zombies[k].location.distanceSquaredTo(currentLocation);
                    if(tempDist < dist)
                    {
                        dist = tempDist;
                        nearest = zombies[k].location;
                    }

                    x += zombies[k].location.x;
                    y += zombies[k].location.y;
            }

            x /= zombies.length;
            y /= zombies.length;
            MapLocation center = new MapLocation(x, y);
            Direction dir = currentLocation.directionTo(target);
            Direction dirCenter = currentLocation.directionTo(center);
            rc.setIndicatorString(1, "herding: " + nearest.toString() + " center: " + center.toString() + " target: " + target.toString());

            //We are on the wrong side!
            if(dirCenter == dir || dirCenter.rotateLeft() == dir || dirCenter.rotateRight() == dir)
            {
                rc.setIndicatorString(2, "moving around");
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
                    rc.setIndicatorString(2, "herding: " + dist + ", " + rc.getCoreDelay());
                    return move.takeNextStep();
                }
                else
                {
                    rc.setIndicatorString(2, "not herding: " + dist + ", " + rc.getCoreDelay());
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

    private MapLocation getTargetForHerding()
    {
        return new MapLocation(250, 120);
        //return target.add(Direction.SOUTH, 50);
    }

    private MapLocation getTargetToWait()
    {
        //MapLocation loc = mapKnowledge.closestDen(currentLocation);
        MapLocation loc = new MapLocation(241, 62);
        move.setTarget(loc.add(Direction.SOUTH, 3));
        return loc.add(Direction.SOUTH, 3);
    }
}
