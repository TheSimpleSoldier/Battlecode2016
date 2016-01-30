package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

/**
 * Communication used to coordinate the fire of Turrets.
 */
public class TurretSupportCommunication extends Communication
{
    private int x;
    private int y;
    private int coreDelay;

    public TurretSupportCommunication()
    {
        super();
        opcode = CommunicationType.TURRET_SUPPORT;
    }

    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode),coreDelay,x,y};
    }

    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        coreDelay = values[1];
        x = values[2];
        y = values[3];
    }

    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.valSize, CommunicationUtilities.locationSize, CommunicationUtilities.locationSize};
    }
}
