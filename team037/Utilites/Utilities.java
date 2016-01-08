package team037.Utilites;

import battlecode.common.RobotType;
import battlecode.common.Team;
import team037.Enums.Bots;

/**
 * Created by joshua on 1/5/16.
 */
public class Utilities
{
    public static RobotType typeFromBot(Bots bot)
    {
        switch(bot)
        {
            case ALPHAARCHON:
            case BASEARCHON:
                return RobotType.ARCHON;
            case BASEGAURD:
                return RobotType.GUARD;
            case BASESCOUT:
                return RobotType.SCOUT;
            case BASESOLDIER:
                return RobotType.SOLDIER;
            case BASETTM:
                return RobotType.TTM;
            case BASETURRET:
                return RobotType.TURRET;
            case BASEVIPER:
                return RobotType.VIPER;
        }
        return null;
    }

    public static RobotType typeFromInt(int value)
    {
        switch(value)
        {
            case 0:
                return RobotType.ARCHON;
            case 1:
                return RobotType.BIGZOMBIE;
            case 2:
                return RobotType.FASTZOMBIE;
            case 3:
                return RobotType.GUARD;
            case 4:
                return RobotType.RANGEDZOMBIE;
            case 5:
                return RobotType.SCOUT;
            case 6:
                return RobotType.SOLDIER;
            case 7:
                return RobotType.STANDARDZOMBIE;
            case 8:
                return RobotType.TTM;
            case 9:
                return RobotType.TURRET;
            case 10:
                return RobotType.VIPER;
            case 11:
                return RobotType.ZOMBIEDEN;
        }

        return null;
    }

    public static int intFromType(RobotType type)
    {
        switch(type)
        {
            case ARCHON:
                return 0;
            case BIGZOMBIE:
                return 1;
            case FASTZOMBIE:
                return 2;
            case GUARD:
                return 3;
            case RANGEDZOMBIE:
                return 4;
            case SCOUT:
                return 5;
            case SOLDIER:
                return 6;
            case STANDARDZOMBIE:
                return 7;
            case TTM:
                return 8;
            case TURRET:
                return 9;
            case VIPER:
                return 10;
            case ZOMBIEDEN:
                return 11;
        }

        return -1;
    }

    public static int intFromTeam(Team team)
    {
        switch(team)
        {
            case A:
                return 0;
            case B:
                return 1;
            case ZOMBIE:
                return 2;
        }

        return -1;
    }

    public static Team teamFromInt(int team)
    {
        switch(team)
        {
            case 0:
                return Team.A;
            case 1:
                return Team.B;
            case 2:
                return Team.ZOMBIE;
        }

        return null;
    }
}
