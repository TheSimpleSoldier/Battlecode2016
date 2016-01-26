package team037.DataStructures;

import battlecode.common.MapLocation;

/**
 * Creates an array of the given size, overwriting the oldest MapLocations
 * in the array when new MapLocations are added.
 */
public class MapLocationBuffer {
    private MapLocation[] buffer;
    private int index;
    private final int size;

    public MapLocationBuffer(int size) {
        this.size = size;
        index = 0;
        buffer = new MapLocation[size];
    }

    // Add a single location to the buffer
    public void addToBuffer(MapLocation location) {
        buffer[index] = location;
        index++;
        index %= size;
    }

    // Return the entire array and reinitialize it
    public MapLocation[] dumpBuffer() {
        MapLocation[] dump = buffer;
        buffer = new MapLocation[size];
        index = 0;
        return dump;
    }

    // Return the array without reinitialization
    public MapLocation[] getBuffer() {
        return buffer;
    }

    // Return whether this array contains anything
    public boolean isEmpty() {
        return buffer[(index-1)%size] == null;
    }

    // Return the index of the most recently added MapLocation
    public int newestIndex() {
        return index - 1;
    }

    // Return the size of the array
    public int size() { return size; }

    // Check to see if this array contains a location
    public boolean contains(MapLocation location) {
        for (int i = buffer.length; --i >= 0;) {
            if (buffer[i] != null && buffer[i].equals(location)) return true;
        }
        return false;
    }
}
