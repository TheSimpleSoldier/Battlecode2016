package team037.Utilites;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Signal;
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
                communication.type = CommunicationType.DEN;
            case ENEMY:
                communication.type = CommunicationType.ENEMY;
            case PARTS:
                communication.type = CommunicationType.PARTS;
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
            case MISSION:
                communication.type = CommunicationType.MISSION;
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

        communication.type = CommunicationType.ENEMYL;
        communication.id = signal.getRobotID();
        MapLocation loc = signal.getLocation();
        communication.x = loc.x;
        communication.y = loc.y;

        return communication;
    }

    public static int[] createCommunication(Communication communication)
    {
        /*
        * c-type(change mission)
            * first int- opcode, id(0 if doesn't matter), type, bot
            * second int- new bot
            * */
        int[] message = new int[2];
        String first = "";
        String second = "";
        switch(communication.type)
        {
            case DEN:
                String type = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.DEN));
                first += ("0000" + type).substring(type.length());
            case ENEMY:
                String type2 = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.ENEMY));
                first += ("0000" + type2).substring(type2.length());
            case PARTS:
                String type3 = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.PARTS));
                first += ("0000" + type3).substring(type3.length());
                String id = Integer.toBinaryString(communication.id);
                first += ("000000000000000" + id).substring(id.length());
                switch(communication.bType)
                {
                    case ARCHON:
                        first += "0000";
                    case BIGZOMBIE:
                        first += "0001";
                    case FASTZOMBIE:
                        first += "0010";
                    case GUARD:
                        first += "0011";
                    case RANGEDZOMBIE:
                        first += "0100";
                    case SCOUT:
                        first += "0101";
                    case SOLDIER:
                        first += "0110";
                    case STANDARDZOMBIE:
                        first += "0111";
                    case TTM:
                        first += "1000";
                    case TURRET:
                        first += "1001";
                    case VIPER:
                        first += "1010";
                    case ZOMBIEDEN:
                        first += "1011";
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
            case MISSION:
                String type4 = Integer.toBinaryString(CommunicationType.toInt(CommunicationType.MISSION));
                first += ("0000" + type4).substring(type4.length());
                String id2 = Integer.toBinaryString(communication.id);
                first += ("000000000000000" + id2).substring(id2.length());
                switch(communication.bType)
                {
                    case ARCHON:
                        first += "0000";
                    case BIGZOMBIE:
                        first += "0001";
                    case FASTZOMBIE:
                        first += "0010";
                    case GUARD:
                        first += "0011";
                    case RANGEDZOMBIE:
                        first += "0100";
                    case SCOUT:
                        first += "0101";
                    case SOLDIER:
                        first += "0110";
                    case STANDARDZOMBIE:
                        first += "0111";
                    case TTM:
                        first += "1000";
                    case TURRET:
                        first += "1001";
                    case VIPER:
                        first += "1010";
                    case ZOMBIEDEN:
                        first += "1011";
                }
                String btype = Integer.toBinaryString(Bots.toInt(communication.sType));
                first += ("0000" + btype).substring(btype.length());
                message[0] = Integer.parseInt(first, 2);
                message[1] = Bots.toInt(communication.nType);
        }
        return message;
    }
}
