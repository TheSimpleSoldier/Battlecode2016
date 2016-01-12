package team037;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.MapBoundsCommunication;
import team037.Utilites.MapUtils;

public class MapKnowledge {
    public static final int MAP_ADD = 16100;

    // robot's knowledge of the map where MapLocation(x, y)
    public int minX = Integer.MAX_VALUE;
    public int maxX = Integer.MIN_VALUE;
    public int minY = Integer.MAX_VALUE;
    public int maxY = Integer.MIN_VALUE;

    public boolean[] exploredRegions = new boolean[16];
    public boolean[] exploredEdges = new boolean[4];
    public boolean[] edgesBeingExplored = new boolean[4];
    public boolean[][] exploredLocsInRegion;

    public AppendOnlyMapLocationSet denLocations;
    public AppendOnlyMapLocationSet archonStartPosition;
    public AppendOnlyMapLocationSet ourTurretLocations;
    public AppendOnlyMapLocationSet partsAndNeutrals;

    public RobotInfo[] enemyArchons;
    public int[] enemiesLastUpdated;

    public MapKnowledge() {
        denLocations = new AppendOnlyMapLocationSet();
        archonStartPosition = new AppendOnlyMapLocationSet();
        ourTurretLocations = new AppendOnlyMapLocationSet();
        partsAndNeutrals = new AppendOnlyMapLocationSet();
        enemyArchons = new RobotInfo[4];
        enemiesLastUpdated = new int[]{0,0,0,0};
    }

    public void setMinX(int x) { minX = x; }
    public void setMaxX(int x) { maxX = x; }
    public void setMinY(int y) { minY = y; }
    public void setMaxY(int y) { maxY = y; }

    public void addDenLocation(MapLocation m) { denLocations.add(m); }
    public void addStartingArchonLocation(MapLocation m) { archonStartPosition.add(m); }
    public void addAlliedTurretLocation(MapLocation m) { ourTurretLocations.add(m); }
    public MapLocation[] getAlliedTurretLocations() { return ourTurretLocations.array; }
    public void addPartsAndNeutrals(MapLocation m) { partsAndNeutrals.add(m); }
    public MapLocation[] getPartsAndNeutrals() { return partsAndNeutrals.array; }
    public boolean partListed(MapLocation m) { return partsAndNeutrals.contains(m); }

    public MapLocation nearestCorner(MapLocation m) {
        // TODO: implement this
        return null;
    }

    /**
     * This method returns the closest discovered den
     *
     * @param currentLoc
     * @return
     */
    public MapLocation closestDen(MapLocation currentLoc)
    {
        MapLocation closest = null;
        int closestDist = Integer.MAX_VALUE;
        for (int i = denLocations.length; --i>=0;)
        {
            if (denLocations.array[i] == null)
                continue;

            MapLocation den = denLocations.array[i];
            int dist = den.distanceSquaredTo(currentLoc);
            if (closestDist > dist)
            {
                closest = den;
                closestDist = dist;
            }
        }

        return closest;
    }

    /**
     * This method removes enemy den locations that have been eliminated
     *
     * @param rc
     * @throws GameActionException
     */
    public MapLocation updateDens(RobotController rc) throws GameActionException
    {
        for (int i = denLocations.length; --i >= 0; )
        {
            MapLocation den = denLocations.array[i];
            if (den == null)
                continue;

            if (rc.canSenseLocation(den) && (rc.senseRobotAtLocation(den) == null || rc.senseRobotAtLocation(den).type != RobotType.ZOMBIEDEN))
            {
                denLocations.remove(den);
                return den;
            }
        }

        return null;
    }

    /**
     * In addition to reading in info from the comm, this should be called regularly
     */
    public void senseAndUpdateEdges() throws GameActionException
    {
        if (!exploredEdges[0])
        {
            int y = MapUtils.senseFarthest(Direction.NORTH);

            if (y < minY)
            {
                minY = y;
            }
        }
        if (!exploredEdges[1])
        {
            int y = MapUtils.senseFarthest(Direction.SOUTH);

            if (y > maxY)
            {
                maxY = y;
            }
        }
        if (!exploredEdges[2])
        {
            int x = MapUtils.senseFarthest(Direction.WEST);
            if (x < minX)
            {
                minX = x;
            }
        }
        if (!exploredEdges[3])
        {
            int x = MapUtils.senseFarthest(Direction.EAST);
            if (x > maxX)
            {
                maxX = x;
            }
        }
    }

