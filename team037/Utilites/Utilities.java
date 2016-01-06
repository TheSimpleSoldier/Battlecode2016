package team037.Utilites;

import battlecode.common.RobotType;
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
}
