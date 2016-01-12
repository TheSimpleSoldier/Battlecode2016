package team037.DataStructures;

import battlecode.common.*;

public class SortedParts
{
    MapLocation[] locs;
    MapLocation[] deleted;
    double[] score;

    int start;
    int end;

    int deleteLength;

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
        deleted = new MapLocation[2000];
        deleteLength = 0;
    }

    /**
     * This method gets the location with the highest score
     *
     * @return
     */
    public MapLocation getBestSpot(MapLocation current)
    {
        if (end == start)
            return null;

        double highestScore = 0;
        MapLocation best = null;

        for (int i = start; i < end; i++)
        {
            if (locs[i] != null)
            {
                double value = (score[i] / current.distanceSquaredTo(locs[i]) + 1);

                if (value > highestScore)
                {
                    best = locs[i];
                    highestScore = value;
                }
            }
        }

        return best;
    }

    /**
     * This method adds a part location to the array
     *
     * @param m
     * @param value
     * @param neutral
     */
    public void addParts(MapLocation m, int value, boolean neutral)
    {
        if (m == null)
            return;

        if (deleted(m))
            return;

        double newValue = value;

        // b/c neutral parts don't have a build time
        // they are more valuable than parts
        if (neutral)
            newValue *= 2;


        locs[end] = m;
        score[end] = newValue;
        end++;

        //sort(newValue, m);
    }

    public boolean deleted(MapLocation m)
    {
        for (int i = deleteLength; --i>=0; )
        {
            if (deleted[i] != null && deleted[i].equals(m))
                return true;
        }
        return false;
    }

    /**
     * This method returns the index of a mapLocation stored in our array
     *
     * @param m
     * @return
     */
    public int getIndexOfMapLocation(MapLocation m)
    {
        for (int i = start; i < end; i++)
        {
            if (locs[i] != null && locs[i].equals(m))
                return i;
        }
        return -1;
    }

    /**
     * This method adds a part to the correct location in an array
     */
    public void remove(int index)
    {
        if (index == -1)
            return;

        deleted[deleteLength] = locs[index];
        deleteLength++;

        locs[index] = null;
        score[index] = 0;

//        if (index == start)
//        {
//            score[start] = 0;
//            locs[start] = null;
//            start++;
//        }
//        else if (index == end)
//        {
//            score[end-1] = 0;
//            locs[end-1] = null;
//            end--;
//        }
//        // if we are ahead of center push everything forward
//        else if (start + (end - start) / 2 <= index)
//        {
//            int iterator = index;
//            while (iterator < end)
//            {
//                locs[iterator] = locs[iterator+1];
//                score[iterator] = score[iterator+1];
//                iterator++;
//            }
//            locs[end-1] = null;
//            score[end-1] = 0;
//            end--;
//        }
//        // push everything ahead
//        else
//        {
//            int iterator = index;
//            while (iterator > start)
//            {
//                locs[iterator] = locs[iterator-1];
//                score[iterator] = score[iterator-1];
//                iterator--;
//            }
//            locs[start] = null;
//            score[start] = 0;
//            start++;
//        }
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
        RobotType type = rc.getType();
        int sensorRadiusSquared = type.sensorRadiusSquared;

        MapLocation[] parts = rc.sensePartLocations(rc.getType().sensorRadiusSquared);

        for (int i = parts.length; --i>=0; )
        {
            if (!contains(parts[i]))
            {
                addParts(parts[i], (int)rc.senseParts(parts[i]), false);
            }
        }

        RobotInfo[] neutralBots = rc.senseNearbyRobots(sensorRadiusSquared, Team.NEUTRAL);

        for (int i = neutralBots.length; --i>=0; )
        {
            if (!contains(neutralBots[i].location))
            {
                addParts(neutralBots[i].location, neutralBots[i].type.partCost, true);
            }
        }
    }
}
