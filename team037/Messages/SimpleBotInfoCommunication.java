package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class SimpleBotInfoCommunication extends Communication
{
    public int id;
    public int x;
    public int y;

    @Override
    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode), id, x, y};
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        id = values[1];
        x = values[2];
        y = values[3];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.valSize,
        CommunicationUtilities.locationSize, CommunicationUtilities.locationSize};
    }
}
