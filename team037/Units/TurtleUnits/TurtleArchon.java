package team037.Units.TurtleUnits;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Units.BaseArchon;
import team037.Utilites.Utilities;

public class TurtleArchon extends BaseArchon
{
    public MapLocation[] turtleSpot;

    public TurtleArchon(RobotController rc)
    {
        super(rc);


    }

    @Override
    public boolean act() throws GameActionException
    {
        if (rc.isCoreReady() && carryOutAbility()) {
            return true;
        }

        if (fight());
        else if (fightZombies());
        else if (carryOutAbility());
        if(updateTarget()) {
            navigator.setTarget(getNextSpot());
        }

        return navigator.takeNextStep();
    }

    public boolean updateTarget()
    {
        return false;
    }

    public MapLocation getNextSpot()
    {
        return null;
    }
}