    /**
     * This method updates the map edges from a msg
     *
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public boolean updateEdges(int minX, int minY, int maxX, int maxY)
    {
        if (inRegionMode()) updateRegions(maxX, maxY, minX, minY);

        boolean updated = false;

        if (minX < this.minX)
        {
            this.minX = minX;
            updated = true;
        }

        if (minY < this.minY)
        {
            this.minY = minY;
            updated = true;
        }

        if (maxX > this.maxX)
        {
            this.maxX = maxX;
            updated = true;
        }

        if (maxY > this.maxY)
        {
            this.maxY = maxY;
            updated = true;
        }

        if (updated)
        {
//            System.out.println("We updated");
        }

        return updated;
    }

    public void setValueInDirection(int val, Direction d) {
        switch (d) {
            case NORTH:
                if (inRegionMode()) updateRegions(maxX, maxY, minX, val);
                minY = val;
                break;
            case SOUTH:
                if (inRegionMode()) updateRegions(maxX, maxY, minX, val);
                maxY = val;
                break;
            case WEST:
                if (inRegionMode()) updateRegions(maxX, maxY, minX, val);
                minX = val;
                break;
            case EAST:
                if (inRegionMode()) updateRegions(maxX, maxY, minX, val);
                maxX = val;
                break;
        }
    }

    public boolean mapBoundaryComplete() {
        return minX != Integer.MIN_VALUE && minY != Integer.MIN_VALUE && maxX != Integer.MIN_VALUE && maxY != Integer.MIN_VALUE;
    }

    /**
     * This method returns the top left and bottom right corners of a square in the grid
     *
     *
     * 0000     0001    0010    0011
     *
     * 0100     0101    0110    0111
     *
     * 1000     1001    1010    1011
     *
     * 1100     1101    1110    1111
     *
     * @param numb
     * @return
     */
    public MapLocation[] getRegion(int numb, int minX, int maxX, int minY, int maxY)
    {
        int topX, topY, bottomX, bottomY;

        if (numb % 4 == 0)
        {
            topX = minX;
            bottomX = minX + (maxX - minX) / 4;
        }
        else if (numb % 4 == 1)
        {
            topX = minX + (maxX - minX) / 4 + 1;
            bottomX = minX + (maxX - minX) / 2;
        }
        else if (numb % 4 == 2)
        {
            topX = minX + (maxX - minX) / 2 + 1;
            bottomX = minX + 3 * (maxX - minX) / 4;
        }
        else
        {
            topX = minX + 3 * (maxX - minX) / 4 + 1;
            bottomX = maxX;
        }

        if (numb < 4)
        {
            topY = maxY;
            bottomY = minY + (maxY - minY / 4);
        }
        else if (numb < 8)
        {
            topY = minY + (maxY - minY / 4) + 1;
            bottomY = minY + (maxY - minY / 2);
        }
        else if (numb < 12)
        {
            topY = minY + (maxY - minY / 2) + 1;
            bottomY = minY + 3 * (maxY - minY / 4);
        }
        else
        {
            topY = minY + 3 * (maxY - minY / 4) + 1;
            bottomY = maxY;
        }

        return new MapLocation[]{new MapLocation(topX, topY), new MapLocation(bottomX, bottomY)};
    }

    /**
     * This method returns the region for a mapLocation
     *
     * 0    1   2   3
     * 4    5   6   7
     * 8    9   10  11
     * 12   13  14  15
     *
     * @param spot
     * @return
     */
    public int getRegion(MapLocation spot, int minX, int maxX, int minY, int maxY)
    {
        int x = spot.x;
        int y = spot.y;

        int row = 0;
        int col;

        if (x < minX + (maxX - minX) / 4)
        {
            col = 0;
        }
        else if (x < minX + (maxX - minX) / 2)
        {
            col = 1;
        }
        else if (x < minX + 3 * (maxX - minX) / 4)
        {
            col = 2;
        }
        else
        {
            col = 3;
        }

        if (y < minY + (maxY - minY) / 4)
        {
            row = 0;
        }
        else if (y < minY + (maxY - minY) / 2)
        {
            row = 1;
        }
        else if (y < minY + 3 * (maxY - minY) / 4)
        {
            row = 2;
        }
        else
        {
            row = 3;
        }

        return row * 4 + col;
    }

    /**
     * This method returns if the current region has been explored yet
     *
     * @param current
     * @return
     */
    public boolean exploredCurrentRegion(MapLocation current)
    {
        return exploredRegions[getRegion(current, minX, maxX, minY, maxY)];
    }

    /**
     * This method returns the center of a region
     *
     * @param region
     * @return
     */
    public MapLocation getRegionCenter(int region)
    {
        MapLocation[] bounds = getRegion(region, minX, maxX, minY, maxY);

        return new MapLocation((bounds[0].x + bounds[1].x) / 2, (bounds[0].y + bounds[1].y) / 2);
    }

