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
        for (int i = length - 1; i >= 0; i--) {
            if (array[i].equals(m)) {
                return true;
            }
        }
        return false;
    }

    public MapLocation[] toDenseMapLocationArray() {
        MapLocation[] result = new MapLocation[length];
        for (int i = length - 1; i >= 0; i++ ) {
            result[i] = array[i];
        }
        return result;
    }
}

