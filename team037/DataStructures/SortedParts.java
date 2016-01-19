package team037.DataStructures;

import battlecode.common.*;

public class SortedParts
{
    MapLocation[] locs;
    double[] score;

    int count;
    int size;

    /**
     * This class is for use of archons and it gives the Archon
     * the best location to go to based on a ratio of parts to distance
     */
    public SortedParts()
    {
        size = 10;
        locs = new MapLocation[size];
        score = new double[size];
        count = 0;
    }

    /**
     * This method gets the location with the highest score
     *
     * @return
     */
    public MapLocation getBestSpot(MapLocation current)
    {
        if (count == 0)
            return null;

        double highestScore = 0;
        MapLocation best = null;

        for (int i = size; --i>=0; )
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

        double newValue = value;

        // b/c neutral parts don't have a build time
        // they are more valuable than parts
        if (neutral)
            newValue *= 2;


        count++;

        if (count >= locs.length * 0.7)
        {
            IncreaseSize();
        }

        int index = m.hashCode();

        while (locs[index % size] != null) index++;

        locs[index % size] = m;
        score[index % size] = newValue;
    }

    public void IncreaseSize()
    {
        size = locs.length * 2;
        MapLocation[] newLocs = new MapLocation[size];
        double[] newScores = new double[size];

        for (int i = locs.length; --i>=0; )
        {
            if (locs[i] != null)
            {
                int index = locs[i].hashCode();

                while (newLocs[index % size] != null) index++;

                newLocs[index % size] = locs[i];
                newScores[index % size] = score[i];
            }
        }

        locs = newLocs;
        score = newScores;
    }

    /**
     * This method returns the index of a mapLocation stored in our array
     *
     * @param m
     * @return
     */
    public int getIndexOfMapLocation(MapLocation m)
    {
        int index = m.hashCode();

        while (locs[index % size] != null)
        {
            if (locs[index % size].equals(m))
            {
                return index % size;
            }
            index++;
        }

        for (int i = locs.length; --i>=0;)
        {
            if (locs[i] != null && locs[i].equals(m))
            {
                return i;
            }
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

        locs[index] = null;
        score[index] = 0;
        count--;

        int nextIndex = index+1;
        int currentEmpty = index;

        // need to re-order hash table
        while (locs[nextIndex % size] != null)
        {
            int hashValue = locs[nextIndex % size].hashCode() % size;
            if (hashValue <= currentEmpty)
            {
                locs[currentEmpty] = locs[nextIndex % size];
                score[currentEmpty] = score[nextIndex % size];
                locs[nextIndex % size] = null;
                score[nextIndex % size] = 0;
                currentEmpty = nextIndex % size;
            }
            nextIndex++;
        }
    }

    /**
     * This method determines if a location has already been stored in our parts sorter
     *
     * @param m
     * @return
     */
    public boolean contains(MapLocation m)
    {
        return getIndexOfMapLocation(m) != -1;
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

        MapLocation[] parts = rc.sensePartLocations(sensorRadiusSquared);

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
            if (neutralBots[i].type == RobotType.ARCHON)
            {
                addParts(neutralBots[i].location, 1000, true);
            }
            else
            {
                addParts(neutralBots[i].location, neutralBots[i].type.partCost, true);
            }
        }
    }
}
