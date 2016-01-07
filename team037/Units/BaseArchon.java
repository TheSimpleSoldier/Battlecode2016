package team037.Units;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.Communication;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Unit;
import team037.Utilites.BuildOrderCreation;

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
        nextType = Bots.typeFromBot(nextBot);
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

    // maybe spawn a unit or repair a damaged unit
    public boolean carryOutAbility() throws GameActionException
    {
        if (nearByAllies.length > 0)
        {
            double weakestHealth = 9999;
            RobotInfo weakest = null;

            for (int i = nearByAllies.length; --i>=0; )
            {
                double health = nearByAllies[i].health;
                if (health < nearByAllies[i].maxHealth)
                {
                    if (health < weakestHealth)
                    {
                        weakestHealth = health;
                        weakest = nearByAllies[i];
                    }
                }
            }

            if (weakest != null)
            {
                rc.repair(weakest.location);
            }
        }

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
                    nextType = Bots.typeFromBot(nextBot);
                    return true;
                }
            }
        }

        return false;
    }
}
