package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.Communication;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Unit;
import team037.Utilites.BuildOrderCreation;
import team037.Utilites.Utilities;

public class BaseArchon extends Unit
{
    private BuildOrder buildOrder;
    Bots nextBot;
    RobotType nextType;

    public BaseArchon(RobotController rc)
    {
        super(rc);
        buildOrder = BuildOrderCreation.createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Utilities.typeFromBot(nextBot);
    }

    public boolean takeNextStep() throws GameActionException
    {
        if (navigator.getTarget() == null) {
            MapLocation currentLoc = rc.getLocation();
            navigator.setTarget(new MapLocation(currentLoc.x, currentLoc.y + 17));
        }
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return false;
    }

    public boolean fightZombies() throws GameActionException
    {
        return false;
    }

    // maybe spawn a unit?
    public boolean carryOutAbility() throws GameActionException
    {

        if(rc.hasBuildRequirements(nextType) && rc.getCoreDelay() < 1)
        {
            for (int i = dirs.length; --i>=0; )
            {
                if (rc.canBuild(dirs[i], nextType))
                {
                    rc.build(dirs[i], nextType);
                    int id = rc.senseRobotAtLocation(rc.getLocation().add(dirs[i])).ID;
                    Communication communication = new Communication();
                    communication.opcode = CommunicationType.INITIALMISSION;
                    communication.val1 = id;
                    communication.bType1 = nextBot;
                    communicator.sendCommunication(2, communication);
                    nextBot = buildOrder.nextBot();
                    nextType = Utilities.typeFromBot(nextBot);
                    return true;
                }
            }
        }

        return false;
    }
}
