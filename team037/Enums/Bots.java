package team037.Enums;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Unit;
import team037.Units.*;

/**
 * Created by joshua on 1/5/16.
 * One of each type of unit
 */
public enum Bots
{
    BASEARCHON, BASEGAURD, BASESCOUT, BASESOLDIER, BASETTM, BASETURRET, BASEVIPER, ALPHAARCHON,
    SCOUTINGSCOUT, ATTACKINGSOLDIER;

    public static int toInt(Bots type)
    {
        switch(type)
        {
            case BASEARCHON:
                return 0;
            case BASEGAURD:
                return 1;
            case BASESCOUT:
                return 2;
            case BASESOLDIER:
                return 3;
            case BASETTM:
                return 4;
            case BASETURRET:
                return 5;
            case BASEVIPER:
                return 6;
            case ALPHAARCHON:
                return 7;
            case SCOUTINGSCOUT:
                return 8;
            case ATTACKINGSOLDIER:
                return 9;
        }
        return -1;
    }

    public static Bots fromInt(int value)
    {
        switch(value)
        {
            case 0:
                return BASEARCHON;
            case 1:
                return BASEGAURD;
            case 2:
                return BASESCOUT;
            case 3:
                return BASESOLDIER;
            case 4:
                return BASETTM;
            case 5:
                return BASETURRET;
            case 6:
                return BASEVIPER;
            case 7:
                return ALPHAARCHON;
            case 8:
                return SCOUTINGSCOUT;
            case 9:
                return ATTACKINGSOLDIER;
        }

        return null;
    }

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
            case SCOUTINGSCOUT:
                return RobotType.SCOUT;
            case BASESOLDIER:
            case ATTACKINGSOLDIER:
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

    public static Unit returnUnit(Bots bot, RobotController rc)
    {
        switch(bot)
        {
            case ALPHAARCHON:
                return new AlphaArchon(rc);
            case BASEARCHON:
                return new BaseArchon(rc);
            case BASEGAURD:
                return new BaseGaurd(rc);
            case BASESCOUT:
                return new BaseScout(rc);
            case SCOUTINGSCOUT:
                return new ScoutingScout(rc);
            case BASESOLDIER:
                return new BaseSoldier(rc);
            case ATTACKINGSOLDIER:
                return new AttackingSoldier(rc);
            case BASETTM:
                return new BaseTTM(rc);
            case BASETURRET:
                return new BaseTurret(rc);
            case BASEVIPER:
                return new BaseViper(rc);
        }

        return null;
    }
}
