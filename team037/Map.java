package team037;

/**
 * Created by davej on 1/5/2016.
 */

import battlecode.common.*;

public class Map {

    public long[][] mapY;   // Column-major storage of the map.
    public long[][] mapX;   // Row-major storage of the map.
    public final int originX, originY;  // Origin in real coordinates.
    static RobotController rc;
    static int[] perimeter, knight;
    static int radiusSq;

    /**
     * Constructor.
     * @param in_rc RobotController
     */
    public Map(RobotController in_rc) {
        rc = in_rc;

        switch(rc.getType().sensorRadiusSquared) {
            case 53:
                // Scout
            case 35:
                // Archon
                radiusSq = RobotType.ARCHON.sensorRadiusSquared;
                knight = new int[] {
                        9,11,13,24,
                        36,58,80,90,
                        99,97,95,84,
                        72,50,28,18,
                        41,31,33,45,
                        67,77,75,63,
                        42,43,44,53,54,55,64,65,66
                };
                perimeter = new int[] {
                        0,1,2,3,4,5,6,14,15,25,
                        26,37,48,59,70,81,92,91,101,100,
                        108,107,106,105,104,103,102,94,93,83,
                        82,71,60,49,38,27,16,17,7,8,
                        42,43,44,53,54,55,64,65,66
                };
                break;
            default:
                // 24 (all other units)
                radiusSq = RobotType.SOLDIER.sensorRadiusSquared;
                knight = new int[] {
                        0,2,4,11,
                        20,38,56,63,
                        68,66,64,57,
                        48,30,12,5,
                        23,15,17,27,
                        45,53,51,41,
                        24,25,26,33,34,35,42,43,44
                };
                perimeter = new int[] {
                        0,1,2,3,4,10,11,19,
                        20,29,28,47,56,55,63,62,
                        68,67,66,65,64,58,57,49,
                        48,39,30,21,12,13,5,6
                };

                break;
        }
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

    public void scan(MapLocation currentLoc) {
        try {
            switch (Clock.getBytecodesLeft() / 1000) {
                case 0:
                    break;
                case 1:
                case 2:
                    scanImmediateVicinity(currentLoc);
                    break;
                case 3:
                case 4:
                case 5:
                    scanPerimeter(currentLoc);
                    break;
                case 6:
                case 7:
                    scanKnight(currentLoc);
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    scanRange(currentLoc, RobotType.SOLDIER.sensorRadiusSquared);
                    break;
                default:
                    scanRange(currentLoc, radiusSq);
                    break;
            }
        } catch (GameActionException e) {}
    }

    private void scanPerimeter(MapLocation currentLoc) throws GameActionException {
        // ((((mapX[i][(j) / 64] >>> (63 - (j % 64))) & 1L) == 1) && (((mapY[j][(i) / 64] >>> (63 - (i % 64))) & 1L) == 1))
        double rubble = GameConstants.RUBBLE_OBSTRUCTION_THRESH;
//        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, rc.getType().sensorRadiusSquared);
        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, radiusSq);

        for (int i = perimeter.length; --i >= 0;) {

            MapLocation location = locations[perimeter[i]];

            int x = location.x-originX+128;
            int y = location.y-originY+128;

            if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
                mapX[y][x/64] |= 1L << (63 - (x%64));
                mapY[x][y/64] |= 1L << (63 - (y%64));
            } else {
                mapX[y][x/64] &= ~(1L << (63 - (x%64)));
                mapY[x][y/64] &= ~(1L << (63 - (y%64)));
            }
        }
    }

    private void scanKnight(MapLocation currentLoc) throws GameActionException {
        // ((((mapX[i][(j) / 64] >>> (63 - (j % 64))) & 1L) == 1) && (((mapY[j][(i) / 64] >>> (63 - (i % 64))) & 1L) == 1))
        double rubble = GameConstants.RUBBLE_OBSTRUCTION_THRESH;
//        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, rc.getType().sensorRadiusSquared);
        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, radiusSq);

        for (int i = knight.length; --i >= 0;) {

            MapLocation location = locations[knight[i]];

            int x = location.x-originX+128;
            int y = location.y-originY+128;

            if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
                mapX[y][x/64] |= 1L << (63 - (x%64));
                mapY[x][y/64] |= 1L << (63 - (y%64));
            } else {
                mapX[y][x/64] &= ~(1L << (63 - (x%64)));
                mapY[x][y/64] &= ~(1L << (63 - (y%64)));
            }
        }
    }

    private void scanImmediateVicinity(MapLocation currentLoc) throws GameActionException {

        MapLocation location = currentLoc;
        double rubble = GameConstants.RUBBLE_OBSTRUCTION_THRESH;
        int currentX = location.x-originX+128;
        int currentY = location.y-originY+128;
        int x = currentX;
        int y = currentY;

        if (rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        Direction direction = Direction.NORTH;
        location = currentLoc.add(direction);
        y = currentY-1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        direction = Direction.NORTH_EAST;
        location = currentLoc.add(direction);
        x = currentX+1;
        y = currentY-1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        direction = Direction.EAST;
        location = currentLoc.add(direction);
        x = currentX+1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        direction = Direction.SOUTH_EAST;
        location = currentLoc.add(direction);
        x = currentX+1;
        y = currentY+1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        direction = Direction.SOUTH;
        location = currentLoc.add(direction);
        y = currentY+1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        direction = Direction.SOUTH_WEST;
        location = currentLoc.add(direction);
        x = currentX-1;
        y = currentY+1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        direction = Direction.WEST;
        location = currentLoc.add(direction);
        x = currentX-1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
        direction = Direction.NORTH_WEST;
        location = currentLoc.add(direction);
        x = currentX-1;
        y = currentY-1;
        if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
            mapX[y][x/64] |= 1L << (63 - (x%64));
            mapY[x][y/64] |= 1L << (63 - (y%64));
        } else {
            mapX[y][x/64] &= ~(1L << (63 - (x%64)));
            mapY[x][y/64] &= ~(1L << (63 - (y%64)));
        }
    }

    private void scanRange(MapLocation currentLoc, int radiusSq) throws GameActionException {
        // ((((mapX[i][(j) / 64] >>> (63 - (j % 64))) & 1L) == 1) && (((mapY[j][(i) / 64] >>> (63 - (i % 64))) & 1L) == 1))
        double rubble = GameConstants.RUBBLE_OBSTRUCTION_THRESH;
//        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, rc.getType().sensorRadiusSquared);
        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, radiusSq);

        for (int i = locations.length; --i >= 0;) {

            MapLocation location = locations[i];

            int x = location.x-originX+128;
            int y = location.y-originY+128;

            if (!rc.onTheMap(location) || rc.senseRubble(location) > rubble) {
                mapX[y][x/64] |= 1L << (63 - (x%64));
                mapY[x][y/64] |= 1L << (63 - (y%64));
            } else {
                mapX[y][x/64] &= ~(1L << (63 - (x%64)));
                mapY[x][y/64] &= ~(1L << (63 - (y%64)));
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
