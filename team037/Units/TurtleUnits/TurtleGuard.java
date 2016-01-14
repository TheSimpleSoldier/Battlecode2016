package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Units.BaseGaurd;
import team037.Utilites.Utilities;

public class TurtleGuard extends BaseGaurd
{
    MapLocation turtlePoint;

    public TurtleGuard(RobotController rc)
    {
        super(rc);
    }

    public boolean updateTarget()
    {
        // TODO: Implement this
        return false;
    }

    public MapLocation getNextSpot()
    {
        // TODO: Implement this
        return null;
    }
}
