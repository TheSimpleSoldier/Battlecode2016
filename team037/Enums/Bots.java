package team037.Enums;

/**
 * Created by joshua on 1/5/16.
 * One of each type of unit
 */
public enum Bots
{
    BASEARCHON, BASEGAURD, BASESCOUT, BASESOLDIER, BASETTM, BASETURRET, BASEVIPER, ALPHAARCHON;

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
        }
        return -1;
    }
}
