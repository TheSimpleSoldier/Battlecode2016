package team037.Utilites;

import battlecode.common.MapLocation;
import battlecode.common.Signal;
import scala.Int;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Messages.SimpleBotInfoCommunication;
import team037.Unit;

/**
 * Created by joshua on 1/5/16.
 * Translates between messages and communications
 */
public class CommunicationUtilities
{
    public static int opcodeSize = 6;
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
                extractVal(message[0], 1, opcodeSize));

        Communication communication = CommunicationType.getCommunication(opcode);
        communication.setValues(unpack(message, communication.getLengths()));
        communication.setSignalValues(signal.getID(), signal.getLocation(), signal.getTeam());

        return communication;
    }

    private static int[] unpack(int[] message, int[] lengths)
    {
        int[] toReturn = new int[lengths.length];
        int current = message[0];
        int loc = 1;
        for(int k = 0; k < lengths.length; k++)
        {
            if(loc + lengths[k] > 32)
            {
                int breakLength = lengths[k] - (32 - loc);
                int val1 = extractVal(message[0], loc, (32 - loc));
                int val2 = extractVal(message[1], 1, breakLength);
                val1 = val1 << breakLength;
                toReturn[k] = val1 | val2;
                current = message[1];
                loc = 1 + breakLength;
            }
            else
            {
                toReturn[k] = extractVal(current, loc, lengths[k]);
                loc += lengths[k];
            }
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

        MapLocation loc = signal.getLocation();

        if (Unit.mapKnowledge.denLocations.contains(loc))
        {
            opcode = CommunicationType.SKILLED_DEN;
        }
        else
        {
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
        }

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
        String first = "";
        String second = "";
        boolean firstOne = true;
        for(int k = 0; k < values.length; k++)
        {
            if(first.length() + lengths[k] > 31 && firstOne)
            {
                String str = createVal(values[k], lengths[k]);
                firstOne = false;
                second += str.substring(31 - first.length());
                first += str.substring(0, (31 - first.length()));
            }
            else if(firstOne)
            {
                first += createVal(values[k], lengths[k]);
            }
            else
            {
                second += createVal(values[k], lengths[k]);
            }
        }
        String buffer = new String(new char[31 - first.length()]).replace('\0', '0');
        first += buffer;
        buffer = new String(new char[31 - second.length()]).replace('\0', '0');
        second += buffer;
        message[0] = Integer.parseInt(first, 2);
        message[1] = Integer.parseInt(second, 2);


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
