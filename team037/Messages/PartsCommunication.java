package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class PartsCommunication extends Communication
{
    public int parts;
    public int x;
    public int y;

    @Override
    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode), parts, x, y};
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        parts = values[1];
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
