package team037.Messages;

import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

/**
 * Communication used to inform all units of the
 * known boundaries of the map.
 */
public class MapBoundsCommunication extends Communication
{
    public int edgeIndicator;
    public int xVal;
    public int maxX;
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
                maxY,
                edgeIndicator
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
        edgeIndicator = values[5];
    }

    @Override
    public int[] getLengths()
    {
        return new int[]{
            CommunicationUtilities.opcodeSize,
            CommunicationUtilities.locationSize,
            CommunicationUtilities.mapSpanSize,
            CommunicationUtilities.locationSize,
            CommunicationUtilities.mapSpanSize,
            CommunicationUtilities.mapIndicatorSize
        };
    }
}
