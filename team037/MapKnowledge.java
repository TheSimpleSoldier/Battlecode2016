package team037;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.Utilites.MapUtils;

public class MapKnowledge {

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


    /**
     * P IIII BB COOOOOOOOOOOORD WIIIDTH BB
     * 1   4   2        15          7    2   = 31
     * PPPPPPPPP COOOOOOOOOOOORD HEEIGHT
     *     9            15          7         = 31
     *
     * using substring it is zero based index
     *          first idx is the start (inclusive)
     *          seoncd idx is the end (exclusive)
     *
     * @param message
     */
    public void updateEdgesFromMessage(int[] message) {
        String one = Integer.toBinaryString(message[0]);
        String two = Integer.toBinaryString(message[1]);

        String widthBit = one.substring(4, 6);
        int xCoord = Integer.parseInt(one.substring(6, 21), 2);
        int width = Integer.parseInt(one.substring(21, 28), 2);

        String heightBit = one.substring(28);
        int yCoord = Integer.parseInt(two.substring(8, 25), 2);
        int height = Integer.parseInt(two.substring(25), 2);


        if (widthBit.equals("00")) {
            // we know nothing! (jon snow)
        } else if (widthBit.equals("01")) {
            // we only have a endcoord
            maxX = xCoord;
        } else if (widthBit.equals("10")) {
            // we only have the startcoord
            minX = xCoord;
        } else {
            // we have both
            minX = xCoord;
            maxX = xCoord + width;
        }


        if (heightBit.equals("00")) {
            // w know nothing! (jon snow)
        } else if (heightBit.equals("01")) {
            // we only have the endcoord
            maxY = yCoord;
        } else if (heightBit.equals("10")) {
            // we only have the start coord
            minY = yCoord;
        } else {
            minY = yCoord;
            maxY = yCoord + height;
        }
    }


}