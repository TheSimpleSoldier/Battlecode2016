package team037.Messages;

import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class MissionCommunication extends Communication
{
    public int id;
    public Bots newBType;
    public int x;
    public int y;

    @Override
    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode), id, Bots.toInt(newBType), x, y};
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        id = values[1];
        newBType = Bots.fromInt(values[2]);
        x = values[3];
        y = values[4];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.valSize,
        CommunicationUtilities.botSize, CommunicationUtilities.locationSize,
        CommunicationUtilities.locationSize};
    }
}
