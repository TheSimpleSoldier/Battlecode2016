package team037.Enums;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Unit;
import team037.Units.*;
import team037.Units.DenKillers.DenKillerGuard;
import team037.Units.DenKillers.DenKillerSoldier;

/**
 * Created by joshua on 1/5/16.
 * One of each type of unit
 */
public enum Bots
{
    BASEARCHON, BASEGAURD, BASESCOUT, BASESOLDIER, BASETTM, BASETURRET, BASEVIPER, ALPHAARCHON,
    SCOUTINGSCOUT, PATROLSCOUT, DENKILLERSOLDIER, DENKILLERGUARD, REGIONSCOUT, HERDINGSCOUT;

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
            case PATROLSCOUT:
                return 9;
            case DENKILLERSOLDIER:
                return 10;
            case DENKILLERGUARD:
                return 11;
            case REGIONSCOUT:
                return 12;
            case HERDINGSCOUT:
                return 13;
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
                return PATROLSCOUT;
            case 10:
                return DENKILLERSOLDIER;
            case 11:
                return DENKILLERGUARD;
            case 12:
                return REGIONSCOUT;
            case 13:
                return HERDINGSCOUT;
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
            case DENKILLERGUARD:
            case BASEGAURD:
                return RobotType.GUARD;
            case REGIONSCOUT:
            case PATROLSCOUT:
            case BASESCOUT:
            case SCOUTINGSCOUT:
            case HERDINGSCOUT:
                return RobotType.SCOUT;
            case DENKILLERSOLDIER:
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
            case BASETTM:
                return new BaseTTM(rc);
            case BASETURRET:
                return new BaseTurret(rc);
            case BASEVIPER:
                return new BaseViper(rc);
            case PATROLSCOUT:
                return new PatrolScout(rc);
            case DENKILLERSOLDIER:
                return new DenKillerSoldier(rc);
            case DENKILLERGUARD:
                return new DenKillerGuard(rc);
            case REGIONSCOUT:
                return new RegionScout(rc);
            case HERDINGSCOUT:
                return new HerdingScout(rc);
        }

        return null;
    }
}
