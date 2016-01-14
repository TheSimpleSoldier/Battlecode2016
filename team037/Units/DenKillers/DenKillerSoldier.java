package team037.Units.DenKillers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team037.Enums.Bots;
import team037.Units.BaseUnits.BaseSoldier;

public class DenKillerSoldier extends BaseSoldier
{
    public DenKillerSoldier(RobotController rc)
    {
        super(rc);
    }

    public boolean takeNextStep() throws GameActionException
    {
        MapLocation goal = navigator.getTarget();

        if (goal == null || currentLocation.equals(goal) || (rc.canSenseLocation(goal) && (rc.senseRobotAtLocation(goal) == null || rc.senseRobotAtLocation(goal).team != Team.ZOMBIE || !rc.onTheMap(goal))))
        {
            // if we are standing on a den location alert all other allies that a zombie den has been destroyed
            if (mapKnowledge.denLocations.contains(currentLocation))
            {
                rc.broadcastSignal(2500);
            }

            if (mapKnowledge.denLocations.hasLocations())
            {
                navigator.setTarget(mapKnowledge.closestDen(currentLocation));
            }
        }
        else
        {
            if (rc.getRoundNum() > 1000)
            {
                nextBot = Bots.RUSHINGSOLDIER;
            }
        }

        if (rc.getRoundNum() % 5 == 0 && goal != null && mapKnowledge.denLocations.hasLocations())
        {
            mapKnowledge.updateDens(rc);
            if (mapKnowledge.denLocations.hasLocations())
            {
                navigator.setTarget(mapKnowledge.closestDen(currentLocation));
            }
        }

        return navigator.takeNextStep();
    }
}
