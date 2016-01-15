package team037.Units.Scouts;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.EdgeDiscovered;
import team037.Messages.ExploringMapEdge;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.MapUtils;

public class ScoutingScout extends BaseScout
{
    public static Direction scoutDirection;
    public static int dir = -1;

    public ScoutingScout(RobotController rc)  {
        super(rc);
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
            nextBot = Bots.REGIONSCOUT;

        int edge = MapUtils.senseEdge(rc, scoutDirection);
        if (edge != Integer.MIN_VALUE && !mKnowledge.exploredEdges[dir])
        {
            mKnowledge.setValueInDirection(edge, scoutDirection);

            if (dir >= 0)
                mKnowledge.exploredEdges[dir] = true;

            scoutDirection = null;
            Communication communication = new EdgeDiscovered();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.EDGE_EXPLORED), id, dir});
            communicator.sendCommunication(2500, communication);

            communication = mKnowledge.getMapBoundsCommunication(id);
            communicator.sendCommunication(2500, communication);
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
        if (scoutDirection != null) return false;

        dir = mKnowledge.getClosestDir(currentLocation);
        if (dir == 0) {
            rc.setIndicatorString(0, "Going north");
            scoutDirection = Direction.NORTH;
        } else if (dir == 1) {
            rc.setIndicatorString(0, "Going West");
            scoutDirection = Direction.WEST;
        } else if (dir == 2) {
            rc.setIndicatorString(0, "Going South");
            scoutDirection = Direction.SOUTH;
        } else if (dir == 3) {
            rc.setIndicatorString(0, "Going East");
            scoutDirection = Direction.EAST;
        }

        if (dir != -1)
        {
            Communication communication = new ExploringMapEdge();
            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.EXPLORE_EDGE), 0, dir});
            communicator.sendCommunication(2500, communication);
            return true;
        }

        return false;
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