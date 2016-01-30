package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

/**
 * Communication used to declare a target for attack.
 */
public class AttackCommunication extends Communication
{
    public int x;
    public int y;

    public AttackCommunication()
    {
        super();
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
