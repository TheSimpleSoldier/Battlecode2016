package team037.Enums;

/**
 * Created by joshua on 1/5/16.
 *
 * Den is for den locations
 * Parts is for part location and quantities
 * Enemy is for full enemy info
 * Enemyl is for enemy info minus type
 * Mission is for mission changes
 */
public enum CommunicationType
{
    DEN, PARTS, ENEMY, OENEMY, CHANGEMISSION, INITIALMISSION, MAP_BOUNDS, MAP_DENS,
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
            case INITIALMISSION:
                return 5;
            case MAP_BOUNDS:
                return 6;
            case MAP_DENS:
                return 7;
            case SARCHON:
                return 8;
            case SENEMY:
                return 9;
            case SZOMBIE:
                return 10;
            case SDEN:
                return 11;
            case SPARTS:
                return 12;
        }
        return -1;
    }
}
