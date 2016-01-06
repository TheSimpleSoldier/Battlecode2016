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
    DEN, PARTS, ENEMY, ENEMYL, MISSION;

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
            case ENEMYL:
                return 3;
            case MISSION:
                return 4;
        }
        return -1;
    }
}
