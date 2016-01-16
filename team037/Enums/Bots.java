package team037.Enums;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Unit;
import team037.Units.*;
import team037.Units.BaseUnits.*;
import team037.Units.CastleUnits.CastleSoldier;
import team037.Units.DenKillers.DenKillerGuard;
import team037.Units.DenKillers.DenKillerSoldier;
import team037.Units.Rushers.RushingSoldier;
import team037.Units.Rushers.RushingViper;
import team037.Units.ScoutBomb.ScoutBombScout;
import team037.Units.Scouts.HerdingScout;
import team037.Units.Scouts.PatrolScout;
import team037.Units.Scouts.RegionScout;
import team037.Units.Scouts.ScoutingScout;
import team037.Units.TurtleUnits.TurtleArchon;
import team037.Units.TurtleUnits.TurtleGuard;
import team037.Units.TurtleUnits.TurtleSoldier;
import team037.Units.TurtleUnits.TurtleTurret;

/**
 * Created by joshua on 1/5/16.
 * One of each type of unit
 */
public enum Bots
{
    BASEARCHON, BASEGAURD, BASESCOUT, BASESOLDIER, BASETURRET, BASEVIPER, ALPHAARCHON,
    SCOUTINGSCOUT, PATROLSCOUT, DENKILLERSOLDIER, DENKILLERGUARD, RUSHINGSOLDIER, RUSHINGVIPER,
    REGIONSCOUT, HERDINGSCOUT, CASTLESOLDIER, TURTLEARCHON, TURTLESOLDIER, TURTLETURRET, TURTLEGUARD, SCOUTBOMBARCHON, SCOUTBOMBSCOUT;

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
            case BASETURRET:
                return 4;
            case BASEVIPER:
                return 5;
            case ALPHAARCHON:
                return 6;
            case SCOUTINGSCOUT:
                return 7;
            case PATROLSCOUT:
                return 8;
            case DENKILLERSOLDIER:
                return 9;
            case DENKILLERGUARD:
                return 10;
            case RUSHINGSOLDIER:
                return 11;
            case RUSHINGVIPER:
                return 12;
            case REGIONSCOUT:
                return 13;
            case HERDINGSCOUT:
                return 14;
            case CASTLESOLDIER:
                return 15;
            case TURTLEARCHON:
                return 16;
            case TURTLEGUARD:
                return 17;
            case TURTLESOLDIER:
                return 18;
            case TURTLETURRET:
                return 19;
            case SCOUTBOMBSCOUT:
                return 20;
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
                return BASETURRET;
            case 5:
                return BASEVIPER;
            case 6:
                return ALPHAARCHON;
            case 7:
                return SCOUTINGSCOUT;
            case 8:
                return PATROLSCOUT;
            case 9:
                return DENKILLERSOLDIER;
            case 10:
                return DENKILLERGUARD;
            case 11:
                return RUSHINGSOLDIER;
            case 12:
                return RUSHINGVIPER;
            case 13:
                return REGIONSCOUT;
            case 14:
                return HERDINGSCOUT;
            case 15:
                return CASTLESOLDIER;
            case 16:
                return TURTLEARCHON;
            case 17:
                return TURTLEGUARD;
            case 18:
                return TURTLESOLDIER;
            case 19:
                return TURTLETURRET;
            case 20:
                return SCOUTBOMBSCOUT;
        }

        return null;
    }

    public static RobotType typeFromBot(Bots bot)
    {
        switch(bot)
        {
            case TURTLEARCHON:
            case ALPHAARCHON:
            case BASEARCHON:
                return RobotType.ARCHON;
            case TURTLEGUARD:
            case DENKILLERGUARD:
            case BASEGAURD:
                return RobotType.GUARD;
            case SCOUTBOMBSCOUT:
            case REGIONSCOUT:
            case PATROLSCOUT:
            case BASESCOUT:
            case SCOUTINGSCOUT:
            case HERDINGSCOUT:
                return RobotType.SCOUT;
            case TURTLESOLDIER:
            case CASTLESOLDIER:
            case RUSHINGSOLDIER:
            case DENKILLERSOLDIER:
            case BASESOLDIER:
                return RobotType.SOLDIER;
            case TURTLETURRET:
            case BASETURRET:
                return RobotType.TURRET;
            case RUSHINGVIPER:
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
            case RUSHINGVIPER:
                return new RushingViper(rc);
            case RUSHINGSOLDIER:
                return new RushingSoldier(rc);
            case REGIONSCOUT:
                return new RegionScout(rc);
            case HERDINGSCOUT:
                return new HerdingScout(rc);
            case CASTLESOLDIER:
                return new CastleSoldier(rc);
            case TURTLEARCHON:
                return new TurtleArchon(rc);
            case TURTLEGUARD:
                return new TurtleGuard(rc);
            case TURTLESOLDIER:
                return new TurtleSoldier(rc);
            case TURTLETURRET:
                return new TurtleTurret(rc);
            case SCOUTBOMBSCOUT:
                return new ScoutBombScout(rc);
        }

        return null;
    }
}
