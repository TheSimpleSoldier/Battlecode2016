package team037.Units.Scouts;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.ExploringMapEdge;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.MapUtils;

import java.util.Random;

public class ScoutingScout extends BaseScout
{
    public static Direction scoutDirection;
    public static int dir = -1;
    private Random random;

    public ScoutingScout(RobotController rc)  {
        super(rc);
        random = new Random(id);
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (rc.getRoundNum() % 5 == 0)
        {
            mKnowledge.senseAndUpdateEdges();
        }

        if (mKnowledge.inRegionMode())
            nextBot = Bots.SCOUTBOMBSCOUT;

        if(mKnowledge.edgeReached(scoutDirection))
        {
            scoutDirection = null;
        }
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        return currentLocation.add(scoutDirection, 100);
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        if(scoutDirection != null) return false;

        Direction[] dirs = new Direction[4];
        int num = 0;
        if(!mKnowledge.edgeReached(Direction.NORTH))
        {
            dirs[num] = Direction.NORTH;
            num++;
        }
        if(!mKnowledge.edgeReached(Direction.EAST))
        {
            dirs[num] = Direction.EAST;
            num++;
        }
        if(!mKnowledge.edgeReached(Direction.SOUTH))
        {
            dirs[num] = Direction.SOUTH;
            num++;
        }
        if(!mKnowledge.edgeReached(Direction.WEST))
        {
            dirs[num] = Direction.WEST;
            num++;
        }

        if(num == 0)
        {
            return false;
        }

        scoutDirection = dirs[Math.abs(random.nextInt()) % num];

        return true;
    }

    public static void updateScoutDirection()
    {
        scoutDirection = null;
    }

    public static int getScoutDir()
    {
        return dir;
    }
}