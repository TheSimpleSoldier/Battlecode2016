package team037.Units;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

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
            if(currentLocation.equals(target))
            {
                herding = true;
                target = getTarget();
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

            for(int k = zombies.length - 1; k >= 0; k--)
            {
                double tempDist = nearest.distanceSquaredTo(currentLocation);
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

            //We are on the wrong side!
            if(dirCenter == dir || dirCenter.rotateLeft() == dir || dirCenter.rotateRight() == dir)
            {

            }
            else
            {
                //time to move
                if(dist < minDist)
                {
                    if(rc.canMove(dir))
                    {
                        rc.move(dir);
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }


            return true;
        }
        else
        {
            return super.takeNextStep();
        }
    }

    /**
     * Updates everyone on the map where the zombie is
     * @return returns whether it broadcast or not
     * @throws GameActionException
     */
    @Override
    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }

    private MapLocation getTarget()
    {
        return null;
    }
}
