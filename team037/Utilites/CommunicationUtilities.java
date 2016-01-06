package team037.Utilites;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Signal;
import team037.DataStructures.CommTypeToSpacing;
import team037.DataStructures.Communication;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;

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
 * first int- opcode, id(0 if doesn't matter), type, bot
 * second int- new bot
 */
public class CommunicationUtilities
{
    static int opcodeSize = 4;
    static int locationSize = 15;
    static int idSize = 15;
    static int botSize = 8;
    static int typeSize = 4;

    static int locationoffset = 53;

    public static Communication readCommunication(Signal signal)
    {
        Communication communication = new Communication();
        int[] message = signal.getMessage();
        int opcode = message[0] >>> (31 - opcodeSize);

        switch(CommunicationType.values()[opcode])
        {
            case DEN:
            case ENEMY:
            case PARTS:
                switch(CommunicationType.values()[opcode])
                {
                    case DEN:
                        communication.type = CommunicationType.DEN;
                        break;
                    case ENEMY:
                        communication.type = CommunicationType.ENEMY;
                        break;
                    case PARTS:
                        communication.type = CommunicationType.PARTS;
                        break;
                }
                int id = message[0] << (opcodeSize + 1);
                communication.id = id >>> (32 - idSize);
                int type = message[0] << (opcodeSize + idSize + 1);
                type = type >>> (32 - typeSize);
                switch(type)
                {
                    case 0:
                        communication.bType = RobotType.ARCHON;
                        break;
                    case 1:
                        communication.bType = RobotType.BIGZOMBIE;
                        break;
                    case 2:
                        communication.bType = RobotType.FASTZOMBIE;
                        break;
                    case 3:
                        communication.bType = RobotType.GUARD;
                        break;
                    case 4:
                        communication.bType = RobotType.RANGEDZOMBIE;
                        break;
                    case 5:
                        communication.bType = RobotType.SCOUT;
                        break;
                    case 6:
                        communication.bType = RobotType.SOLDIER;
                        break;
                    case 7:
                        communication.bType = RobotType.STANDARDZOMBIE;
                        break;
                    case 8:
                        communication.bType = RobotType.TTM;
                        break;
                    case 9:
                        communication.bType = RobotType.TURRET;
                        break;
                    case 10:
                        communication.bType = RobotType.VIPER;
                        break;
                    case 11:
                        communication.bType = RobotType.ZOMBIEDEN;
                        break;
                }
                communication.bType = RobotType.values()[type >>> (32 - typeSize)];
                int locX = message[1] >>> (31 - locationSize);
                communication.x = locX - 16100 + locationoffset;
                int locY = message[1] << (locationSize + 1);
                locY = locY >>> (32 - locationSize);
                communication.y = locY - 16100 + locationoffset;
                break;
            case CHANGEMISSION:
                communication.type = CommunicationType.CHANGEMISSION;
                int id2 = message[0] << (opcodeSize + 1);
                communication.id = id2 >>> (32 - idSize);
                int type2 = message[0] << (opcodeSize + idSize + 1);
                communication.bType = RobotType.values()[type2 >>> (32 - typeSize)];
                int bot = message[0] << (opcodeSize + idSize + typeSize + 1);
                communication.sType = Bots.values()[bot >>> (32 - botSize)];
                communication.nType = Bots.values()[message[1]];
                break;
        }

        return communication;
    }

    public static Communication readEnemyCommunication(Signal signal)
    {
        Communication communication = new Communication();

        communication.type = CommunicationType.OENEMY;
        communication.id = signal.getRobotID();
        MapLocation loc = signal.getLocation();
        communication.x = loc.x;
        communication.y = loc.y;

        return communication;
    }

    public static Communication readSimpleCommunication(Signal signal, int round)
    {
        Communication communication = new Communication();
        communication.id = signal.getRobotID();
        MapLocation loc = signal.getLocation();
        communication.x = loc.x;
        communication.y = loc.y;

        switch(round % 10)
        {
            case 0:
            case 1:
                communication.type = CommunicationType.ENEMY;
                communication.bType = RobotType.ARCHON;
                break;
            case 2:
            case 3:
                communication.type = CommunicationType.ENEMY;
                communication.bType = RobotType.SOLDIER;
                break;
            case 4:
            case 5:
                communication.type = CommunicationType.ENEMY;
                communication.bType = RobotType.STANDARDZOMBIE;
                break;
            case 6:
            case 7:
                communication.type = CommunicationType.DEN;
                communication.bType = RobotType.ZOMBIEDEN;
                break;
            case 8:
            case 9:
                communication.type = CommunicationType.PARTS;
                communication.bType = RobotType.SOLDIER;
                break;
        }

        return communication;
    }

    public static int[] createCommunication(Communication communication)
    {
        int[] message = new int[2];
        String first = "";
        String second = "";
        switch(communication.type)
        {
            case DEN:
            case ENEMY:
            case PARTS:
                switch(communication.type)
                {
                    case DEN:
                        String type = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.DEN));
                        first += ("0000" + type).substring(type.length());
                        break;
                    case ENEMY:
                        String type2 = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.ENEMY));
                        first += ("0000" + type2).substring(type2.length());
                        break;
                    case PARTS:
                        String type3 = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.PARTS));
                        first += ("0000" + type3).substring(type3.length());
                        break;
                }
                String id = Integer.toBinaryString(communication.id);
                first += ("000000000000000" + id).substring(id.length());
                switch(communication.bType)
                {
                    case ARCHON:
                        first += "0000";
                        break;
                    case BIGZOMBIE:
                        first += "0001";
                        break;
                    case FASTZOMBIE:
                        first += "0010";
                        break;
                    case GUARD:
                        first += "0011";
                        break;
                    case RANGEDZOMBIE:
                        first += "0100";
                        break;
                    case SCOUT:
                        first += "0101";
                        break;
                    case SOLDIER:
                        first += "0110";
                        break;
                    case STANDARDZOMBIE:
                        first += "0111";
                        break;
                    case TTM:
                        first += "1000";
                        break;
                    case TURRET:
                        first += "1001";
                        break;
                    case VIPER:
                        first += "1010";
                        break;
                    case ZOMBIEDEN:
                        first += "1011";
                        break;
                }
                first += "00000000";
                String x = Integer.toBinaryString(communication.x + 16100 - locationoffset);
                second += ("000000000000000" + x).substring(x.length());
                String y = Integer.toBinaryString(communication.y + 16100 - locationoffset);
                second += ("000000000000000" + y).substring(y.length());
                second += "0";
                message[0] = Integer.parseInt(first, 2);
                message[1] = Integer.parseInt(second, 2);
                break;
            case CHANGEMISSION:
                String type4 = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.CHANGEMISSION));
                first += ("0000" + type4).substring(type4.length());
                String id2 = Integer.toBinaryString(communication.id);
                first += ("000000000000000" + id2).substring(id2.length());
                switch(communication.bType)
                {
                    case ARCHON:
                        first += "0000";
                        break;
                    case BIGZOMBIE:
                        first += "0001";
                        break;
                    case FASTZOMBIE:
                        first += "0010";
                        break;
                    case GUARD:
                        first += "0011";
                        break;
                    case RANGEDZOMBIE:
                        first += "0100";
                        break;
                    case SCOUT:
                        first += "0101";
                        break;
                    case SOLDIER:
                        first += "0110";
                        break;
                    case STANDARDZOMBIE:
                        first += "0111";
                        break;
                    case TTM:
                        first += "1000";
                        break;
                    case TURRET:
                        first += "1001";
                        break;
                    case VIPER:
                        first += "1010";
                        break;
                    case ZOMBIEDEN:
                        first += "1011";
                        break;
                }
                String btype = Integer.toBinaryString(Bots.toInt(communication.sType));
                first += ("0000" + btype).substring(btype.length());
                message[0] = Integer.parseInt(first, 2);
                message[1] = Bots.toInt(communication.nType);
            case MAP_BOUNDS:
                int[] values = communication.ints;
                message = packInts(CommunicationType.MAP_BOUNDS, values);

        }
        return message;
    }

    private static final String ZEROS = "00000000000000000000000000000000000000000000000000000000";
    private static final String PADDING = "10000000000000000000000000000000000000000000000000000";
    private static final int MAX_VALUES = 30; // need string to start with 1
    private static int[] packInts(CommunicationType type, int[] values) {

        int[] lengths = CommTypeToSpacing.getSpacingArrayFromCommType(type);
        assert values.length == lengths.length;

        String first = "";
        String second = "";
        int usedFirst = 0;
        int usedSecond = 0;

        // add the message type first
        String mtype = Integer.toBinaryString(CommunicationType.toInt(type));
        first += ("0000" + mtype).substring(mtype.length());
        usedFirst += 4;

        for (int i = 0; i < values.length; i++) {
            String temp = Integer.toBinaryString(values[i]);
            if (usedFirst + values[i] <= MAX_VALUES) {
                usedFirst += values[i];
                first += (ZEROS + temp).substring(ZEROS.length() + temp.length() - lengths[i]);
            } else if (usedSecond + values[i] <= MAX_VALUES) {
                usedSecond += values[i];
                second += (ZEROS + temp).substring(ZEROS.length() + temp.length() - lengths[i]);
            } else {
                System.out.println("Oh noes! Couldn't pack in all the ints!");
                System.out.println(i);
                System.out.println(values.length);
                System.out.println(values[i]);
                System.out.println(lengths[i]);
            }
        }
        first = PADDING.substring(0, 31 - first.length()) + first;
        second = PADDING.substring(0, 31 - second.length()) + second;

        return new int[] {
                Integer.parseInt(first, 2),
                Integer.parseInt(second, 2)
        };
    }


    private static int[] unpackInts(CommunicationType type, int[] message) {
        // first figure out what values go in the first line
        int[] spacing = CommTypeToSpacing.getSpacingArrayFromCommType(type);

        int countFirst = 0;
        int usedFirst = 4; // since we know we have the message info in first
        int countSecond = 0;
        int usedSecond = 0;
        for (int i = 0; i < spacing.length; i++) {
            if (spacing[i] + usedFirst <= MAX_VALUES) {
                countFirst += 1;
                usedFirst += spacing[i];
            } else if (spacing[i] + usedSecond <= MAX_VALUES) {
                countSecond += 1;
                usedSecond += spacing[i];
            } else {
                System.out.println("Too many values to unpack!");
            }
        }

        int[] results = new int[spacing.length];
        int idx = 31 - usedFirst;
        String first = Integer.toBinaryString(message[0]);
        String second = Integer.toBinaryString(message[1]);
        for (int i = 0; i < spacing.length; i++) {
            if (countFirst > 0) {
                countFirst -= 1;
                results[i] = Integer.parseInt(first.substring(idx, idx + spacing[i]), 2);
                idx += spacing[i];
                if (countFirst == 0) {
                    idx = 31 - usedSecond;
                }
            } else if (countSecond > 0) {
                countSecond -= 1;
                results[i] = Integer.parseInt(second.substring(idx, idx + spacing[i]), 2);
                idx += spacing[i];
            } else {
                System.out.println("too many values to unpack!");
            }
        }

        return results;
    }


    public static boolean shouldCommunicateSimple(Communication communication, int round)
    {
        if(communication.type == CommunicationType.ENEMY && communication.bType == RobotType.ARCHON)
        {
            if(round % 10 == 0)
            {
                return true;
            }
        }
        else if(communication.type == CommunicationType.ENEMY && communication.bType == RobotType.SOLDIER)
        {
            if(round % 10 == 2)
            {
                return true;
            }
        }
        else if(communication.type == CommunicationType.ENEMY && communication.bType == RobotType.STANDARDZOMBIE)
        {
            if(round % 10 == 4)
            {
                return true;
            }
        }
        else if(communication.type == CommunicationType.DEN && communication.bType == RobotType.ZOMBIEDEN)
        {
            if(round % 10 == 6)
            {
                return true;
            }
        }
        else if(communication.type == CommunicationType.PARTS && communication.bType == RobotType.SOLDIER)
        {
            if(round % 10 == 8)
            {
                return true;
            }
        }

        return false;
    }
}
