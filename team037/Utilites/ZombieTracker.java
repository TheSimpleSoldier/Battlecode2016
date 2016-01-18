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

    public int getNextZombieRoundStrength()
    {
        int currentRound = rc.getRoundNum();

        int totalStrength = 0;

        zombieCounts = zombieSpawnSchedule.getScheduleForRound(getNextZombieRound());

        for (int i = zombieCounts.length; --i>=0;)
        {
            switch (zombieCounts[i].getType())
            {
                case FASTZOMBIE:
                    totalStrength += FAST_ZOMBIE_SCORE * zombieCounts[i].getCount();
                    break;
                case BIGZOMBIE:
                    totalStrength += BIG_ZOMBIE_SCORE * zombieCounts[i].getCount();
                    break;
                case STANDARDZOMBIE:
                    totalStrength += STANDARD_ZOMBIE_SCORE * zombieCounts[i].getCount();
                    break;
                case RANGEDZOMBIE:
                    totalStrength += RANGED_ZOMBIE_SCORE * zombieCounts[i].getCount();
                    break;
            }
        }

        return totalStrength;
    }
}
