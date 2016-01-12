package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class AttackCommunication extends Communication
{
    private int x;
    private int y;

    public AttackCommunication()
    {
        super();
        opcode = CommunicationType.TURRET_SUPPORT;
    }

    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode),x,y};
    }

    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        x = values[1];
        y = values[2];
    }

    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.locationSize, CommunicationUtilities.locationSize};
    }
}