    /**
     * This method returns the closest unexplored region
     *
     * @param current
     * @return
     */
    public int closestUnexploredRegion(MapLocation current)
    {
        int closestDist = Integer.MAX_VALUE;
        int region = -1;

        for (int i = 0; i < 16; i++)
        {
            if (!exploredRegions[i])
            {
                int dist = current.distanceSquaredTo(getRegionCenter(i));

                if (dist < closestDist)
                {
                    closestDist = dist;
                    region = i;
                }
            }
        }

        MapLocation[] corners = getRegion(region, minX, maxX, minY, maxY);
        int width = corners[1].x - corners[0].x;
        int height = corners[1].y - corners[0].y;

        if (width <= 0 || height <= 0)
            return -1;

        exploredLocsInRegion = new boolean[width][height];
        return region;
    }

    /**
     * This method updates the explored regions when a new bound is discovered
     *
     * @param newMaxX
     * @param newMaxY
     * @param newMinX
     * @param newMinY
     */
    public void updateRegions(int newMaxX, int newMaxY, int newMinX, int newMinY)
    {
        boolean[] updatedExploredRegions = new boolean[16];
        for (int i = 0; i < 16; i++)
        {
            if (exploredRegions[i])
            {
                if (newMaxX > maxX)
                {
                    if (i % 4 != 3)
                    {
                        updatedExploredRegions[i] = exploredRegions[i+1];
                    }
                }
                else if (newMaxY > maxY)
                {
                    if (i < 12)
                    {
                        updatedExploredRegions[i] = exploredRegions[i+4];
                    }
                }
                else if (newMinX < minX)
                {
                    if (i % 4 != 0)
                    {
                        updatedExploredRegions[i] = exploredRegions[i-1];
                    }
                }
                else if (newMinY < minY)
                {
                    if (i >= 4)
                    {
                        updatedExploredRegions[i] = exploredRegions[i-4];
                    }
                }
            }
        }
        exploredRegions = updatedExploredRegions;
    }

    /**
     * This method returns true if all regions have been searched
     *
     * @return
     */
    public boolean exporedAllRegions()
    {
       for (int i = 0; i < exploredRegions.length; i++)
       {
           if (!exploredRegions[i]) return false;
       }

        return true;
    }

    /**
     * This method returns the closest edge of the map
     *
     * North = 0
     * East = 1
     * South = 2
     * West = 3
     *
     * @param current
     * @return
     */
    public int getClosestDir(MapLocation current)
    {
        int maxXDist, maxYDist, minXDist, minYDist;
        boolean edgeNotExplored = false;

        // North
        if (exploredEdges[0] || edgesBeingExplored[0])
            minYDist = Integer.MAX_VALUE;
        else
        {
            minYDist = current.y - minY;
            edgeNotExplored = true;
        }

        // East
        if (exploredEdges[1] || edgesBeingExplored[1])
            maxXDist = Integer.MAX_VALUE;
        else
        {
            maxXDist = maxX - current.x;
            edgeNotExplored = true;
        }

        // South
        if (exploredEdges[2] || edgesBeingExplored[2])
            maxYDist = Integer.MAX_VALUE;
        else
        {
            maxYDist = maxY - current.y;
            edgeNotExplored = true;
        }

        // West
        if (exploredEdges[3] || edgesBeingExplored[3])
            minXDist = Integer.MAX_VALUE;
        else
        {
            minXDist = current.x - minX;
            edgeNotExplored = true;
        }

        // if all edges are being explored then stop
        if (!edgeNotExplored)
        {
            System.out.println("All edges have been explored");
            return -1;

        }


        boolean up = true;
        boolean left = true;

        if (minYDist < maxYDist)
            up = false;

        if (maxXDist < minXDist)
            left = false;

        if (up && left)
        {
            if (minYDist < minXDist)
                return 0; // NORTH
            else
                return 3; // EAST
        }
        else if (up && !left)
        {
            if (minYDist < maxXDist)
                return 0; // NORTH
            else
                return 1; // WEST
        }
        else if (!up && left)
        {
            if (maxYDist < minXDist)
                return 2; // SOUTH
            else
                return 3; // EAST
        }
        else
        {
            if (maxYDist < maxXDist)
                return 2; // SOUTH
            else
                return 1; // WEST
        }
    }

    /**
     * This method gets which direction you should go solely based on id
     *
     * @param id
     * @return
     */
    public int getDir(int id)
    {
        if (exploredEdges[id%4])
        {
            if (exploredEdges[(id+1)%4])
            {
                if (exploredEdges[(id+2)%4])
                {
                    if (exploredEdges[(id+3)%4])
                    {
                        return -1;
                    }
                    return (id+3)%4;
                }
                return (id+2)%4;
            }
            return (id+1)%4;
        }
        return id % 4;
    }

    /**
     * This method sets an edge to explored
     *
     * @param edge
     */
    public void reachedEdge(int edge)
    {
        exploredEdges[edge] = true;
    }

