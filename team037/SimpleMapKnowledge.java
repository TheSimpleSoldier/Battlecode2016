package team037;

import battlecode.common.MapLocation;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.DataStructures.SimpleRobotInfo;

/**
 * Created by joshua on 1/13/16.
 */
public class SimpleMapKnowledge
{
    public int minX = Integer.MAX_VALUE;
    public int minY = Integer.MAX_VALUE;
    public int maxX = Integer.MIN_VALUE;
    public int maxY = Integer.MIN_VALUE;
    public SimpleRobotInfo[] ourArchons;
    public SimpleRobotInfo[] theirArchons;
    public AppendOnlyMapLocationSet dens;
    private int denStart = 0;

    public SimpleMapKnowledge()
    {
        ourArchons = new SimpleRobotInfo[4];
        theirArchons = new SimpleRobotInfo[4];
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

        for(int k = 0; k < 4; k++)
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

        for(int k = 0; k < 4; k++)
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

        for(int k = 0; k < 4; k++)
        {
            if(archons[k] == null)
            {
                return;
            }
            else if(archons[k].id == id)
            {
                archons[k] = null;
                for(int a = k + 1; a < 4; a++)
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
}
