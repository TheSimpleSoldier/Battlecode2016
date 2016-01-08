package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

/**
 * Created by joshua on 1/7/16.
 */
public class MapBoundsCommunication extends Communication
{
    public int widthIndicator;
    public int minX;
    public int width;
    public int heightIndicator;
    public int minY;
    public int height;

    @Override
    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode), widthIndicator, minX, width,
        heightIndicator, minY, height};
    }

    @Override
    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
        widthIndicator = values[1];
        minX = values[2];
        width = values[3];
        heightIndicator = values[4];
        minY = values[5];
        height = values[6];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize, CommunicationUtilities.mapIndicatorSize,
        CommunicationUtilities.locationSize, CommunicationUtilities.mapSpanSize,
        CommunicationUtilities.mapIndicatorSize, CommunicationUtilities.locationSize,
        CommunicationUtilities.mapSpanSize};
    }
}
