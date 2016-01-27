package team037.Units.Rushers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.MapKnowledge;
import team037.Units.BaseUnits.BaseGaurd;
import team037.Units.PacMan.PacMan;
import team037.Units.PacMan.PacManUtils;
import team037.Utilites.MapUtils;

public class RushingGuard extends BaseGaurd implements PacMan
{
    private boolean rushing = false;
    private MapLocation lastTarget = null;
    private int currentIndex = -1;
    private int dist = Integer.MAX_VALUE;

    public RushingGuard(RobotController rc)
    {
        super(rc);
        rc.setIndicatorString(0, "rushing guard");

        rushTarget = mapKnowledge.getOppositeCorner(currentLocation);

        int minDist = 9999;
        for(int k = allies.length; --k >= 0;)
        {
            if(allies[k].type == RobotType.ARCHON)
            {
                int tempDist = currentLocation.distanceSquaredTo(allies[k].location);
                if(tempDist < minDist)
                {
                    minDist = tempDist;
                    myArchon = allies[k].ID;
                }
            }
        }

        dist = (int) Math.sqrt(currentLocation.distanceSquaredTo(rushTarget));
        dist = dist / 2;
        dist = dist*dist;
    }



    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        rc.setIndicatorLine(currentLocation, rushTarget, 0, 0, 0);
    }

    @Override
    public MapLocation getNextSpot()
    {
        currentIndex++;
        return rushTarget;
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (zombies != null && zombies.length > 0)
            return fightMicro.guardZombieMicro(zombies,nearByZombies,allies);
        return runAway(null,false,true);
    }
    public int[] applyAdditionalConstants(int[] directions) {

        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
        if (myArchons == null) {
            myArchons = rc.getInitialArchonLocations(us);
        }

        if (rc.isArmageddon()) {
            directions = PacManUtils.applySimpleConstants(currentLocation, directions, myArchons, new int[]{32, 16, 8});
        } else {
            directions = PacManUtils.applySimpleConstants(currentLocation, directions, myArchons, new int[]{-32, -16, -8});
        }

        return directions;
    }

    public int[] applyAdditionalWeights(int[] directions) {

        if (rc.isArmageddon()) {
            directions = PacManUtils.applyWeights(currentLocation, directions, allies, DEFAULT_WEIGHTS[ENEMIES]);
        } else {
            directions = PacManUtils.applyWeights(currentLocation, directions, allies, DEFAULT_WEIGHTS[NEUTRALS_AND_PARTS]);
        }
        return directions;
    }
}
