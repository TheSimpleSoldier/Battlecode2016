package team037.DataStructures;

import battlecode.common.*;

public class RobotTypeTracker
{
    public static RobotType robotType;
    public static RobotController rc;
    private static int[] ids;
    private static MapLocation[] locations;
    private static int size;
    private static int count;

    /**
     * This class if for keeping track of robots by id
     *
     * @param rt
     * @param robotController
     */
    public RobotTypeTracker(RobotType rt, RobotController robotController)
    {
        robotType = rt;
        rc = robotController;
        size = 10;
        count = 0;
        ids = new int[size];
        locations = new MapLocation[size];
    }

    /**
     * This method adds a part location to the array
     *
     * @param m
     * @param id
     */
    public static void add(MapLocation m, int id)
    {
        if (m == null)
            return;

        int index = getIndexOfID(id);

        if (index >= 0)
        {
            locations[index] = m;
            return;
        }

        count++;

        if (count >= locations.length * 0.7)
        {
            IncreaseSize();
        }

        index = id;

        while (ids[index % size] != 0) index++;

        locations[index % size] = m;
        ids[index % size] = id;
    }

    /**
     * This method increases the size of the hash map
     */
    public static void IncreaseSize()
    {
        size = locations.length * 2;
        MapLocation[] newLocs = new MapLocation[size];
        int[] newIds = new int[size];

        for (int i = locations.length; --i>=0; )
        {
            if (locations[i] != null)
            {
                int index = ids[i];

                index = Math.abs(index);

                while (newLocs[index % size] != null) index++;

                newLocs[index % size] = locations[i];
                newIds[index % size] = ids[i];
            }
        }

        locations = newLocs;
        ids = newIds;
    }



    /**
     * This method returns the index of a mapLocation stored in our array
     *
     * @param id
     * @return
     */
    public static int getIndexOfID(int id)
    {
        int index = id;
        while (locations[index % size] != null)
        {
            if (ids[index % size] == id)
            {
                return index % size;
            }
            index++;
        }

        return -1;
    }

    /**
     * This method adds a part to the correct location in an array
     */
    public static void remove(int index)
    {
        if (index == -1)
            return;

        locations[index] = null;
        ids[index] = 0;
        count--;

        int nextIndex = index+1;

        // need to re-order hash table
        while (locations[nextIndex % size] != null)
        {
            int currentIndex = nextIndex % size;
            MapLocation temp = locations[currentIndex];
            int tempScore = ids[currentIndex];

            locations[currentIndex] = null;
            ids[currentIndex] = 0;

            add(temp, tempScore);
            nextIndex++;
        }
    }

    /**
     * This method determines if a location has already been stored in our parts sorter
     *
     * @param id
     * @return
     */
    public static boolean contains(int id)
    {
        return getIndexOfID(id) > -1;
    }


    /**
     * This method adds any units with the correct type to the array
     *
     * @param allies
     */
    public void scanForRobots(RobotInfo[] allies)
    {
        for (int i = allies.length; --i>=0; )
        {
            if (allies[i].type == robotType)
            {
                add(allies[i].location, allies[i].ID);
            }
        }
    }
}
