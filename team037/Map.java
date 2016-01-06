package team037;

/**
 * Created by davej on 1/5/2016.
 */

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Map {

    public long[][] mapY;   // Column-major storage of the map.
    public long[][] mapX;   // Row-major storage of the map.
    public final int originX, originY;  // Origin in real coordinates.
    static RobotController rc;
    static RobotType ground = RobotType.ARCHON;
    static final int yOffset = 1024;
    static final int xOffset = 2048;

    /**
     * Constructor.
     * @param in_rc RobotController
     */
    public Map(RobotController in_rc) {
        rc = in_rc;
        MapLocation hqLocation = rc.getLocation();
        originX = hqLocation.x;
        originY = hqLocation.y;
        mapX = new long[256][4];
        mapY = new long[256][4];
    }

    /**
     * Methods to convert from map coordinates to array coordinates.
     * @param x coordinate (map)
     * @param y coordinate (map)
     * @return int[]{(array)x_coordinate,(array)y_coordinate
     */
    public int[] mapToArray(int x, int y) {
        return new int[]{x-originX+128, y-originY+128};
    }
    /**
     * @param location MapLocation at (map) (x,y)
     * @return int[]{(array)x_coordinate,(array)y_coordinate
     */
    public int[] mapToArray(MapLocation location) {
        return new int[]{location.x-originX+128, location.y-originY+128};
    }
    /**
     * @param location int[]{(map)x_coordinate,(map)y_coordinate}
     * @return int[]{(array)x_coordinate,(array)y_coordinate
     */
    public int[] mapToArray(int[] location) {
        return new int[]{location[0]-originX+128, location[1]-originY+128};
    }

    /**
     * Methods to convert from array coordinates to map coordinates.
     * @param x coordinate (array)
     * @param y coordinate (array)
     * @return MapLocation object at location (array) (x,y)
     */
    public MapLocation arrayToMap(int x, int y) {
        return new MapLocation(x-128+originX,y-128+originY);
    }
    /**
     * @param location int[]{(array)x_coordinate,(array)y_coordinate}
     * @return MapLocation object at location (array) (x,y)
     */
    public MapLocation arrayToMap(int[] location) {
        return new MapLocation(location[0]-128+originX,location[1]-128+originY);
    }

    public void scanRange(int x, int y, int range) {
        int maxX = x + range;
        int maxY = y + range;

        x -= range + 1;
        y -= range + 1;

        for (int i = y; i++ < maxY;) {
            // ((((mapX[i][(j) / 64] >>> (63 - (j % 64))) & 1L) == 1) && (((mapY[j][(i) / 64] >>> (63 - (i % 64))) & 1L) == 1))
            int yIndex = i / 64;
            long yValue = 1L << (63 - (i % 64));
            for (int j = x; j++ < maxX;) {
                MapLocation location = arrayToMap(j,i);
                if (rc.canSenseLocation(location)) {
                    if (rc.senseRubble(location) > 100) {
                        mapX[i][j/64] |= 1L << (63 - (j%64));
                        mapY[j][yIndex] |= yValue;
                    } else {
                        mapX[i][j/64] &= ~(1L << (63 - (j%64)));
                        mapY[j][yIndex] &= ~yValue;
                    }
                }
            }
        }
    }

//    public void readMapBroadcast(int y, int x, int range) {
//        switch(x%64) {
//            case 0:
//            case 1:
//            case 2:
//            case 3:
//            case 4:
//            case 5:
//            case 6:
//                switch(y%64) {
//                    case 0:
//                    case 1:
//                    case 2:
//                    case 3:
//                    case 4:
//                    case 5:
//                    case 6:
//                        // x and y check +1
//                        readMapBroadcastXYPlus(y,x,range);
//                        break;
//
//                    case 59:
//                    case 60:
//                    case 61:
//                    case 62:
//                    case 63:
//                    case 64:
//                        // x checks +1, y checks -1
//                        readMapBroadcastXPlusYMinus(y,x,range);
//                        break;
//
//                    default:
//                        // x checks +1
//                        readMapBroadcastXPlus(y,x,range);
//                        break;
//                }
//                break;
//
//            case 59:
//            case 60:
//            case 61:
//            case 62:
//            case 63:
//            case 64:
//                switch(y%64) {
//                    case 0:
//                    case 1:
//                    case 2:
//                    case 3:
//                    case 4:
//                    case 5:
//                    case 6:
//                        // x checks -1, y checks +1
//                        readMapBroadcastXMinusYPlus(y,x,range);
//                        break;
//
//                    case 59:
//                    case 60:
//                    case 61:
//                    case 62:
//                    case 63:
//                    case 64:
//                        // x and y check -1
//                        readMapBroadcastXYMinus(y,x,range);
//                        break;
//
//                    default:
//                        // x checks -1
//                        readMapBroadcastXMinus(y,x,range);
//                        break;
//                }
//                break;
//
//            default:
//                switch(y%64) {
//                    case 0:
//                    case 1:
//                    case 2:
//                    case 3:
//                    case 4:
//                    case 5:
//                    case 6:
//                        // y checks +1
//                        readMapBroadcastYPlus(y,x,range);
//                        break;
//
//                    case 59:
//                    case 60:
//                    case 61:
//                    case 62:
//                    case 63:
//                    case 64:
//                        // y checks -1
//                        readMapBroadcastYMinus(y,x,range);
//                        break;
//
//                    default:
//                        // check one channel only
//                        readMapBroadcast1(y,x,range);
//                        break;
//                }
//                break;
//        }
//    }
//
//    private void readMapBroadcast1(int y, int x, int radius) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex] = read & rc.readBroadcast(xIndex);
//                xIndex += 7;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex] = read & rc.readBroadcast(yIndex);
//                yIndex += 7;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastXPlus(int y, int x, int radius) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y][mapXIndex] = read & rc.readBroadcast(xIndex++);
//                read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex+1] = read & rc.readBroadcast(xIndex);
//                xIndex += 5;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex] = read & rc.readBroadcast(yIndex);
//                yIndex += 7;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastYPlus(int y, int x, int radius) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex] = read & rc.readBroadcast(xIndex);
//                xIndex += 7;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x][mapYIndex] = read & rc.readBroadcast(yIndex++);
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex+1] = read & rc.readBroadcast(yIndex);
//                yIndex += 5;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastXYPlus(int y, int x, int radius) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y][mapXIndex] = read & rc.readBroadcast(xIndex++);
//                read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex+1] = read & rc.readBroadcast(xIndex);
//                xIndex += 5;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x][mapYIndex] = read & rc.readBroadcast(yIndex++);
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex+1] = read & rc.readBroadcast(yIndex);
//                yIndex += 5;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastXMinus(int y, int x, int radius) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y][mapXIndex] = read & rc.readBroadcast(xIndex++);
//                read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex+1] = read & rc.readBroadcast(xIndex);
//                xIndex += 5;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex] = read & rc.readBroadcast(yIndex);
//                yIndex += 7;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastYMinus(int y, int x, int radius) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex] = read & rc.readBroadcast(xIndex);
//                xIndex += 7;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x][mapYIndex] = read & rc.readBroadcast(yIndex++);
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex+1] = read & rc.readBroadcast(yIndex);
//                yIndex += 5;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastXYMinus(int y, int x, int radius) {
//
//        int mapXIndex = (x / 64) - 1;
//        int mapYIndex = (y / 64) - 1;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y][mapXIndex] = read & rc.readBroadcast(xIndex++);
//                read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex+1] = read & rc.readBroadcast(xIndex);
//                xIndex += 5;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x][mapYIndex] = read & rc.readBroadcast(yIndex++);
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex+1] = read & rc.readBroadcast(yIndex);
//                yIndex += 5;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastXPlusYMinus(int y, int x, int radius) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = (y / 64) - 1;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y][mapXIndex] = read & rc.readBroadcast(xIndex++);
//                read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex+1] = read & rc.readBroadcast(xIndex);
//                xIndex += 5;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x][mapYIndex] = read & rc.readBroadcast(yIndex++);
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex+1] = read & rc.readBroadcast(yIndex);
//                yIndex += 5;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    private void readMapBroadcastXMinusYPlus(int y, int x, int radius) {
//
//        int mapXIndex = (x / 64) - 1;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = radius; i-- > 0;) {
//                long read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y][mapXIndex] = read & rc.readBroadcast(xIndex++);
//                read = ((long) rc.readBroadcast(xIndex++)) << 32;
//                mapX[y++][mapXIndex+1] = read & rc.readBroadcast(xIndex);
//                xIndex += 5;
//
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x][mapYIndex] = read & rc.readBroadcast(yIndex++);
//                read = ((long) rc.readBroadcast(yIndex++)) << 32;
//                mapY[x++][mapYIndex+1] = read & rc.readBroadcast(yIndex);
//                yIndex += 5;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
//
//    public void broadcastMap(int y, int x, int range) {
//
//        int mapXIndex = x / 64;
//        int mapYIndex = y / 64;
//
//        int yIndex = y * 8 + yOffset + mapYIndex * 2;
//        int xIndex = x * 8 + xOffset + mapXIndex * 2;
//
//        try {
//            for (int i = range; i-- > 0;) {
//                long write = mapX[y++][mapXIndex];
//                rc.broadcast(xIndex++, (int) (write >>> 32));
//                rc.broadcast(xIndex, (int) write);
//                xIndex += 7;
//
//                write = mapY[x++][mapYIndex];
//                rc.broadcast(yIndex++, (int) (write >>> 32));
//                rc.broadcast(yIndex, (int) write);
//                yIndex += 7;
//            }
//        } catch (GameActionException e) {
//
//        }
//    }
}
