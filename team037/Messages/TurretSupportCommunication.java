package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class TurretSupportCommunication extends Communication
{
    public CommunicationType opcode;
    private int x;
    private int y;

    public TurretSupportCommunication()
    {
        super();
        opcode = CommunicationType.TURRET_SUPPORT;
    }

    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode),0,x,y};
    }

    public void setValues(int[] values)
    {
        x = values[2];
        y = values[3];
    }

    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.valSize, CommunicationUtilities.valSize, CommunicationUtilities.valSize};
    }
}
