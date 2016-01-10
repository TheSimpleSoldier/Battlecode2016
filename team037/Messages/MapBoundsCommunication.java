package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

public class MapBoundsCommunication extends Communication
{
    public int widthIndicator;
    public int xVal;
    public int width;
    public int heightIndicator;
    public int yVal;
    public int height;

    public MapBoundsCommunication() {
        super();
        opcode = CommunicationType.MAP_BOUNDS;
    }

    @Override
    public int[] getValues()
    {
        return new int[]{
                CommunicationType.toInt(opcode),
                widthIndicator,
                xVal,
                width,
                heightIndicator,
                yVal,
                height
        };
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.MAP_BOUNDS;
        widthIndicator = values[0];
        xVal = values[1];
        width = values[2];
        heightIndicator = values[3];
        yVal = values[4];
        height = values[5];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{
            CommunicationUtilities.opcodeSize,
            CommunicationUtilities.mapIndicatorSize,
            CommunicationUtilities.locationSize,
            CommunicationUtilities.mapSpanSize,
            CommunicationUtilities.mapIndicatorSize,
            CommunicationUtilities.locationSize,
            CommunicationUtilities.mapSpanSize
        };
    }
}
