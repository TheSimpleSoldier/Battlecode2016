package team037.Messages;

import battlecode.common.MapLocation;
import battlecode.common.Team;
import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

/**
 * Created by davej on 1/11/2016.
 */
public class RubbleCommunication extends Communication {

    public int hi, lo;

    public RubbleCommunication() {
        super();
    }


    public int[] getValues()
    {
        // Separate most significant and least significant bits
        return new int[]{CommunicationType.toInt(opcode),hi,lo};
    }

    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        hi = values[1]; // Upper 24 bits
        lo = values[2]; // Lower 25 bits
    }

    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, 24, 25};
    }
}
