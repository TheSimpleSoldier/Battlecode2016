package team037.Enums;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Unit;
import team037.Units.*;
import team037.Units.BaseUnits.*;
import team037.Units.CastleUnits.CastleSoldier;
import team037.Units.DenKillers.DenKillerGuard;
import team037.Units.DenKillers.DenKillerSoldier;
import team037.Units.PacMan.PacManArchon;
import team037.Units.PacMan.PacManScout;
import team037.Units.Rushers.RushingSoldier;
import team037.Units.Rushers.RushingViper;
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
    BASEARCHON, BASEGAURD, BASESCOUT, BASESOLDIER, BASETTM, BASETURRET, BASEVIPER, ALPHAARCHON,
    SCOUTINGSCOUT, PATROLSCOUT, DENKILLERSOLDIER, DENKILLERGUARD, RUSHINGSOLDIER, RUSHINGVIPER,
    REGIONSCOUT, HERDINGSCOUT, CASTLESOLDIER, TURTLEARCHON, TURTLESOLDIER, TURTLETURRET, TURTLEGUARD,
    PACMANARCHON, PACMANSCOUT;

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
            case RUSHINGSOLDIER:
                return 12;
            case RUSHINGVIPER:
                return 13;
            case REGIONSCOUT:
                return 14;
            case HERDINGSCOUT:
                return 15;
            case CASTLESOLDIER:
                return 16;
            case TURTLEARCHON:
                return 17;
            case TURTLEGUARD:
                return 18;
            case TURTLESOLDIER:
                return 19;
            case TURTLETURRET:
                return 20;
            case PACMANARCHON:
                return 21;
            case PACMANSCOUT:
                return 22;
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
                return RUSHINGSOLDIER;
            case 13:
                return RUSHINGVIPER;
            case 14:
                return REGIONSCOUT;
            case 15:
                return HERDINGSCOUT;
            case 16:
                return CASTLESOLDIER;
            case 17:
                return TURTLEARCHON;
            case 18:
                return TURTLEGUARD;
            case 19:
                return TURTLESOLDIER;
            case 20:
                return TURTLETURRET;
            case 21:
                return PACMANARCHON;
            case 22:
                return PACMANSCOUT;
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
            case PACMANARCHON:
                return RobotType.ARCHON;
            case TURTLEGUARD:
            case DENKILLERGUARD:
            case BASEGAURD:
                return RobotType.GUARD;
            case REGIONSCOUT:
            case PATROLSCOUT:
            case BASESCOUT:
            case SCOUTINGSCOUT:
            case HERDINGSCOUT:
            case PACMANSCOUT:
                return RobotType.SCOUT;
            case TURTLESOLDIER:
            case CASTLESOLDIER:
            case RUSHINGSOLDIER:
            case DENKILLERSOLDIER:
            case BASESOLDIER:
                return RobotType.SOLDIER;
            case BASETTM:
                return RobotType.TTM;
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
            case PACMANARCHON:
                return new PacManArchon(rc);
            case PACMANSCOUT:
                return new PacManScout(rc);
        }

        return null;
    }
}
