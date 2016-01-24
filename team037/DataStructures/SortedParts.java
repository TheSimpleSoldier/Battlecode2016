package team037.DataStructures;

import battlecode.common.*;
import team037.Utilites.MapUtils;
import team037.Utilites.RubbleUtilities;

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

    public MapLocation getMassivConcentration()
    {
        for (int i = locs.length; --i>=0; )
        {
            if (locs[i] != null)
            {
                MapLocation[] nearBySquares = MapLocation.getAllMapLocationsWithinRadiusSq(locs[i], 25);
                int count = 0;

                for (int j = nearBySquares.length; --j>=0; )
                {
                    if (contains(nearBySquares[j]))
                    {
                        count++;
                    }
                }

                // there are 10 or more squares with parts/neutrals in this area
                if (count > 10)
                {
                    return locs[i];
                }
            }
        }

        return null;
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
     * This method returns the bets location within sight range of the archon
     *
     * @return
     */
    public MapLocation getBestSpotInSightRange(MapLocation current)
    {
       if (count == 0)
           return null;

        double highestScore = 0;
        MapLocation best = null;

        for (int i = size; --i>=0; )
        {
            if (locs[i] != null)
            {
                int dist = current.distanceSquaredTo(locs[i]) + 1;

                if (dist <= RobotType.ARCHON.sensorRadiusSquared)
                {
                    double value = (score[i] / dist);

                    if (value > highestScore)
                    {
                        best = locs[i];
                        highestScore = value;
                    }
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

        if (contains(m)) return;

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

                index = Math.abs(index);

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
        if (m == null) return -1;


        int index = m.hashCode();

        index = Math.abs(index);

        while (locs[index % size] != null)
        {
            if (locs[index % size].equals(m))
            {
                return index % size;
            }
            index++;
        }

        return -1;
    }

    /**
     * This method will iterate over the entire array and do a hard remove
     *
     * @param m
     */
    public void hardRemove(MapLocation m)
    {
        System.out.println("Hard remove");

        for (int i = locs.length; --i>=0; )
        {
            if (locs[i] != null && locs[i].equals(m))
            {
                remove(i);
            }
        }
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

        // need to re-order hash table
        while (locs[nextIndex % size] != null)
        {
            int currentIndex = nextIndex % size;
            MapLocation temp = locs[currentIndex];
            double tempScore = score[currentIndex];

            locs[currentIndex] = null;
            score[currentIndex] = 0;

            addParts(temp, (int) tempScore, false);
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
        return getIndexOfMapLocation(m) > -1;
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
            double rubble = rc.senseRubble(parts[i]);
            int partVal = (int) rc.senseParts(parts[i]);

            if (rubble > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            {
                int turns = RubbleUtilities.calculateClearActionsToPassableButSlow(rubble);
                if (turns > 0)
                {
                    partVal /= turns;
                    if (partVal <= 0) partVal = 1;
                }

                addParts(parts[i], partVal, false);
            }
            else
            {
                addParts(parts[i], partVal, false);
            }
        }

        RobotInfo[] neutralBots = rc.senseNearbyRobots(sensorRadiusSquared, Team.NEUTRAL);

        for (int i = neutralBots.length; --i>=0; )
        {
            if (neutralBots[i].type == RobotType.ARCHON)
            {
                addParts(neutralBots[i].location, Integer.MAX_VALUE, true);
            }
            else
            {
                addParts(neutralBots[i].location, neutralBots[i].type.partCost, true);
            }
        }
    }

    /**
     * get Neutral Archon spot
     *
     * @return
     */
    public MapLocation getNeutralArchon()
    {
        for (int i = locs.length; --i>=0; )
        {
            if (score[i] > 99999)
            {
                return locs[i];
            }
        }

        return null;
    }
}
