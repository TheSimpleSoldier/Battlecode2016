package team037.Enums;

/**
 * Created by joshua on 1/5/16.
 * One of each type of unit
 */
public enum Bots
{
    BASEARCHON, BASEGAURD, BASESCOUT, BASESOLDIER, BASETTM, BASETURRET, BASEVIPER, ALPHAARCHON,
    SCOUTINGSCOUT;

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
        }

        return null;
    }
}
