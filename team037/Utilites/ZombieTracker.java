package team037.Utilites;

import battlecode.common.RobotController;
import battlecode.common.ZombieCount;
import battlecode.common.ZombieSpawnSchedule;

public class ZombieTracker
{
    private static RobotController rc;
    private static int[] zombieRounds;
    private static ZombieCount[] zombieCounts;
    private static int currentSpawnRound;
    private static int nextSpawnRound;
    private static int index;

    public ZombieTracker(RobotController robotController)
    {
        rc = robotController;
        ZombieSpawnSchedule zombieSpawnSchedule = rc.getZombieSpawnSchedule();
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
}
