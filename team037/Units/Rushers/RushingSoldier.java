package team037.Units.Rushers;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Units.BaseUnits.BaseSoldier;
import team037.Units.PacMan.PacMan;
import team037.Units.PacMan.PacManUtils;

/**
 * Extension of BaseSoldier designed to rush the enemy.
 */
public class RushingSoldier extends BaseSoldier implements PacMan
{
    private boolean rushing = false;
    private MapLocation lastTarget = null;
    private MapLocation[] updatedLocs;
    private int currentIndex = -1;
    private int dist = Integer.MAX_VALUE;

    public RushingSoldier(RobotController rc)
    {
        super(rc);
        updatedLocs = new MapLocation[enemyArchonStartLocs.length];

        for (int i = updatedLocs.length; --i>=0; )
        {
            updatedLocs[i] = enemyArchonStartLocs[i];
        }

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
    public boolean updateTarget() throws GameActionException
    {
        return true;
        /*MapLocation target = navigator.getTarget();
        if (target == null) return true;
        if (currentLocation.equals(target) || currentLocation.isAdjacentTo(target)) return true;
        return false;*/
    }

    @Override
    public boolean fight() throws GameActionException
    {
        return fightMicro.basicAttack(nearByEnemies, enemies);
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (rushing && zombies == null && zombies.length == 0 || (zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN))) {
            return runAway(null);
        }
        return super.fightZombies();
    }



    /**
     * Add additional constants to push the unit towards enemy Archons AND away from allied Archons
     *
     * @param directions
     * @return
     */
    public int[] applyAdditionalConstants(int[] directions) {

        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
        if (myArchons == null) {
            myArchons = rc.getInitialArchonLocations(us);
        }
        directions = PacManUtils.applySimpleConstants(currentLocation, directions, myArchons, new int[]{16, 8, 4});

        MapLocation[] badArchons = mapKnowledge.getArchonLocations(false);
        if (badArchons == null) {
            badArchons = rc.getInitialArchonLocations(us);
        }
        directions = PacManUtils.applySimpleConstants(currentLocation, directions, badArchons, new int[]{-8, -4, -2});

        return directions;
    }
}
