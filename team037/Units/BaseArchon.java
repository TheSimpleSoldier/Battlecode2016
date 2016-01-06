package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.DataStructures.BuildOrder;
import team037.Enums.Bots;
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

    public Unit getNewStrategy(Unit current) throws GameActionException
    {
        return current;
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
                    nextBot = buildOrder.nextBot();
                    nextType = Utilities.typeFromBot(nextBot);
                    return true;
                }
            }
        }

        return false;
    }
}
