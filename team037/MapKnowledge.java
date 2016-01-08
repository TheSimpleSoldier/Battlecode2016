package team037;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.Messages.MapBoundsCommunication;
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

    public RobotInfo[] enemyArchons = new RobotInfo[6];

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
     * This method updates an enemy archon in our database
     *
     * @param enemyArchon
     */
    public void addEnemyArchon(RobotInfo enemyArchon) {
        for (int i = enemyArchons.length; --i >=0; ) {
            if (enemyArchons[i] == null || enemyArchons[i].ID == enemyArchon.ID) {
                enemyArchons[i] = enemyArchon;
                break;
            }
        }
    }

    /**
     * This method naively returns a random archon's position stored in an int
     * @return
     */
    public int getArchonMessage() {
        if (enemyArchons[0] == null) {
            return 0;
        }

        int msg = 0;

        msg = enemyArchons[0].location.x;

        msg *= 100000;

        msg += enemyArchons[0].location.y;

        return msg;
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
            minX = MapUtils.senseEdge(rc, Direction.WEST);
        }
        if (maxX == Integer.MIN_VALUE) {
            maxX = MapUtils.senseEdge(rc, Direction.EAST);
        }
    }


    public MapBoundsCommunication packForMessage() {
        MapBoundsCommunication communication = new MapBoundsCommunication();

        if (minX != Integer.MIN_VALUE && maxX != Integer.MIN_VALUE) {
            communication.widthIndicator = 3;
            communication.xVal = minX + MAP_ADD;
            communication.width = maxX - minX;
        } else if (minX != Integer.MIN_VALUE) {
            communication.widthIndicator = 2;
            communication.xVal = minX + MAP_ADD;
            communication.width = 0;
        } else if (maxX != Integer.MIN_VALUE) {
            communication.widthIndicator = 1;
            communication.xVal = maxX + MAP_ADD;
            communication.width = 0;
        } else {
            communication.widthIndicator = 0;
            communication.xVal = 0;
            communication.width = 0;
        }


        if (minY != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE) {
            communication.heightIndicator = 3;
            communication.yVal = minY + MAP_ADD;
            communication.height = maxY - minY;
        } else if (minY != Integer.MIN_VALUE) {
            communication.heightIndicator = 2;
            communication.yVal = minY + MAP_ADD;
            communication.height = 0;
        } else if (maxY != Integer.MIN_VALUE) {
            communication.heightIndicator = 1;
            communication.yVal = maxY + MAP_ADD;
            communication.height = 0;
        } else {
            communication.heightIndicator = 0;
            communication.yVal = 0;
            communication.height = 0;
        }

        return communication;
    }

    public void setValueInDirection(int val, Direction d) {
        switch (d) {
            case NORTH:
                minY = val;
                break;
            case SOUTH:
                maxY = val;
                break;
            case WEST:
                minX = val;
                break;
            case EAST:
                maxX = val;
                break;
        }
    }

    public void updateEdgesFromMessage(MapBoundsCommunication communication) {
        int widthBit = communication.widthIndicator;
        int xCoord = communication.xVal;
        int width = communication.width;

        int heightBit = communication.heightIndicator;
        int yCoord = communication.yVal;
        int height = communication.height;


        if (widthBit == 0) {
            // we know nothing! (jon snow)
        } else if (widthBit == 1) {
            // we only have a endcoord
            maxX = xCoord - MAP_ADD;
        } else if (widthBit == 2) {
            // we only have the startcoord
            minX = xCoord - MAP_ADD;
        } else if (widthBit == 3) {
            // we have both
            minX = xCoord - MAP_ADD;
            maxX = xCoord - MAP_ADD + width;
        } else {
            System.out.println("control bit borked! in MapKnowledge");
        }


        if (heightBit == 0) {
            // w know nothing! (jon snow)
        } else if (heightBit == 1) {
            // we only have the endcoord
            maxY = yCoord - MAP_ADD;
        } else if (heightBit == 2) {
            // we only have the start coord
            minY = yCoord - MAP_ADD;
        } else if (heightBit == 3) {
            minY = yCoord - MAP_ADD;
            maxY = yCoord - MAP_ADD + height;
        } else {
            System.out.println("control bit borked! in MapKnowledge");
        }
    }


    public boolean mapBoundaryComplete() {
        return minX != Integer.MIN_VALUE && minY != Integer.MIN_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE;
    }


}