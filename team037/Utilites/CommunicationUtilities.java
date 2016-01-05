package team037.Utilites;

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
                communication.bType = RobotType.values()[type >>> (32 - typeSize)];
                int locX = message[1] >>> (31 - locationSize);
                communication.x = locX - 16000 + locationoffset;
                int locY = message[1] << (locationSize + 1);
                locY = locY >>> (32 - locationSize);
                communication.y = locY - 16000 + locationoffset;
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

    public static int[] createCommunication(Communication communication)
    {
        return null;
    }
}
