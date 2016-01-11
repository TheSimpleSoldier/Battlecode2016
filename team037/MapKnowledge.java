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

    public boolean[] exploredRegions = new boolean[16];
    public boolean[] exploredEdges = new boolean[4];
    public boolean[] edgesBeingExplored = new boolean[4];

    public AppendOnlyMapLocationSet denLocations;
    public AppendOnlyMapLocationSet archonStartPosition;
    public AppendOnlyMapLocationSet ourTurretLocations;
    public AppendOnlyMapLocationSet partsAndNeutrals;

    public MapKnowledge() {
        denLocations = new AppendOnlyMapLocationSet();
        archonStartPosition = new AppendOnlyMapLocationSet();
        ourTurretLocations = new AppendOnlyMapLocationSet();
        partsAndNeutrals = new AppendOnlyMapLocationSet();
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

    /**
     * This method updates the map edges from a msg
     *
     * @param minX
     * @param minY
     * @param height
     * @param width
     */
    public boolean updateEdges(int minX, int minY, int height, int width)
    {
        if (inRegionMode()) updateRegions(minX + width, minY + height, minX, minY);

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

        if (minX + width > this.maxX)
        {
            this.maxX = minX + width;
            updated = true;
        }

        if (minY + height > this.maxY)
        {
            this.maxY = minY + height;
            updated = true;
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
            return -1;

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
}