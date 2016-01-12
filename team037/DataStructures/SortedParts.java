package team037.DataStructures;

import battlecode.common.*;

public class SortedParts
{
    MapLocation[] locs;
    double[] score;

    int start;
    int end;

    /**
     * This class is for use of archons and it gives the Archon
     * the best location to go to based on a ratio of parts to distance
     */
    public SortedParts()
    {
        locs = new MapLocation[2000];
        score = new double[2000];
        start = 1000;
        end = 1000;
    }

    /**
     * This method gets the location with the highest score
     *
     * @return
     */
    public MapLocation getBestSpot()
    {
        if (end == start)
            return null;

        return locs[end--];
    }

    /**
     * This method adds a part location to the array
     *
     * @param m
     * @param currentSpot
     * @param value
     * @param neutral
     */
    public void addParts(MapLocation m, MapLocation currentSpot, int value, boolean neutral)
    {
        if (m == null || currentSpot == null)
            return;

        double newValue = value;

        // b/c neutral parts don't have a build time
        // they are more valuable than parts
        if (neutral)
            newValue *= 2;

        // the score is equal to
        newValue = newValue / Math.sqrt(m.distanceSquaredTo(currentSpot));
        sort(newValue, m);
    }

    /**
     * This method adds a part to the correct location in an array
     *
     * @param value
     * @param mapLocation
     */
    public void sort(double value, MapLocation mapLocation)
    {
        int index = getIndex(start, end, value);

        if (index == start)
        {
            score[start] = value;
            locs[start] = mapLocation;
            start--;
        }
        else if (index == end)
        {
            score[end] = value;
            locs[end] = mapLocation;
            end++;
        }
        // if we are ahead of center push everything forward
        else if (start + (end - start) / 2 <= index)
        {
            int iterator = end;
            while (iterator >= index)
            {
                locs[iterator+1] = locs[iterator];
                score[iterator+1] = score[iterator];
                iterator--;
            }
            locs[iterator] = mapLocation;
            score[iterator] = value;
            end++;
        }
        // push everything ahead
        else
        {
            int iterator = start;
            while (iterator <= index)
            {
                locs[iterator-1] = locs[iterator];
                score[iterator-1] = score[iterator];
                iterator++;
            }
            locs[iterator] = mapLocation;
            score[iterator] = value;
            start--;
        }
    }

    /**
     * This method gets the index where a value should be
     *
     * @param start
     * @param stop
     * @param value
     * @return
     */
    public int getIndex(int start, int stop, double value)
    {
        if (start - stop <= 1)
            return start;

        int middle = start + (stop - start) / 2;

        // score lower than middle
        if (score[middle] > value)
            return getIndex(start, middle, value);
        // score higher than middle
        else
            return getIndex(middle, start, value);
    }

    /**
     * This method determines if a location has already been stored in our parts sorter
     *
     * @param m
     * @return
     */
    public boolean contains(MapLocation m)
    {
        for (int i = start; i < end; i++)
        {
            if (locs[i] == null)
                continue;

            if (locs[i].equals(m))
                return true;
        }
        return false;
    }

    /**
     * This method looks for any parts or neutrals it can see and then adds them
     * if they are unique
     *
     * @param rc
     */
    public void findPartsAndNeutralsICanSense(RobotController rc)
    {
        MapLocation currentLocation = rc.getLocation();
        RobotType type = rc.getType();
        int sensorRadiusSquared = type.sensorRadiusSquared;

        MapLocation[] parts = rc.sensePartLocations(rc.getType().sensorRadiusSquared);

        for (int i = parts.length; --i>=0; )
        {
            if (!contains(parts[i]))
            {
                addParts(parts[i], currentLocation, (int)rc.senseParts(parts[i]), false);
            }
        }

        RobotInfo[] neutralBots = rc.senseNearbyRobots(sensorRadiusSquared, Team.NEUTRAL);

        for (int i = neutralBots.length; --i>=0; )
        {
            if (!contains(neutralBots[i].location))
            {
                addParts(neutralBots[i].location, currentLocation, neutralBots[i].type.partCost, true);
            }
        }
    }
}
