package team037;

/**
 * Created by davej on 1/5/2016.
 */

import battlecode.common.*;
import team037.Messages.Communication;

public class Map {

    public long[][] mapY;   // Column-major storage of the map.
    public long[][] mapX;   // Row-major storage of the map.
    public final int originX, originY;  // Origin in real coordinates.
    static RobotController rc;
    static int[] perimeter, knight;
    public static int radiusSq;

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

    public void updateFromComms(Communication com) {

        long[] x = new long[7];
        long[] y = new long[7];

        int[] values = com.getValues();

        long data = values[2];              // Lower 25 bits
        data += ((long) values[1]) << 25;   // Upper 24 bits

        if ((data & 0b1000000000000000000000000000000000000000000000000L)!= 0) {
            //2^48
            y[0] += 64;
            x[0] += 64;
        }
        if ((data & 0b100000000000000000000000000000000000000000000000L)!= 0) {
            //2^47
            y[0] += 32;
            x[1] += 64;
        }
        if ((data & 0b10000000000000000000000000000000000000000000000L)!= 0) {
            //2^46
            y[0] += 16;
            x[2] += 64;
        }
        if ((data & 0b1000000000000000000000000000000000000000000000L)!= 0) {
            //2^45
            y[0] += 8;
            x[3] += 64;
        }
        if ((data & 0b100000000000000000000000000000000000000000000L)!= 0) {
            //2^44
            y[0] += 4;
            x[4] += 64;
        }
        if ((data & 0b10000000000000000000000000000000000000000000L)!= 0) {
            //2^43
            y[0] += 2;
            x[5] += 64;
        }
        if ((data & 0b1000000000000000000000000000000000000000000L)!= 0) {
            //2^42
            y[0] += 1;
            x[6] += 64;
        }

        // x++

        if ((data & 0b100000000000000000000000000000000000000000L)!= 0) {
            //2^41
            y[1] += 64;
            x[0] += 32;
        }
        if ((data & 0b10000000000000000000000000000000000000000L)!= 0) {
            //2^40
            y[1] += 32;
            x[1] += 32;
        }
        if ((data & 0b1000000000000000000000000000000000000000L)!= 0) {
            //2^39
            y[1] += 16;
            x[2] += 32;
        }
        if ((data & 0b100000000000000000000000000000000000000L)!= 0) {
            //2^38
            y[1] += 8;
            x[3] += 32;
        }
        if ((data & 0b10000000000000000000000000000000000000L)!= 0) {
            //2^37
            y[1] += 4;
            x[4] += 32;
        }
        if ((data & 0b1000000000000000000000000000000000000L)!= 0) {
            //2^36
            y[1] += 2;
            x[5] += 32;
        }
        if ((data & 0b100000000000000000000000000000000000L)!= 0) {
            //2^35
            y[1] += 1;
            x[6] += 32;
        }

        // x++

        if ((data & 0b10000000000000000000000000000000000L)!= 0) {
            //2^34
            y[2] += 64;
            x[0] += 16;
        }
        if ((data & 0b1000000000000000000000000000000000L)!= 0) {
            //2^33
            y[2] += 32;
            x[1] += 16;
        }
        if ((data & 0b100000000000000000000000000000000L)!= 0) {
            //2^32
            y[2] += 16;
            x[2] += 16;
        }
        if ((data & 0b10000000000000000000000000000000L)!= 0) {
            //2^31
            y[2] += 8;
            x[3] += 16;
        }
        if ((data & 0b1000000000000000000000000000000L)!= 0) {
            //2^30
            y[2] += 4;
            x[4] += 16;
        }
        if ((data & 0b100000000000000000000000000000L)!= 0) {
            //2^29
            y[2] += 2;
            x[5] += 16;
        }
        if ((data & 0b10000000000000000000000000000L)!= 0) {
            //2^28
            y[2] += 1;
            x[6] += 16;
        }

        // x++

        if ((data & 0b1000000000000000000000000000L)!= 0) {
            //2^27
            y[3] += 64;
            x[0] += 8;
        }
        if ((data & 0b100000000000000000000000000L)!= 0) {
            //2^26
            y[3] += 32;
            x[1] += 8;
        }
        if ((data & 0b10000000000000000000000000L)!= 0) {
            //2^25
            y[3] += 16;
            x[2] += 8;
        }
        if ((data & 0b1000000000000000000000000L)!= 0) {
            //2^24
            y[3] += 8;
            x[3] += 8;
        }
        if ((data & 0b100000000000000000000000L)!= 0) {
            //2^23
            y[3] += 4;
            x[4] += 8;
        }
        if ((data & 0b10000000000000000000000L)!= 0) {
            //2^22
            y[3] += 2;
            x[5] += 8;
        }
        if ((data & 0b1000000000000000000000L)!= 0) {
            //2^21
            y[3] += 1;
            x[6] += 8;
        }

        // x++

        if ((data & 0b100000000000000000000L)!= 0) {
            //2^20
            y[4] += 64;
            x[0] += 4;
        }
        if ((data & 0b10000000000000000000L)!= 0) {
            //2^19
            y[4] += 32;
            x[1] += 4;
        }
        if ((data & 0b1000000000000000000L)!= 0) {
            //2^18
            y[4] += 16;
            x[2] += 4;
        }
        if ((data & 0b100000000000000000L)!= 0) {
            //2^17
            y[4] += 8;
            x[3] += 4;
        }
        if ((data & 0b10000000000000000L)!= 0) {
            //2^16
            y[4] += 4;
            x[4] += 4;
        }
        if ((data & 0b1000000000000000L)!= 0) {
            //2^15
            y[4] += 2;
            x[5] += 4;
        }
        if ((data & 0b100000000000000L)!= 0) {
            //2^14
            y[4] += 1;
            x[6] += 4;
        }

        // x++

        if ((data & 0b10000000000000L)!= 0) {
            //2^13
            y[5] += 64;
            x[0] += 2;
        }
        if ((data & 0b1000000000000L)!= 0) {
            //2^12
            y[5] += 32;
            x[1] += 2;
        }
        if ((data & 0b100000000000L)!= 0) {
            //2^11
            y[5] += 16;
            x[2] += 2;
        }
        if ((data & 0b10000000000L)!= 0) {
            //2^10
            y[5] += 8;
            x[3] += 2;
        }
        if ((data & 0b1000000000L)!= 0) {
            //2^9
            y[5] += 4;
            x[4] += 2;
        }
        if ((data & 0b100000000L)!= 0) {
            //2^8
            y[5] += 2;
            x[5] += 2;
        }
        if ((data & 0b10000000L)!= 0) {
            //2^7
            y[5] += 1;
            x[6] += 2;
        }

        // x++

        if ((data & 0b1000000L)!= 0) {
            //2^6
            y[6] += 64;
            x[0] += 1;
        }
        if ((data & 0b100000L)!= 0) {
            //2^5
            y[6] += 32;
            x[1] += 1;
        }
        if ((data & 0b10000L)!= 0) {
            //2^4
            y[6] += 16;
            x[2] += 1;
        }
        if ((data & 0b1000L)!= 0) {
            //2^3
            y[6] += 8;
            x[3] += 1;
        }
        if ((data & 0b100L)!= 0) {
            //2^2
            y[6] += 4;
            x[4] += 1;
        }
        if ((data & 0b10L)!= 0) {
            //2^1
            y[6] += 2;
            x[5] += 1;
        }
        if ((data & 0b1L)!= 0) {
            //2^0
            y[6] += 1;
            x[6] += 1;
        }

        int xIndex = com.signalLoc.x - originX + 125;
        int yIndex = com.signalLoc.y - originY + 131;

        int shift = 63 - (yIndex % 64);
        int index = yIndex / 64;
        long not = ~(64L << shift);
        if (shift > 54) {
            int index2 = index - 1;
            int shift2 = 64 - shift;
            long not2 = ~(64L >> shift2);

            mapY[xIndex][index] &= not;
            mapY[xIndex][index] |= y[0] << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= y[0] >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[1] << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= y[1] >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[2] << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= y[2] >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[3] << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= y[3] >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[4] << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= y[4] >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[5] << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= y[5] >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[6] << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= y[6] >> shift2;
        } else {
            mapY[xIndex][index] &= not;
            mapY[xIndex][index] |= y[0] << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[1] << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[2] << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[3] << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[4] << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[5] << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= y[6] << shift;
        }

        shift = 63 - (xIndex % 64);
        index = xIndex / 64;
        not = ~(64L << shift);

        if (shift > 54) {
            int index2 = index - 1;
            int shift2 = 64 - shift;
            long not2 = ~(64L >> shift2);

            mapX[yIndex][index] &= not;
            mapX[yIndex][index] |= x[6] << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= x[6] >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[5] << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= x[5] >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[4] << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= x[4] >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[3] << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= x[3] >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[2] << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= x[2] >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[1] << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= x[1] >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[0] << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= x[0] >> shift2;
        } else {
            mapX[yIndex][index] &= not;
            mapX[yIndex][index] |= x[6] << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[5] << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[4] << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[3] << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[2] << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[1] << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= x[0] << shift;
        }
    }

