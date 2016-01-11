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
        opcode = CommunicationType.fromInt(values[0]);
        widthIndicator = values[1];
        xVal = values[2];
        width = values[3];
        heightIndicator = values[4];
        yVal = values[5];
        height = values[6];
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
