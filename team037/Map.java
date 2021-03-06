package team037;

/**
 * Class stores and updates a two binary representations of map topography, which are transpositions of each other,
 * for efficient path finding.
 */

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;

public class Map {

    public long[][] mapY;   // Column-major storage of the map.
    public long[][] mapX;   // Row-major storage of the map.
    public final int originX, originY;  // Origin in real coordinates.
    static RobotController rc;
    public static double[] adjacentRubble;

    /**
     * Constructor.
     * @param in_rc RobotController
     */
    public Map(RobotController in_rc) {
        rc = in_rc;

        MapLocation originLocation = rc.getLocation();
        originX = originLocation.x;
        originY = originLocation.y;
        mapX = new long[256][4];
        mapY = new long[256][4];
        adjacentRubble = null;
    }

    /**
     * Return the distances from a given location to the nearest non-walkable locations.
     * @param currentLoc Unit's current location
     * @param direction Direction of the ping
     * @param width Width of the ping
     * @return Array of size width containing distances to the nearest non-walkable locations.
     */
    public int[] ping(MapLocation currentLoc, int direction, int width) {
        int[] pingData = new int[width];
        int x = currentLoc.x - originX + 128;
        int y = currentLoc.y - originY + 128;
        switch (direction) {
            case 0:
            case 1:
                x += width/2;
                y -= 1;
                // North
                for (int i = width; --i >= 0;) {
                    pingData[i] = Navigation.distanceLeft(y,x,mapY);
                    x -= 1;
                }
                break;

            case 2:
            case 3:
                y += width/2;
                x += 1;
                // East
                for (int i = width; --i >= 0;) {
                    pingData[i] = Navigation.distanceRight(x,y,mapX);
                    y -= 1;
                }
                break;

            case 4:
            case 5:
                x -= width/2;
                y += 1;
                // South
                for (int i = width; --i >= 0;) {
                    pingData[i] = Navigation.distanceRight(y,x,mapY);
                    x += 1;
                }
                break;

            case 6:
            case 7:
                y -= width/2;
                x -= 1;
                // West
                for (int i = width; --i >= 0;) {
                    pingData[i] = Navigation.distanceLeft(x,y,mapX);
                    y += 1;
                }
                break;
        }
        return pingData;
    }

    /**
     * Probe the north, south east, and west for the distances to the nearest rubble.
     * @param currentLoc current location
     * @return
     */
    public int[] probe(MapLocation currentLoc) {
        int[] probeData = new int[4];
        int x = currentLoc.x - originX + 128;
        int y = currentLoc.y - originY + 128;
        probeData[0] = Navigation.distanceLeft(y,x,mapY);
        probeData[1] = Navigation.distanceLeft(x,y,mapX);
        probeData[2] = Navigation.distanceRight(y,x,mapY);
        probeData[3] = Navigation.distanceRight(x,y,mapX);
        return probeData;
    }

    /**
     * @param location MapLocation at (map) (x,y)
     * @return int[]{(array)x_coordinate,(array)y_coordinate
     */
    public int[] mapToArray(MapLocation location) {
        return new int[]{location.x-originX+128, location.y-originY+128};
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
     * Scans 9 total MapLocations surrounding the unit.
     * @param currentLoc MapLocation of the unit's current location
     * @throws GameActionException
     */
    public double[] scanImmediateVicinity(MapLocation currentLoc) throws GameActionException {

        MapLocation location = currentLoc;
        double rubble = GameConstants.RUBBLE_OBSTRUCTION_THRESH;
        adjacentRubble = new double[8];

        long xNorth = 0;
        long xMid = 0;
        long xSouth = 0;

        long yWest = 0;
        long yMid = 0;
        long yEast = 0;

        if (rc.senseRubble(location) > rubble) {
            xMid += 2;
            yMid += 2;
        }
        location = currentLoc.add(Direction.NORTH);
        adjacentRubble[0] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[0] >= rubble) {
            xNorth += 2;
            yMid += 4;
        }
        location = currentLoc.add(Direction.NORTH_EAST);
        adjacentRubble[1] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[1] >= rubble) {
            xNorth += 1;
            yEast += 4;
        }
        location = currentLoc.add(Direction.EAST);
        adjacentRubble[2] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[2] >= rubble) {
            xMid += 1;
            yEast += 2;
        }
        location = currentLoc.add(Direction.SOUTH_EAST);
        adjacentRubble[3] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[3] >= rubble) {
            xSouth += 1;
            yEast += 1;
        }
        location = currentLoc.add(Direction.SOUTH);
        adjacentRubble[4] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[4] >= rubble) {
            xSouth += 2;
            yMid += 1;
        }
        location = currentLoc.add(Direction.SOUTH_WEST);
        adjacentRubble[5] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[5] >= rubble) {
            xSouth += 4;
            yWest += 1;
        }
        location = currentLoc.add(Direction.WEST);
        adjacentRubble[6] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[6] >= rubble) {
            xMid += 4;
            yWest += 2;
        }
        location = currentLoc.add(Direction.NORTH_WEST);
        adjacentRubble[7] = rc.senseRubble(location);
        if (!rc.onTheMap(location) || adjacentRubble[7] >= rubble) {
            xNorth += 4;
            yWest += 4;
        }

        int xIndex = location.x-originX+127;
        int yIndex = location.y-originY+129;

        int shift = 63 - (yIndex % 64);
        int index = yIndex / 64;
        long not = ~(7L << shift);
        if (shift > 61) {
            int index2 = index - 1;
            int shift2 = 64 - shift;
            long not2 = ~(7L >> shift2);

            mapY[xIndex][index] &= not;
            mapY[xIndex][index] |= yWest << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= yWest >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= yMid << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= yMid >> shift2;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= yEast << shift;
            mapY[xIndex][index2] &= not2;
            mapY[xIndex][index2] |= yEast >> shift2;
        } else {
            mapY[xIndex][index] &= not;
            mapY[xIndex][index] |= yWest << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= yMid << shift;

            mapY[++xIndex][index] &= not;
            mapY[xIndex][index] |= yEast << shift;
        }

        shift = 63 - (xIndex % 64);
        index = xIndex / 64;
        not = ~(7L << shift);

        if (shift > 61) {
            int index2 = index - 1;
            int shift2 = 64 - shift;
            long not2 = ~(7L >> shift2);

            mapX[yIndex][index] &= not;
            mapX[yIndex][index] |= xSouth << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= xSouth >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= xMid << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= xMid >> shift2;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= xNorth << shift;
            mapX[yIndex][index2] &= not2;
            mapX[yIndex][index2] |= xNorth >> shift2;
        } else {
            mapX[yIndex][index] &= not;
            mapX[yIndex][index] |= xSouth << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= xMid << shift;

            mapX[--yIndex][index] &= not;
            mapX[yIndex][index] |= xNorth << shift;
        }