    public void scan(MapLocation currentLoc) {
        MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc,RobotType.SOLDIER.sensorRadiusSquared);
        scanSoldier(locs);
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

    public void scanImmediateVicinity(MapLocation currentLoc) throws GameActionException {

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


    public void scanAll(MapLocation currentLoc) {
        MapLocation[] inSight;
        switch (rc.getType()) {
            case ARCHON:
//                inSight = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, RobotType.ARCHON.sensorRadiusSquared);
                break;
            case SCOUT:
//                inSight = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, RobotType.SCOUT.sensorRadiusSquared);
//                scanArchon(inSight);
                break;
            default:
                inSight = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc, RobotType.SOLDIER.sensorRadiusSquared);
                scanSoldier(inSight);
                break;
        }
    }

    private void scanSoldier(MapLocation[] locations) {
        long[] x = new long[9];
        long[] y = new long[9];
        double rubbleThresh = GameConstants.RUBBLE_OBSTRUCTION_THRESH;

        try {
            MapLocation checkLoc = locations[4];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[0] += 4;
                x[6] += 256;
            }
            checkLoc = locations[3];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[0] += 8;
                x[5] += 256;
            }
            checkLoc = locations[2];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[0] += 16;
                x[4] += 256;
            }
            checkLoc = locations[1];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[0] += 32;
                x[3] += 256;
            }
            checkLoc = locations[0];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[0] += 64;
                x[2] += 256;
            }

            // y++

            checkLoc = locations[11];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[1] += 2;
                x[7] += 128;
            }
            checkLoc = locations[10];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[1] += 4;
                x[6] += 128;
            }
            checkLoc = locations[9];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[1] += 8;
                x[5] += 128;
            }
            checkLoc = locations[8];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[1] += 16;
                x[4] += 128;
            }
            checkLoc = locations[7];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[1] += 32;
                x[3] += 128;
            }
            checkLoc = locations[6];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[1] += 64;
                x[2] += 128;
            }
            checkLoc = locations[5];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[1] += 128;
                x[1] += 128;
            }


            // y++


            checkLoc = locations[20];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 1;
                x[8] += 64;
            }
            checkLoc = locations[19];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 2;
                x[7] += 64;
            }
            checkLoc = locations[18];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 4;
                x[6] += 64;
            }
            checkLoc = locations[17];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 8;
                x[5] += 64;
            }
            checkLoc = locations[16];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 16;
                x[4] += 64;
            }
            checkLoc = locations[15];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 32;
                x[3] += 64;
            }
            checkLoc = locations[14];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 64;
                x[2] += 64;
            }
            checkLoc = locations[13];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 128;
                x[1] += 64;
            }
            checkLoc = locations[12];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[2] += 256;
                x[0] += 64;
            }


            // y++


            checkLoc = locations[29];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 1;
                x[8] += 32;
            }
            checkLoc = locations[28];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 2;
                x[7] += 32;
            }
            checkLoc = locations[27];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 4;
                x[6] += 32;
            }
            checkLoc = locations[26];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 8;
                x[5] += 32;
            }
            checkLoc = locations[25];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 16;
                x[4] += 32;
            }
            checkLoc = locations[24];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 32;
                x[3] += 32;
            }
            checkLoc = locations[23];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 64;
                x[2] += 32;
            }
            checkLoc = locations[22];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 128;
                x[1] += 32;
            }
            checkLoc = locations[21];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[3] += 256;
                x[0] += 32;
            }

            // y++



            checkLoc = locations[38];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 1;
                x[8] += 16;
            }
            checkLoc = locations[37];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 2;
                x[7] += 16;
            }
            checkLoc = locations[36];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 4;
                x[6] += 16;
            }
            checkLoc = locations[35];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 8;
                x[5] += 16;
            }
            checkLoc = locations[34];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 16;
                x[4] += 16;
            }
            checkLoc = locations[33];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 32;
                x[3] += 16;
            }
            checkLoc = locations[32];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 64;
                x[2] += 16;
            }
            checkLoc = locations[31];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 128;
                x[1] += 16;
            }
            checkLoc = locations[30];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 256;
                x[0] += 16;
            }


            // y++

            checkLoc = locations[47];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 1;
                x[8] += 8;
            }
            checkLoc = locations[46];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 2;
                x[7] += 8;
            }
            checkLoc = locations[45];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 4;
                x[6] += 8;
            }
            checkLoc = locations[44];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 8;
                x[5] += 8;
            }
            checkLoc = locations[43];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 16;
                x[4] += 8;
            }
            checkLoc = locations[42];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 32;
                x[3] += 8;
            }
            checkLoc = locations[41];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 64;
                x[2] += 8;
            }
            checkLoc = locations[40];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 128;
                x[1] += 8;
            }
            checkLoc = locations[39];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[5] += 256;
                x[0] += 8;
            }

            // y++


            checkLoc = locations[56];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 1;
                x[8] += 4;
            }
            checkLoc = locations[55];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 2;
                x[7] += 4;
            }
            checkLoc = locations[54];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 4;
                x[6] += 4;
            }
            checkLoc = locations[53];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 8;
                x[5] += 4;
            }
            checkLoc = locations[52];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 16;
                x[4] += 4;
            }
            checkLoc = locations[51];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 32;
                x[3] += 4;
            }
            checkLoc = locations[50];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 64;
                x[2] += 4;
            }
            checkLoc = locations[49];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 128;
                x[1] += 4;
            }
            checkLoc = locations[48];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[6] += 256;
                x[0] += 4;
            }


            // y++



            checkLoc = locations[63];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[7] += 2;
                x[7] += 2;
            }
            checkLoc = locations[62];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[7] += 4;
                x[6] += 2;
            }
            checkLoc = locations[61];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[7] += 8;
                x[5] += 2;
            }
            checkLoc = locations[60];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[7] += 16;
                x[4] += 2;
            }
            checkLoc = locations[59];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[7] += 32;
                x[3] += 2;
            }
            checkLoc = locations[58];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[7] += 64;
                x[2] += 2;
            }
            checkLoc = locations[57];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[7] += 128;
                x[1] += 2;
            }


            // y++



            checkLoc = locations[68];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[8] += 4;
                x[6] += 1;
            }
            checkLoc = locations[67];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[8] += 8;
                x[5] += 1;
            }
            checkLoc = locations[66];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[8] += 16;
                x[4] += 1;
            }
            checkLoc = locations[65];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[8] += 32;
                x[3] += 1;
            }
            checkLoc = locations[64];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[8] += 64;
                x[2] += 1;
            }


            int xIndex = locations[0].x - originX + 128;
            int yIndex = locations[20].y - originY + 128;

            int shift = 63 - (yIndex % 64);
            int index = yIndex / 64;
            long not = ~(511L << shift);
            if (shift > 54) {
                int index2 = index - 1;
                int shift2 = 64 - shift;
                long not2 = ~(511L >> shift2);

                mapY[xIndex][index] &= not;
                mapY[xIndex][index] |= y[0] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[0] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[1] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[1] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[2] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[2] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[3] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[3] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[4] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[4] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[5] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[5] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[6] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[6] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[7] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[7] >> shift2;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[8] << shift;
                mapY[xIndex][index2] &= not2;
                mapY[xIndex][index2] |= y[8] >> shift2;
            } else {
                mapY[xIndex][index] &= not;
                mapY[xIndex][index] |= y[0] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[1] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[2] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[3] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[4] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[5] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[6] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[7] << shift;

                mapY[++xIndex][index] &= not;
                mapY[xIndex][index] |= y[8] << shift;
            }

            shift = 63 - (xIndex % 64);
            index = xIndex / 64;
            not = ~(511L << shift);

            if (shift > 54) {
                int index2 = index - 1;
                int shift2 = 64 - shift;
                long not2 = ~(511L >> shift2);

                mapX[yIndex][index] &= not;
                mapX[yIndex][index] |= x[8] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[8] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[7] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[7] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[6] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[6] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[5] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[5] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[4] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[4] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[3] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[3] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[2] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[2] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[1] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[1] >> shift2;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[0] << shift;
                mapX[yIndex][index2] &= not2;
                mapX[yIndex][index2] |= x[0] >> shift2;
            } else {
                mapX[yIndex][index] &= not;
                mapX[yIndex][index] |= x[8] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[7] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[6] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[5] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[4] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[3] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[2] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[1] << shift;

                mapX[--yIndex][index] &= not;
                mapX[yIndex][index] |= x[0] << shift;
            }
        } catch (GameActionException e) {}
    }

    private void scanArchon(MapLocation[] locations) {

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
