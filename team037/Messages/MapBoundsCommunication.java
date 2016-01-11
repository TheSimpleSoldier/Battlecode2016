package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class MapBoundsCommunication extends Communication
{
    public int widthIndicator;
    public int xVal;
    public int maxX;
    public int heightIndicator;
    public int yVal;
    public int maxY;

    public MapBoundsCommunication() {
        super();
        opcode = CommunicationType.MAP_BOUNDS;
    }

    @Override
    public int[] getValues()
    {
        return new int[]{
                CommunicationType.toInt(opcode),
                xVal,
                maxX,
                yVal,
                maxY
        };
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        xVal = values[1];
        maxX = values[2];
        yVal = values[3];
        maxY = values[4];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{
            CommunicationUtilities.opcodeSize,
            CommunicationUtilities.locationSize,
            CommunicationUtilities.mapSpanSize,
            CommunicationUtilities.locationSize,
            CommunicationUtilities.mapSpanSize
        };
    }
}
