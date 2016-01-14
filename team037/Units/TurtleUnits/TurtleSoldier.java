package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Units.BaseSoldier;
import team037.Utilites.Utilities;

public class TurtleSoldier extends BaseSoldier
{
    public TurtleSoldier(RobotController rc)
    {
        super(rc);
    }

    @Override
    public boolean updateTarget()
    {
        // TODO: Implement this
        return false;
    }

    @Override
    public MapLocation getNextSpot()
    {
        // TODO: Implement this
        return null;
    }
}
