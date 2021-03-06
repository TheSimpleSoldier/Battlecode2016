package team037.Enums;

import team037.Messages.*;

/**
 * Enum used to convert raw messages into useful data.
 */
public enum CommunicationType
{
    DEN,
    PARTS,
    ENEMY,
    OENEMY,
    CHANGEMISSION,
    MAP_BOUNDS,
    SARCHON,
    SENEMY,
    SZOMBIE,
    SDEN,
    SPARTS,
    TURRET_SUPPORT,
    NEUTRAL,
    SKILLED_DEN,
    DEAD_DEN,
    EXPLORE_EDGE,
    ATTACK,
    RALLY_POINT,
    RUBBLE,
    ARCHON_DISTRESS,
    GOING_AFTER_PARTS,
    TURRET_UNDER_ATTACK;

    public static int toInt(CommunicationType type)
    {
        switch(type)
        {
            case DEN:
                return 0;
            case PARTS:
                return 1;
            case ENEMY:
                return 2;
            case OENEMY:
                return 3;
            case CHANGEMISSION:
                return 4;
            case MAP_BOUNDS:
                return 5;
            case SARCHON:
                return 6;
            case SENEMY:
                return 7;
            case SZOMBIE:
                return 8;
            case SDEN:
                return 9;
            case SPARTS:
                return 10;
            case TURRET_SUPPORT:
                return 11;
            case NEUTRAL:
                return 12;
            case SKILLED_DEN:
                return 13;
            case DEAD_DEN:
                return 14;
            case EXPLORE_EDGE:
                return 15;
            case ATTACK:
                return 16;
            case RALLY_POINT:
                return 17;
            case RUBBLE:
                return 18;
            case ARCHON_DISTRESS:
                return 19;
            case GOING_AFTER_PARTS:
                return 20;
            case TURRET_UNDER_ATTACK:
                return 21;
        }
        return -1;
    }

    public static CommunicationType fromInt(int val)
    {
        switch(val)
        {
            case 0:
                return DEN;
            case 1:
                return PARTS;
            case 2:
                return ENEMY;
            case 3:
                return OENEMY;
            case 4:
                return CHANGEMISSION;
            case 5:
                return MAP_BOUNDS;
            case 6:
                return SARCHON;
            case 7:
                return SENEMY;
            case 8:
                return SZOMBIE;
            case 9:
                return SDEN;
            case 10:
                return SPARTS;
            case 11:
                return TURRET_SUPPORT;
            case 12:
                return NEUTRAL;
            case 13:
                return SKILLED_DEN;
            case 14:
                return DEAD_DEN;
            case 15:
                return EXPLORE_EDGE;
            case 16:
                return ATTACK;
            case 17:
                return RALLY_POINT;
            case 18:
                return RUBBLE;
            case 19:
                return ARCHON_DISTRESS;
            case 20:
                return GOING_AFTER_PARTS;
            case 21:
                return TURRET_UNDER_ATTACK;
        }
        return null;
    }

    public static Communication getCommunication(CommunicationType opcode)
    {
        switch(opcode)
        {
            //BotInfoCommunication
            case DEN:
            case ENEMY:
            case ARCHON_DISTRESS:
            case GOING_AFTER_PARTS:
            case TURRET_UNDER_ATTACK:
                return new BotInfoCommunication();
            //SimpleBotInfoCommunication
            case OENEMY:
            case SKILLED_DEN:
            case DEAD_DEN:
            case SARCHON:
            case SENEMY:
            case SPARTS:
            case SZOMBIE:
            case SDEN:
                return new SimpleBotInfoCommunication();
            //PartsCommunication
            case NEUTRAL:
            case PARTS:
                return new PartsCommunication();
            //MissionCommunication
            case CHANGEMISSION:
                return new MissionCommunication();
            //MapBoundsCommunication
            case MAP_BOUNDS:
                return new MapBoundsCommunication();
            case TURRET_SUPPORT:
                return new TurretSupportCommunication();
            case EXPLORE_EDGE:
                return new ExploringMapEdge();
            case ATTACK:
            case RALLY_POINT:
                return new AttackCommunication();
            case RUBBLE:
                return new RubbleCommunication();
        }

        return null;
    }
}
