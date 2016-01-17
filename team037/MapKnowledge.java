package team037;

import battlecode.common.*;
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
    private static final int numArchons = 9;

    public int minX = Integer.MAX_VALUE;
    public int minY = Integer.MAX_VALUE;
    public int maxX = Integer.MIN_VALUE;
    public int maxY = Integer.MIN_VALUE;
    public boolean[] exploredEdges = new boolean[4];
    public SimpleRobotInfo[] ourArchons;
    public SimpleRobotInfo[] theirArchons;
    public AppendOnlyMapLocationSet dens;
    public boolean updated = false;

    public MapKnowledge()
    {
        ourArchons = new SimpleRobotInfo[numArchons];
        theirArchons = new SimpleRobotInfo[numArchons];
        dens = new AppendOnlyMapLocationSet();
    }

    public Communication getMapBoundsCommunication()
    {
        Communication communication = new MapBoundsCommunication();

        if(minX != Integer.MAX_VALUE && minY != Integer.MAX_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE)
        {
            int width = maxX - minX;
            int height = maxY - minY;

            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.MAP_BOUNDS), minX, width, minY, height, indicatorToEdge()});
            return communication;
        }
        return null;
    }

    public void updateEdgesFromLocation(MapLocation location)
    {
        int x = location.x;
        int y = location.y;
        if(x < minX)
        {
            minX = x;
            updated = true;
        }
        if(y < minY)
        {
            minY = y;
            updated = true;
        }
        if(x > maxX)
        {
            maxX = x;
            updated = true;
        }
        if(y > maxY)
        {
            maxY = y;
            updated = true;
        }
    }

    public void updateEdgesFromInts(int minX, int minY, int width, int height)
    {
        if(minX < this.minX)
        {
            this.minX = minX;
            updated = true;
        }
        if(minY < this.minY)
        {
            this.minY = minY;
            updated = true;
        }
        if(minX + width > this.maxX)
        {
            this.maxX = minX + width;
            updated = true;
        }
        if(minY + height > this.maxY)
        {
            this.maxY = minY + height;
            updated = true;
        }
    }

    public void updateEdgesFromMessage(Communication communication)
    {
        int[] values = communication.getValues();
        updateEdgesFromInts(values[1], values[3], values[2], values[4]);
        edgeToIndicator(values[5]);
    }

    public void senseAndUpdateEdges() throws GameActionException
    {
        int y;
        int x;

        if(!edgeReached(Direction.NORTH))
        {
            y = MapUtils.senseFarthest(Direction.NORTH);

            if(y < minY)
            {
                minY = y;
            }
        }

        if(!edgeReached(Direction.SOUTH))
        {
            y = MapUtils.senseFarthest(Direction.SOUTH);

            if(y > maxY)
            {
                maxY = y;
            }
        }

        if(!edgeReached(Direction.WEST))
        {
            x = MapUtils.senseFarthest(Direction.WEST);

            if(x < minX)
            {
                minX = x;
            }
        }

        if(!edgeReached(Direction.EAST))
        {
            x = MapUtils.senseFarthest(Direction.EAST);

            if(x > maxX)
            {
                maxX = x;
            }
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

        for(int k = 0; k < numArchons; k++)
        {
            if(archons[k] == null || archons[k].id == archon.id)
            {
                archons[k] = archon;
                return;
            }
        }
    }

    public MapLocation[] getArchonLocations(boolean us)
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

        MapLocation[] all = new MapLocation[numArchons];
        int last = 0;
        for(int k = 0; k < numArchons; k++)
        {
            if(archons[k] == null)
            {
                last = k;
                break;
            }
            else
            {
                all[k] = archons[k].location;
            }
        }

        MapLocation[] toReturn = new MapLocation[last];
        for(int k = 0; k < last; k++)
        {
            toReturn[k] = all[k];
        }

        return toReturn;
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

        for(int k = 0; k < numArchons; k++)
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

        for(int k = 0; k < numArchons; k++)
        {
            if(archons[k] == null)
            {
                return;
            }
            else if(archons[k].id == id)
            {
                archons[k] = null;
                for(int a = k + 1; a < numArchons; a++)
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

    public MapLocation updateDens(RobotController rc) throws GameActionException
    {
        for (int i = dens.length; --i >= 0; )
        {
            MapLocation den = dens.array[i];
            if (den == null)
                continue;

            if (rc.canSenseLocation(den) && (rc.senseRobotAtLocation(den) == null || rc.senseRobotAtLocation(den).type != RobotType.ZOMBIEDEN))
            {
                dens.remove(den);
                return den;
            }
        }

        return null;
    }

    public void reachEdge(Direction dir)
    {
        switch(dir)
        {
            case NORTH:
                exploredEdges[0] = true;
                break;
            case SOUTH:
                exploredEdges[2] = true;
                break;
            case EAST:
                exploredEdges[1] = true;
                break;
            case WEST:
                exploredEdges[3] = true;
                break;
        }
    }

    public boolean edgeReached(Direction dir)
    {
        switch(dir)
        {
            case NORTH:
                if(exploredEdges[0])
                {
                    return true;
                }
                return false;
            case SOUTH:
                if(exploredEdges[2])
                {
                    return true;
                }
                return false;
            case EAST:
                if(exploredEdges[1])
                {
                    return true;
                }
                return false;
            case WEST:
                if(exploredEdges[3])
                {
                    return true;
                }
                return false;
        }

        return false;
    }

    public int indicatorToEdge()
    {
        int value = 0;
        if(exploredEdges[0])
        {
            value = value | 8;
        }
        if(exploredEdges[1])
        {
            value = value | 4;
        }
        if(exploredEdges[2])
        {
            value = value | 2;
        }
        if(exploredEdges[3])
        {
            value = value | 1;
        }

        return value;
    }

    public void edgeToIndicator(int value)
    {
        int tempValue = value & 8;
        if(tempValue != 0)
        {
            exploredEdges[0] = true;
        }
        tempValue = value & 4;
        if(tempValue != 0)
        {
            exploredEdges[1] = true;
        }
        tempValue = value & 2;
        if(tempValue != 0)
        {
            exploredEdges[2] = true;
        }
        tempValue = value & 1;
        if(tempValue != 0)
        {
            exploredEdges[3] = true;
        }
    }

    public static int getRange()
    {
        return Unit.type.sensorRadiusSquared * 2;
    }
}
