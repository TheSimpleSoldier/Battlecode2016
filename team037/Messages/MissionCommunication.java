package team037.Messages;

import battlecode.common.RobotType;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;
import team037.Utilites.Utilities;

/**
 * Created by joshua on 1/7/16.
 */
public class MissionCommunication extends Communication
{
    public int id;
    public RobotType rType;
    public Bots bType;
    public Bots newBType;

    @Override
    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode), id, Utilities.intFromType(rType),
        Bots.toInt(bType), Bots.toInt(newBType)};
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        id = values[1];
        rType = Utilities.typeFromInt(values[2]);
        bType = Bots.fromInt(values[3]);
        newBType = Bots.fromInt(values[4]);
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.valSize,
        CommunicationUtilities.typeSize, CommunicationUtilities.botSize, CommunicationUtilities.botSize};
    }
}
