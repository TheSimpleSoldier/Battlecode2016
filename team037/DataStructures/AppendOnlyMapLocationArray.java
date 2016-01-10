package team037.DataStructures;

import battlecode.common.*;

public class AppendOnlyMapLocationArray {

    public MapLocation[] array;
    public int length;

    public AppendOnlyMapLocationArray() {
        array = new MapLocation[2000];
        length = 0;
    }

    public void add(MapLocation m) {
        array[length] = m;
        length += 1;
    }

    public boolean contains(MapLocation m) {
        for (int i = length; --i >= 0; ) {
            if (array[i] == null) continue;
            if (array[i].equals(m)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLocations()
    {
        for (int i = length; --i>=0; )
        {
            if (array[i] != null)
                return true;
        }
        return false;
    }

    public MapLocation[] toDenseMapLocationArray() {
        MapLocation[] result = new MapLocation[length];
        for (int i = length; --i >= 0; ) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * This method removes an item from the array
     *
     * @param m
     */
    public void remove(MapLocation m)
    {
        if (!contains(m))
            return;

        for (int i = length; --i>=0;)
        {
            if (array[i].equals(m))
            {
                array[i] = null;
                return;
            }
        }
    }
}

