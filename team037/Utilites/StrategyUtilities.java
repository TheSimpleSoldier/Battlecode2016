package team037.Utilites;

import battlecode.common.MapLocation;

/**
 * Utilities used to determine what strategy our team should run.
 * Created by joshua on 1/18/16.
 */
public class StrategyUtilities
{
    public static double averageDistToEnemyArchons(MapLocation[] us, MapLocation[] them)
    {
        double average = 0;
        for(int k = 0; k < us.length; k++)
        {
            double total = 0;
            for(int a = 0; a < them.length; a++)
            {
                total += us[k].distanceSquaredTo(them[a]);
            }
            average += total / them.length;
        }
        return average / us.length;
    }

    public static int[] estimatedSize(MapLocation[] us, MapLocation[] them)
    {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for(int k = us.length; --k >= 0;)
        {
            if(us[k].x < minX)
            {
                minX = us[k].x;
            }
            if(us[k].x > maxX)
            {
                maxX = us[k].x;
            }
            if(us[k].y < minY)
            {
                minY = us[k].y;
            }
            if(us[k].y > maxY)
            {
                maxY = us[k].y;
            }

            if(them[k].x < minX)
            {
                minX = them[k].x;
            }
            if(them[k].x > maxX)
            {
                maxX = them[k].x;
            }
            if(them[k].y < minY)
            {
                minY = them[k].y;
            }
            if(them[k].y > maxY)
            {
                maxY = them[k].y;
            }
        }
        return new int[]{maxX - minX, maxY - minY};
    }

    public static boolean enemyBetweenBuddies(MapLocation[] us, MapLocation[] them)
    {
        for(int k = 0; k < us.length; k++)
        {
            for(int a = k + 1; a < us.length; a++)
            {
                int minX = Math.min(us[k].x, us[a].x);
                int minY = Math.min(us[k].y, us[a].y);
                int maxX = Math.max(us[k].x, us[a].x);
                int maxY = Math.max(us[k].y, us[a].y);
                for(int i = 0; i < them.length; i++)
                {
                    if(them[k].x >= minX && them[k].x <= maxX && them[k].y >= minY && them[k].y <= maxY)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double averageRoundsBetweenSpawns(int[] schedule)
    {
        return (schedule[schedule.length - 1] - schedule[0]) / schedule.length;
    }
}
