package team037.Utilites;

import battlecode.common.Signal;

/**
 * Created by joshua on 1/5/16.
 * Translates between messages and communications
 *
 * message formats:
 * i-type(info)
 * first int- opcode, id, type
 * second int- location x, location y
 *
 * c-type(change mission)
 * first int- opcode, id(0 if doesn't matter), bot, type
 * second int- new bot
 */
public class CommunicationUtilities
{
    int opcodeSize = 4;
    int locationSize = 16;
    int idSize = 16;
    int botSize = 8;
    int typeSize = 4;

    int locationoffset = 53;

    public static Communication readCommunication(Signal signal)
    {
        return null;
    }

    public static int[] createCommunication(Communication communication)
    {
        return null;
    }
}
