package team037.DataStructures;

import battlecode.common.*;

/**
 * Array that can contain up to 2000 MapLocation objects. Index of the next object is
 * always 1 + previous object's index, regardless of whether previous MapLocations still
 * exist in the array. Ensures only unique MapLocations are inserted.
 */
public class AppendOnlyMapLocationSet extends AppendOnlyMapLocationArray{

    public AppendOnlyMapLocationSet() {
        super();
    }

    @Override
    public void add(MapLocation m) {
        if (!contains(m)) {
            array[length] = m;
            length += 1;
        }
    }
}

