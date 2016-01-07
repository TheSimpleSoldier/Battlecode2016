package team037.Utilites;

import battlecode.common.MapLocation;
import battlecode.common.Signal;
import team037.DataStructures.CommTypeToSpacing;
import team037.DataStructures.Communication;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;

/**
 * Created by joshua on 1/5/16.
 * Translates between messages and communications
 */
public class CommunicationUtilities
{
    public static Communication readCommunication(Signal signal)
    {
        Communication communication = new Communication();
        int[] message = signal.getMessage();
        communication.opcode = CommunicationType.fromInt(extractVal(message[0], 1, CommTypeToSpacing.opcodeSize));
        switch(communication.opcode)
        {
            //i-format
            case DEN:
            case PARTS:
            case OENEMY:
            case SARCHON:
            case SENEMY:
            case SZOMBIE:
            case SDEN:
            case SPARTS:
                int[] values = unpack(message, CommTypeToSpacing.I_FORMAT_SPACING);
                communication.val1 = values[0];
                communication.loc1X = values[1];
                communication.loc1Y = values[2];
                break;
            //bi-format
            case ENEMY:
                values = unpack(message, CommTypeToSpacing.BI_FORMAT_SPACING);
                communication.val1 = values[0];
                communication.rType1 = Utilities.typeFromInt(values[1]);
                communication.loc1X = values[2];
                communication.loc1Y = values[3];
                break;
            //cm-format
            case CHANGEMISSION:
                values = unpack(message, CommTypeToSpacing.CM_FORMAT_SPACING);
                communication.val1 = values[0];
                communication.bType1 = Bots.fromInt(values[1]);
                communication.bType2 = Bots.fromInt(values[2]);
                break;
            //im-format
            case INITIALMISSION:
                values = unpack(message, CommTypeToSpacing.IM_FORMAT_SPACING);
                communication.val1 = values[0];
                communication.bType1 = Bots.fromInt(values[1]);
                break;
            //mk-format
            case MAP_BOUNDS:
                values = unpack(message, CommTypeToSpacing.MK_FORMAT_SPACING);
                communication.val1 = values[0];
                communication.loc1X = values[1];
                communication.val2 = values[2];
                communication.val3 = values[3];
                communication.loc1Y = values[4];
                communication.val4 = values[5];
        }

        return communication;
    }

    private static int[] unpack(int[] message, int[] lengths)
    {
        int[] toReturn = new int[lengths.length];
        int current = message[0];
        int loc = 5;
        for(int k = 0; k < lengths.length; k++)
        {
            if(loc + lengths[k] > 32)
            {
                current = message[1];
                loc = 0;
            }

            toReturn[k] = extractVal(current, loc, lengths[k]);
            loc += lengths[k];
        }

        return toReturn;
    }

    private static int extractVal(int val, int start, int length)
    {
        val = val << (start);
        return val >>> (32 - length);
    }

    public static Communication readEnemyCommunication(Signal signal)
    {
        Communication communication = new Communication();

        communication.opcode = CommunicationType.OENEMY;
        communication.val1 = signal.getRobotID();
        MapLocation loc = signal.getLocation();
        communication.loc1X = loc.x;
        communication.loc1Y = loc.y;

        return communication;
    }

    public static Communication readSimpleCommunication(Signal signal, int round)
    {
        Communication communication = new Communication();
        communication.val1 = signal.getRobotID();
        MapLocation loc = signal.getLocation();
        communication.loc1X = loc.x;
        communication.loc1Y = loc.y;

        switch(round % 10)
        {
            case 0:
            case 1:
                communication.opcode = CommunicationType.SARCHON;
                break;
            case 2:
            case 3:
                communication.opcode = CommunicationType.SENEMY;
                break;
            case 4:
            case 5:
                communication.opcode = CommunicationType.SZOMBIE;
                break;
            case 6:
            case 7:
                communication.opcode = CommunicationType.SDEN;
                break;
            case 8:
            case 9:
                communication.opcode = CommunicationType.SPARTS;
                break;
        }

        return communication;
    }

    public static int[] createCommunication(Communication communication)
    {
        int[] message = null;

        switch(communication.opcode)
        {
            //i-format
            case DEN:
            case PARTS:
            case OENEMY:
            case SARCHON:
            case SENEMY:
            case SZOMBIE:
            case SDEN:
            case SPARTS:
                int[] values = {
                communication.val1, communication.loc1X, communication.loc1Y};
                message = pack(communication.opcode, values, CommTypeToSpacing.I_FORMAT_SPACING);
                break;
            //bi-format
            case ENEMY:
                int[] values2 = {
                communication.val1, Utilities.intFromType(communication.rType1),
                communication.loc1X, communication.loc1Y};
                message = pack(communication.opcode, values2, CommTypeToSpacing.BI_FORMAT_SPACING);
                break;
            //cm-format
            case CHANGEMISSION:
                int[] values3 = {
                communication.val1, Bots.toInt(communication.bType1),
                Bots.toInt(communication.bType2)};
                message = pack(communication.opcode, values3, CommTypeToSpacing.CM_FORMAT_SPACING);
                break;
            //im-format
            case INITIALMISSION:
                int[] values4 = {
                communication.val1, Bots.toInt(communication.bType1)};
                message = pack(communication.opcode, values4, CommTypeToSpacing.IM_FORMAT_SPACING);
                break;
            //mk-format
            case MAP_BOUNDS:
                int[] values5 = {
                communication.val1, communication.loc1X, communication.val2,
                communication.val3, communication.loc1Y, communication.val4};
                message = pack(communication.opcode, values5, CommTypeToSpacing.MK_FORMAT_SPACING);
        }

        return message;
    }

    private static int[] pack(CommunicationType opcode, int[] values, int[] lengths)
    {
        int[] message = new int[2];
        String current = "";
        current += createVal(CommunicationType.toInt(opcode), CommTypeToSpacing.opcodeSize);
        for(int k = 0; k < values.length; k++)
        {
            if(current.length() + lengths[k] > 31)
            {
                String buffer = new String(new char[31 - current.length()]).replace('\0', '0');
                current += buffer;
                message[0] = Integer.parseInt(current, 2);
                current = "";
            }

            current += createVal(values[k], lengths[k]);
        }
        String buffer = new String(new char[31 - current.length()]).replace('\0', '0');
        current += buffer;
        if(message[0] == 0)
        {
            message[0] = Integer.parseInt(current + buffer, 2);
        }
        else
        {
            message[1] = Integer.parseInt(current + buffer, 2);
        }

        return message;
    }

    private static String createVal(int val, int length)
    {
        String str = Integer.toBinaryString(val);
        String buffer = new String(new char[length]).replace('\0', '0');
        return (buffer + str).substring(str.length());
    }

    public static boolean shouldCommunicateSimple(Communication communication, int round)
    {
        if(communication.opcode == CommunicationType.SARCHON)
        {
            if(round % 10 == 0)
            {
                return true;
            }
        }
        else if(communication.opcode == CommunicationType.SENEMY)
        {
            if(round % 10 == 2)
            {
                return true;
            }
        }
        else if(communication.opcode == CommunicationType.SZOMBIE)
        {
            if(round % 10 == 4)
            {
                return true;
            }
        }
        else if(communication.opcode == CommunicationType.SDEN)
        {
            if(round % 10 == 6)
            {
                return true;
            }
        }
        else if(communication.opcode == CommunicationType.SPARTS)
        {
            if(round % 10 == 8)
            {
                return true;
            }
        }

        return false;
    }
}
