package team037.Enums;

import team037.Messages.*;

/**
 * Created by joshua on 1/5/16.
 */
public enum CommunicationType
{
    DEN, PARTS, ENEMY, OENEMY, CHANGEMISSION, MAP_BOUNDS,
    SARCHON, SENEMY, SZOMBIE, SDEN, SPARTS;

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
                return new BotInfoCommunication();
            //SimpleBotInfoCommunication
            case OENEMY:
            case SARCHON:
            case SENEMY:
            case SPARTS:
            case SZOMBIE:
            case SDEN:
                return new SimpleBotInfoCommunication();
            //PartsCommunication
            case PARTS:
                return new PartsCommunication();
            //MissionCommunication
            case CHANGEMISSION:
                return new MissionCommunication();
            //MapBoundsCommunication
            case MAP_BOUNDS:
                return new MapBoundsCommunication();
        }

        return null;
    }
}
