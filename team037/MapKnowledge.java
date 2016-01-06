package team037;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.Utilites.MapUtils;

public class MapKnowledge {
    public static final int MAP_ADD = 16100;

    // robot's knowledge of the map where MapLocation(x, y)
    public int minX = Integer.MIN_VALUE;
    public int maxX = Integer.MIN_VALUE;
    public int minY = Integer.MIN_VALUE;
    public int maxY = Integer.MIN_VALUE;

    public AppendOnlyMapLocationSet denLocations;
    public AppendOnlyMapLocationSet archonStartPosition;

    public MapKnowledge() {
        denLocations = new AppendOnlyMapLocationSet();
        archonStartPosition = new AppendOnlyMapLocationSet();
    }

    public void setMinX(int x) { minX = x; }
    public void setMaxX(int x) { maxX = x; }
    public void setMinY(int y) { minY = y; }
    public void setMaxY(int y) { maxY = y; }

    public void addDenLocation(MapLocation m) { denLocations.add(m); }
    public void addStartingArchonLocation(MapLocation m) { archonStartPosition.add(m); }

    public MapLocation nearestCorner(MapLocation m) {
        // TODO: implement this
        return null;
    }

    /**
     * In addition to reading in info from the comm, this should be called regularly
     */
    public void senseAndUpdateEdges(RobotController rc) throws GameActionException {
        if (minY == Integer.MIN_VALUE) {
            minY = MapUtils.senseEdge(rc, Direction.NORTH);
        }
        if (maxY == Integer.MIN_VALUE) {
            maxY = MapUtils.senseEdge(rc, Direction.SOUTH);
        }
        if (minX == Integer.MIN_VALUE) {
            minX = MapUtils.senseEdge(rc, Direction.EAST);
        }
        if (maxX == Integer.MIN_VALUE) {
            maxX = MapUtils.senseEdge(rc, Direction.WEST);
        }
    }


    public int[] packForMessage() {
        int[] packed = new int[6];

        if (minX != Integer.MIN_VALUE && maxX != Integer.MAX_VALUE) {
            packed[0] = 3;
            packed[1] = minX + MAP_ADD;
            packed[2] = maxX - minX;
        } else if (minX != Integer.MIN_VALUE) {
            packed[0] = 2;
            packed[1] = minX + MAP_ADD;
            packed[2] = 0;
        } else if (maxX != Integer.MIN_VALUE) {
            packed[0] = 1;
            packed[1] = maxX + MAP_ADD;
            packed[2] = 0;
        } else {
            packed[0] = 0;
            packed[1] = 0;
            packed[2] = 0;
        }


        if (minY != Integer.MIN_VALUE && maxY != Integer.MAX_VALUE) {
            packed[4] = 3;
            packed[5] = minY + MAP_ADD;
            packed[6] = maxY - minY;
        } else if (minY != Integer.MIN_VALUE) {
            packed[4] = 2;
            packed[5] = minY + MAP_ADD;
            packed[6] = 0;
        } else if (maxY != Integer.MIN_VALUE) {
            packed[4] = 1;
            packed[5] = maxY + MAP_ADD;
            packed[6] = 0;
        } else {
            packed[4] = 0;
            packed[5] = 0;
            packed[6] = 0;
        }

        return packed;
    }

    public void updateEdgesFromMessage(int[] results) {
        int widthBit = results[0];
        int xCoord = results[1];
        int width = results[2];

        int heightBit = results[3];
        int yCoord = results[4];
        int height = results[5];


        if (widthBit == 0) {
            // we know nothing! (jon snow)
        } else if (widthBit == 1) {
            // we only have a endcoord
            maxX = xCoord;
        } else if (widthBit == 2) {
            // we only have the startcoord
            minX = xCoord;
        } else if (widthBit == 3) {
            // we have both
            minX = xCoord;
            maxX = xCoord + width;
        } else {
            System.out.println("control bit borked! in MapKnowledge");
        }


        if (heightBit == 0) {
            // w know nothing! (jon snow)
        } else if (heightBit == 1) {
            // we only have the endcoord
            maxY = yCoord;
        } else if (heightBit == 2) {
            // we only have the start coord
            minY = yCoord;
        } else if (heightBit == 3) {
            minY = yCoord;
            maxY = yCoord + height;
        } else {
            System.out.println("control bit borked! in MapKnowledge");
        }
    }


}