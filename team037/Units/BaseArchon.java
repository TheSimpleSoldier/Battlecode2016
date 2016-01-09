package team037.Units;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.SortedParts;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.MissionCommunication;
import team037.Unit;
import team037.Utilites.BuildOrderCreation;


public class BaseArchon extends Unit
{
    private BuildOrder buildOrder;
    Bots nextBot;
    RobotType nextType;
    RobotInfo[] neutralBots;
    SortedParts sortedParts = new SortedParts();

    public BaseArchon(RobotController rc)
    {
        super(rc);
        buildOrder = BuildOrderCreation.createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Bots.typeFromBot(nextBot);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public void collectData() throws GameActionException
    {
        super.collectData();
        neutralBots = rc.senseNearbyRobots(2, Team.NEUTRAL);

        // don't need to check every round
        if (rc.getRoundNum() % 5 == 0)
        {
            sortedParts.findPartsAndNeutralsICanSense(rc);
        }
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean healNearbyAllies() throws GameActionException {
        // precondition
        if (nearByAllies.length == 0 || !repaired) {
            return false;
        }

        double weakestHealth = 9999;
        RobotInfo weakest = null;

        for (int i = nearByAllies.length; --i>=0; )
        {
            double health = nearByAllies[i].health;
            if (nearByAllies[i].type != RobotType.ARCHON && health < nearByAllies[i].maxHealth && currentLocation.distanceSquaredTo(nearByAllies[i].location) <= RobotType.ARCHON.attackRadiusSquared)
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
            repaired = true;
            return true;
        }
        return false;
    }

    // maybe spawn a unit or repair a damaged unit
    public boolean carryOutAbility() throws GameActionException
    {
        // heal doesn't effect core cooldown
        healNearbyAllies();

        if (neutralBots.length > 0 && rc.isCoreReady())
        {
            rc.activate(neutralBots[0].location);
        }

        if(rc.hasBuildRequirements(nextType) && rc.isCoreReady())
        {
            for (int i = dirs.length; --i>=0; )
            {
                if (rc.canBuild(dirs[i], nextType))
                {
                    rc.build(dirs[i], nextType);
                    int id = rc.senseRobotAtLocation(rc.getLocation().add(dirs[i])).ID;
                    MissionCommunication communication = new MissionCommunication();
                    communication.opcode = CommunicationType.CHANGEMISSION;
                    communication.id = id;
                    communication.rType = Bots.typeFromBot(nextBot);
                    communication.bType = nextBot;
                    communication.newBType = nextBot;
                    communicator.sendCommunication(2, communication);
                    nextBot = buildOrder.nextBot();
                    nextType = Bots.typeFromBot(nextBot);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Process incoming msgs
     *
     * @throws GameActionException
     */
    public void handleMessages() throws GameActionException
    {
        communications = communicator.processCommunications();
        for(int k = 0; k < communications.length; k++)
        {
            if(communications[k].opcode == CommunicationType.CHANGEMISSION)
            {
                MissionCommunication comm = (MissionCommunication) communications[k];
                if(comm.id == rc.getID())
                {
                    nextBot = comm.newBType;
                }
                else if(comm.id == 0 && comm.bType == thisBot)
                {
                    nextBot = comm.newBType;
                }
                else if(comm.id == 0 && comm.rType == rc.getType())
                {
                    nextBot = comm.newBType;
                }
            }
            else if (communications[k].opcode == CommunicationType.PARTS)
            {
                // if we get a new msg about parts then check to see if we have added it to our list of sorted parts
                int[] values = communications[k].getValues();
                MapLocation loc = new MapLocation(values[2], values[3]);

                if (!sortedParts.contains(loc))
                {
                    sortedParts.addParts(loc, currentLocation, values[1], false);
                }
            }
            else if (communications[k].opcode == CommunicationType.NEUTRAL)
            {
                // if we get a msg about neturals then check to see if we have that location stored
                int[] values = communications[k].getValues();
                MapLocation loc = new MapLocation(values[2], values[3]);

                if (!sortedParts.contains(loc))
                {
                    sortedParts.addParts(loc, currentLocation, values[1], true);
                }

            }
        }
    }
}
