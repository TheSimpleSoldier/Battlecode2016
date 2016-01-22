package team037.Enums;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Unit;
import team037.Units.AlphaArchon;
import team037.Units.BaseUnits.*;
import team037.Units.CastleUnits.CastleSoldier;
import team037.Units.DenKillers.DenKillerGuard;
import team037.Units.DenKillers.DenKillerSoldier;
import team037.Units.PacMan.PacManArchon;
import team037.Units.PacMan.PacManScout;
import team037.Units.Rushers.*;
import team037.Units.ScoutBomb.ScoutBombGuard;
import team037.Units.ScoutBomb.ScoutBombScout;
import team037.Units.Scouts.HerdingScout;
import team037.Units.Scouts.PatrolScout;
import team037.Units.Scouts.RegionScout;
import team037.Units.Scouts.ScoutingScout;
import team037.Units.TurtleUnits.*;

/**
 * Created by joshua on 1/5/16.
 * One of each type of unit
 */
public enum Bots
{
    BASEARCHON,
    BASEGAURD,
    BASESCOUT,
    BASESOLDIER,
    BASETURRET,
    BASEVIPER,
    ALPHAARCHON,
    SCOUTINGSCOUT,
    PATROLSCOUT,
    DENKILLERSOLDIER,
    DENKILLERGUARD,
    RUSHINGSOLDIER,
    RUSHINGVIPER,
    REGIONSCOUT,
    HERDINGSCOUT,
    CASTLESOLDIER,
    TURTLEARCHON,
    TURTLESOLDIER,
    TURTLETURRET,
    TURTLEGUARD,
    TURTLESCOUT,
    PACMANARCHON,
    PACMANSCOUT,
    PACMANGUARD,
    SCOUTBOMBARCHON,
    RUSHGUARD,
    RUSHTURRET,
    SCOUTBOMBGUARD,
    SCOUTBOMBSCOUT,
    RUSHSCOUT,
    SUPERRUSHARCHON;


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
            case PACMANARCHON:
                return 20;
            case PACMANSCOUT:
                return 21;
            case PACMANGUARD:
                return 22;
            case TURTLESCOUT:
                return 23;
            case RUSHGUARD:
                return 24;
            case RUSHTURRET:
                return 25;
            case SCOUTBOMBGUARD:
                return 26;
            case SCOUTBOMBSCOUT:
                return 27;
            case RUSHSCOUT:
                return 28;
            case SUPERRUSHARCHON:
                return 29;
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
                return PACMANARCHON;
            case 21:
                return PACMANSCOUT;
            case 22:
                return PACMANGUARD;
            case 23:
                return TURTLESCOUT;
            case 24:
                return RUSHGUARD;
            case 25:
                return RUSHTURRET;
            case 26:
                return SCOUTBOMBGUARD;
            case 27:
                return SCOUTBOMBSCOUT;
            case 28:
                return RUSHSCOUT;
            case 29:
                return SUPERRUSHARCHON;
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
            case SUPERRUSHARCHON:
                return RobotType.ARCHON;
            case RUSHGUARD:
            case TURTLEGUARD:
            case DENKILLERGUARD:
            case BASEGAURD:
            case SCOUTBOMBGUARD:
            case PACMANGUARD:
                return RobotType.GUARD;
            case TURTLESCOUT:
            case SCOUTBOMBSCOUT:
            case REGIONSCOUT:
            case PATROLSCOUT:
            case BASESCOUT:
            case SCOUTINGSCOUT:
            case HERDINGSCOUT:
            case PACMANSCOUT:
            case RUSHSCOUT:
                return RobotType.SCOUT;
            case TURTLESOLDIER:
            case CASTLESOLDIER:
            case RUSHINGSOLDIER:
            case DENKILLERSOLDIER:
            case BASESOLDIER:
                return RobotType.SOLDIER;
            case RUSHTURRET:
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
            case TURTLESCOUT:
                return new TurtleScout(rc);
            case PACMANARCHON:
                return new PacManArchon(rc);
            case PACMANSCOUT:
                return new PacManScout(rc);
            case PACMANGUARD:
                return new PacManScout(rc);
            case RUSHGUARD:
                return new RushingGuard(rc);
            case RUSHTURRET:
                return new RushingTurret(rc);
            case SCOUTBOMBSCOUT:
                return new ScoutBombScout(rc);
            case SCOUTBOMBGUARD:
                return new ScoutBombGuard(rc);
            case RUSHSCOUT:
                return new RushingScout(rc);
        }

        return null;
    }
}