    /**
     * This method sets an edge as being explored
     *
     * @param edge
     */
    public void setEdgesBeingExplored(int edge)
    {
        edgesBeingExplored[edge] = true;
    }

    /**
     * Returns true if we have found 3 edges and false otherwise
     *
     * @return
     */
    public boolean inRegionMode()
    {
        int count = 0;
        for (int i = exploredEdges.length; --i>=0; )
        {
            if (exploredEdges[i]) count++;
        }

        return count >= 3;
    }

    /**
     * This method returns a map bounds communication
     * @return
     */
    public Communication getMapBoundsCommunication(int id)
    {
        Communication communication = new MapBoundsCommunication();

        int width = maxX - minX;
        int height = maxY - minY;

        /**
         * opcode = CommunicationType.fromInt(values[0]);
         widthIndicator = values[1];
         xVal = values[2];
         maxX = values[3];
         heightIndicator = values[4];
         yVal = values[5];
         maxY = values[6];
         */
        communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.MAP_BOUNDS), minX, width, minY, height});
        return communication;
    }

    public MapLocation nxtUnexploredSquare(int region)
    {
        MapLocation[] corners = getRegion(region, minX, maxX, minY, maxY);
        int topX = corners[0].x;
        int topY = corners[0].y;
        for (int i = exploredLocsInRegion.length; --i>=0;)
        {
            for (int j = exploredLocsInRegion[i].length; --j>=0;)
            {
                if (!exploredLocsInRegion[i][j])
                {
                    return new MapLocation(topX + i, topY + j);
                }
            }
        }
        return null;
    }

    /**
     * This method updates a region based on current loc
     *
     * @param region
     * @param rc
     */
    public void upDateExploredLocs(int region, RobotController rc)
    {
        MapLocation[] corners = getRegion(region, minX, maxX, minY, maxY);
        int topX = corners[0].x;
        int topY = corners[0].y;
        int bottomX = corners[1].x;
        int bottomY = corners[1].y;

        MapLocation[] seen = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), rc.getType().sensorRadiusSquared);

        for (int i = seen.length; --i>=0; )
        {
            int x = seen[i].x;
            int y = seen[i].y;

            if (x >= topX && x < bottomX && y >= topY && y < bottomY)
            {
                exploredLocsInRegion[x-topX][y-topY] = true;
            }
        }
    }

    /**
     * This method returns true if a region is explored false o.w.
     *
     * @param region
     * @return
     */
    public boolean regionExplored(int region)
    {
        for (int i = exploredLocsInRegion.length; --i>=0;)
        {
            for (int j = exploredLocsInRegion[i].length; --j>=0; )
            {
                if (!exploredLocsInRegion[i][j])
                    return false;
            }
        }
        exploredRegions[region] = true;
        return true;
    }

    public void updateArchonLocation(RobotInfo archon, int round)
    {
        for(int k = 0; k < 4; k++)
        {
            if(enemyArchons[k] == null)
            {
                return;
            }
            if(enemyArchons[k].ID == archon.ID)
            {
                enemyArchons[k] = archon;
                enemiesLastUpdated[k] = round;
                return;
            }
        }
    }

    public boolean checkIfArchon(int id)
    {
        for(int k = 0; k < 4; k++)
        {
            if(enemyArchons[k] == null)
            {
                return false;
            }
            if(enemyArchons[k].ID == id)
            {
                return true;
            }
        }

        return false;
    }

    public MapLocation getNearestEnemyArchon(MapLocation location)
    {
        double nearestDist = location.distanceSquaredTo(enemyArchons[0].location);
        MapLocation nearest = enemyArchons[0].location;
        for(int k = 1; k < 4; k++)
        {
            if(enemyArchons[k] == null)
            {
                break;
            }

            double tempDist = location.distanceSquaredTo(enemyArchons[k].location);

            if(tempDist < nearestDist)
            {
                nearest = enemyArchons[k].location;
                nearestDist = tempDist;
            }
        }

        return nearest;
    }

    /**
     * This method attempts to find the opposite corner of the map
     *
     * @param startLoc
     * @return
     */
    public MapLocation getOppositeCorner(MapLocation startLoc)
    {
        int x = startLoc.x - minX;
        int y = startLoc.y - minY;

        System.out.println("Opposite corner x: " + (maxX - x) + " y: " + (maxY - y));

        return new MapLocation(maxX - x, maxY - y);
    }

    /**
     * This method returns the archon's starting position com
     *
     * @return
     */
    public MapLocation getArchonCOM()
    {
        int x = 0;
        int y = 0;
        int count = 0;
        if (archonStartPosition.hasLocations())
        {
            for (int i = archonStartPosition.length; --i>=0;)
            {
                count++;
                x += archonStartPosition.array[i].x;
                y += archonStartPosition.array[i].y;
            }
        }

        return new MapLocation(x/count, y/count);
    }
}