package team037;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.DataStructures.SimpleRobotInfo;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.MapBoundsCommunication;
import team037.Utilites.MapUtils;

/**
 * Created by joshua on 1/13/16.
 */
public class MapKnowledge
{
    public static final int MAP_ADD = 16100;

    public int minX = Integer.MAX_VALUE;
    public int minY = Integer.MAX_VALUE;
    public int maxX = Integer.MIN_VALUE;
    public int maxY = Integer.MIN_VALUE;
    public SimpleRobotInfo[] ourArchons;
    public SimpleRobotInfo[] theirArchons;
    public AppendOnlyMapLocationSet dens;

    public MapKnowledge()
    {
        ourArchons = new SimpleRobotInfo[5];
        theirArchons = new SimpleRobotInfo[5];
    }

    public Communication getMapBoundsCommunication(int id)
    {
        Communication communication = new MapBoundsCommunication();

        int width = maxX - minX;
        int height = maxY - minY;

        communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.MAP_BOUNDS), minX, width, minY, height});
        return communication;
    }

    public void updateEdgesFromLocation(MapLocation location)
    {
        int x = location.x;
        int y = location.y;
        if(x < minX)
        {
            minX = x;
        }
        if(y < minY)
        {
            minY = y;
        }
        if(x > maxX)
        {
            maxX = x;
        }
        if(y > maxY)
        {
            maxY = y;
        }
    }

    public void updateEdgesFromInts(int minX, int minY, int width, int height)
    {
        if(minX < this.minX)
        {
            this.minX = minX;
        }
        if(minY < this.minY)
        {
            this.minY = minY;
        }
        if(minX + width > this.maxX)
        {
            this.maxX = minX + width;
        }
        if(minY + height > this.maxY)
        {
            this.maxY = minY + height;
        }
    }

    public void senseAndUpdateEdges() throws GameActionException
    {
        int y = MapUtils.senseFarthest(Direction.NORTH);

        if (y < minY)
        {
            minY = y;
        }

        y = MapUtils.senseFarthest(Direction.SOUTH);

        if (y > maxY)
        {
            maxY = y;
        }

        int x = MapUtils.senseFarthest(Direction.WEST);

        if (x < minX)
        {
            minX = x;
        }

        x = MapUtils.senseFarthest(Direction.EAST);

        if (x > maxX)
        {
            maxX = x;
        }
    }

    public void addArchon(SimpleRobotInfo archon, boolean us)
    {
        SimpleRobotInfo[] archons;
        if(us)
        {
            archons = ourArchons;
        }
        else
        {
            archons = theirArchons;
        }

        for(int k = 0; k < 5; k++)
        {
            if(archons[k] == null || archons[k].id == archon.id)
            {
                archons[k] = archon;
                return;
            }
        }
    }

    public void updateArchon(SimpleRobotInfo archon, boolean us)
    {
        SimpleRobotInfo[] archons;
        if(us)
        {
            archons = ourArchons;
        }
        else
        {
            archons = theirArchons;
        }

        for(int k = 0; k < 5; k++)
        {
            if(archons[k] == null)
            {
                return;
            }
            if(archons[k].id == archon.id)
            {
                archons[k] = archon;
                return;
            }
        }
    }

    public void deleteArchon(int id, boolean us)
    {
        SimpleRobotInfo[] archons;
        if(us)
        {
            archons = ourArchons;
        }
        else
        {
            archons = theirArchons;
        }

        for(int k = 0; k < 5; k++)
        {
            if(archons[k] == null)
            {
                return;
            }
            else if(archons[k].id == id)
            {
                archons[k] = null;
                for(int a = k + 1; a < 5; a++)
                {
                    if(archons[a] == null)
                    {
                        return;
                    }
                    else
                    {
                        archons[a - 1] = archons[a];
                    }
                }
            }
        }
    }

    public MapLocation getOppositeCorner(MapLocation startLoc)
    {
        int x = startLoc.x - minX;
        int y = startLoc.y - minY;

        return new MapLocation(maxX - x, maxY - y);
    }
}
