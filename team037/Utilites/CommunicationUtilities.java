package team037.Utilites;

import battlecode.common.MapLocation;
import battlecode.common.Signal;
import team037.DataStructures.CommTypeToSpacing;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.SimpleBotInfoCommunication;

/**
 * Created by joshua on 1/5/16.
 * Translates between messages and communications
 */
public class CommunicationUtilities
{
    public static int opcodeSize = 5;
    public static int valSize = 15;
    public static int typeSize = 4;
    public static int botSize = 8;
    public static int locationSize = 15;
    public static int mapSpanSize = 7;
    public static int mapIndicatorSize = 2;
    public static int teamSize = 2;

    public static Communication readCommunication(Signal signal)
    {
        int[] message = signal.getMessage();
        CommunicationType opcode = CommunicationType.fromInt(
                extractVal(message[0], 1, CommTypeToSpacing.opcodeSize));
        Communication communication = CommunicationType.getCommunication(opcode);
        communication.setValues(unpack(message, communication.getLengths()));
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
        Communication communication = new SimpleBotInfoCommunication();

        MapLocation loc = signal.getLocation();
        communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.OENEMY),
                signal.getRobotID(), loc.x, loc.y});
        communication.opcode = CommunicationType.OENEMY;

        return communication;
    }

    public static Communication readSimpleCommunication(Signal signal, int round)
    {
        CommunicationType opcode = null;
        switch(round % 10)
        {
            case 0:
            case 1:
                opcode = CommunicationType.SARCHON;
                break;
            case 2:
            case 3:
                opcode = CommunicationType.SENEMY;
                break;
            case 4:
            case 5:
                opcode = CommunicationType.SZOMBIE;
                break;
            case 6:
            case 7:
                opcode = CommunicationType.SDEN;
                break;
            case 8:
            case 9:
                opcode = CommunicationType.SPARTS;
                break;
        }

        MapLocation loc = signal.getLocation();
        Communication communication = new SimpleBotInfoCommunication();
        communication.setValues(new int[]{CommunicationType.toInt(opcode),
        signal.getRobotID(), loc.x, loc.y});

        return communication;
    }

    public static int[] createCommunication(Communication communication)
    {
        return pack(communication.getValues(), communication.getLengths());
    }

    private static int[] pack(int[] values, int[] lengths)
    {
        int[] message = new int[2];
        String current = "";
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