//        printMap(currentLoc,1);
        return adjacentRubble;
    }


    /**
     * Scans all MapLocations in a 24 radius squared area around the unit
     * @param currentLoc MapLocation of the unit's current location
     */
    public double[] scan(MapLocation currentLoc) {
        MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(currentLoc,24);

        long[] x = new long[9];
        long[] y = new long[9];
        double rubbleThresh = GameConstants.RUBBLE_OBSTRUCTION_THRESH;

        adjacentRubble = new double[8];

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
            adjacentRubble[5] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[5] >= rubbleThresh) {
                y[3] += 8;
                x[5] += 32;
            }
            checkLoc = locations[25];
            adjacentRubble[6] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[6] >= rubbleThresh) {
                y[3] += 16;
                x[4] += 32;
            }
            checkLoc = locations[24];
            adjacentRubble[7] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[7] >= rubbleThresh) {
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
            adjacentRubble[4] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[4] >= rubbleThresh) {
                y[4] += 8;
                x[5] += 16;
            }
            checkLoc = locations[34];
            if (!rc.onTheMap(checkLoc) || rc.senseRubble(checkLoc) >= rubbleThresh) {
                y[4] += 16;
                x[4] += 16;
            }
            checkLoc = locations[33];
            adjacentRubble[0] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[0] >= rubbleThresh) {
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
            adjacentRubble[3] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[3] >= rubbleThresh) {
                y[5] += 8;
                x[5] += 8;
            }
            checkLoc = locations[43];
            adjacentRubble[2] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[2] >= rubbleThresh) {
                y[5] += 16;
                x[4] += 8;
            }
            checkLoc = locations[42];
            adjacentRubble[1] = rc.senseRubble(checkLoc);
            if (!rc.onTheMap(checkLoc) || adjacentRubble[1] >= rubbleThresh) {
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
            if (shift > 55) {
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

            if (shift > 55) {
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
//            printMap(currentLoc,4);
        } catch (GameActionException e) {}
        return adjacentRubble;
    }


    /**
     * Updates the map arrays from a Communnication object of type Rubble (RubbleCommunication)
     * @param com RubbleCommunication object
     */
    public void updateFromComms(Communication com) {

        if (com == null || com.opcode != CommunicationType.RUBBLE) return;

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
        long not = ~(127L << shift);
        if (shift > 57) {
            int index2 = index - 1;
            int shift2 = 64 - shift;
            long not2 = ~(127L >> shift2);

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
        not = ~(127L << shift);

        if (shift > 57) {
            int index2 = index - 1;
            int shift2 = 64 - shift;
            long not2 = ~(127L >> shift2);

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

//        printMap(com.signalLoc,3);
    }

    /**
     * Prints the map to verify that mapX and mapY align.
     * @param center MapLocation representing the center of the scan
     * @param offset int subtracted from the center's x and y to get the northwest corner of the scan, and added
     *               to get the southeast corner.
     */
    public void printMap(MapLocation center, int offset) {
        System.out.println();
        int[] loc = mapToArray(center);
        int xStart = loc[0] - offset;
        int yStart = loc[1] - offset;
        int xMax = loc[0] + offset;
        int yMax = loc[1] + offset;
        String[] newMap = new String[offset + offset + 1];
        for (int i = yStart; i <= yMax; i++) {
            String build = "";
            for (int j = xStart; j <= xMax; j++) {
                if ((((mapX[i][(j) / 64] >>> (63 - (j % 64))) & 1L) == 1) &&
                        ((mapY[j][(i) / 64] >>> (63 - (i % 64))) & 1L) == 1) {
                    build = build.concat("X ");
                } else {
                    build = build.concat("- ");
                }
            }
            newMap[i - yStart] = build;
        }


        System.out.println("Center: (" + center.x + "," + center.y + ")");
        for (int i = 0; i < newMap.length; i++) {
            System.out.println(newMap[i]);
        }
        System.out.println();
    }
}
