package team037.Utilites;

import battlecode.common.RobotController;
import battlecode.common.ZombieCount;
import battlecode.common.ZombieSpawnSchedule;

public class ZombieTracker
{
    private static final int BIG_ZOMBIE_SCORE = 10;
    private static final int STANDARD_ZOMBIE_SCORE = 1;
    private static final int RANGED_ZOMBIE_SCORE = 5;
    private static final int FAST_ZOMBIE_SCORE = 5;
    private static final double[] multipliers = new double[]{1.00, 1.10, 1.20, 1.30, 1.50, 1.70, 2.00, 2.30, 2.60, 3.00};

    private static RobotController rc;
    private static int[] zombieRounds;
    private static ZombieCount[] zombieCounts;
    private static int currentSpawnRound;
    private static int nextSpawnRound;
    private static int index;
    private static ZombieSpawnSchedule zombieSpawnSchedule;

    public ZombieTracker(RobotController robotController)
    {
        rc = robotController;
        zombieSpawnSchedule = rc.getZombieSpawnSchedule();
        zombieRounds = zombieSpawnSchedule.getRounds();
        zombieCounts = zombieSpawnSchedule.getScheduleForRound(zombieRounds[0]);

        if (zombieRounds.length > 1)
        {
            currentSpawnRound = zombieRounds[0];
            nextSpawnRound = zombieRounds[1];
            index = 1;
        }
    }

    public int getNextZombieRound()
    {
        int currentRound = rc.getRoundNum();

        while (currentRound > currentSpawnRound)
        {
            currentSpawnRound = nextSpawnRound;
            index++;

            // if there are no more spawn rounds
            if (index >= zombieRounds.length)
            {
                return Integer.MAX_VALUE;
            }

            nextSpawnRound = zombieRounds[index];
        }

        return currentSpawnRound;
    }

    public int getCountStrength(ZombieCount[] zombieCounts)
    {
        int totalStrength = 0;

        for (int i = zombieCounts.length; --i>=0;)
        {
            switch (zombieCounts[i].getType())
            {
                case FASTZOMBIE:
                    totalStrength += FAST_ZOMBIE_SCORE * zombieCounts[i].getCount() * multipliers[Math.min(multipliers.length, index-1)];
                    break;
                case BIGZOMBIE:
                    totalStrength += BIG_ZOMBIE_SCORE * zombieCounts[i].getCount() * multipliers[Math.min(multipliers.length, index-1)];
                    break;
                case STANDARDZOMBIE:
                    totalStrength += STANDARD_ZOMBIE_SCORE * zombieCounts[i].getCount() * multipliers[Math.min(multipliers.length, index-1)];
                    break;
                case RANGEDZOMBIE:
                    totalStrength += RANGED_ZOMBIE_SCORE * zombieCounts[i].getCount() * multipliers[Math.min(multipliers.length, index-1)];
                    break;
            }
        }

        return totalStrength;
    }

    public int getNextZombieRoundStrength()
    {
        zombieCounts = zombieSpawnSchedule.getScheduleForRound(getNextZombieRound());

        return getCountStrength(zombieCounts);
    }

    /**
     * 0 => super easy zombie map
     * 1 => easy Zombie Map
     * 2 => medium map
     * 3 => super hard map
     *
     * @return
     */
    public int getZombieStrength()
    {
        int rounds = 0;

        if (zombieRounds.length <= 5)
        {

        }
        else
        {
            if (zombieRounds[0] < 50)
            {
                rounds = 2;
            }
            else if (zombieRounds[0] < 100)
            {
                rounds = 1;
            }

            if (zombieRounds[1] - zombieRounds[0] < 100 && zombieRounds[2] - zombieRounds[1] < 100)
            {
                rounds += 2;
            }
            else if (zombieRounds[1] - zombieRounds[0] < 100 || zombieRounds[2] - zombieRounds[1] < 100)
            {
                rounds++;
            }
        }

        int totalStrength = 0;
        int roundCount = 0;
        int strength = 0;

        // this loop needs to start at 0
        for (int i = 0; i < zombieRounds.length; i++)
        {
            if (zombieRounds[i] > 500)
            {
                break;
            }

            totalStrength += getCountStrength(zombieSpawnSchedule.getScheduleForRound(zombieRounds[i]));
            roundCount++;
        }

        if (rounds > 4)
        {
            totalStrength /= roundCount;

            if (totalStrength > 20)
            {
                strength = 3;
            }
            else if (totalStrength > 10)
            {
                strength = 2;
            }
            else if (totalStrength > 5)
            {
                strength = 1;
            }
        }

        return (rounds + strength) / 2;
    }
}
