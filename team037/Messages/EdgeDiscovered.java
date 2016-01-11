package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class EdgeDiscovered extends Communication
{
    public int id;
    public int edge;

    @Override
    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode), id, edge};
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        id = values[1];
        edge = values[2];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.valSize,
                CommunicationUtilities.valSize};
    }
}
