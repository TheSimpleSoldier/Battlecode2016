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

    public ZombieTracker(RobotController robotController)
    {
        rc = robotController;
        ZombieSpawnSchedule zombieSpawnSchedule = rc.getZombieSpawnSchedule();
        zombieRounds = zombieSpawnSchedule.getRounds();
        zombieCounts = zombieSpawnSchedule.getScheduleForRound(zombieRounds[0]);
    }

    public int getNextZombieRound()
    {

    }
}
