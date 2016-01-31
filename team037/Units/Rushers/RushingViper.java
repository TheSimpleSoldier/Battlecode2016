package team037.Units.Rushers;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseViper;
import team037.Units.PacMan.PacMan;
import team037.Units.PacMan.PacManUtils;
import team037.Utilites.MapUtils;

/**
 * Extension of BaseViper designed to rush the enemy.
 */
public class RushingViper extends BaseViper implements PacMan
{
    private boolean rushing = false;
    private MapLocation lastTarget = null;
    private MapLocation[] updatedLocs;
    private int currentIndex = -1;
    private int dist = Integer.MAX_VALUE;

    public RushingViper(RobotController rc)
    {
        super(rc);
        updatedLocs = new MapLocation[enemyArchonStartLocs.length];
        rc.setIndicatorString(0, "rushing viper");

        for (int i = updatedLocs.length; --i>=0; )
        {
            updatedLocs[i] = enemyArchonStartLocs[i];
        }

        if (rushTarget == null)
        {
            rushTarget = MapUtils.getNearestLocation(updatedLocs, currentLocation);
        }

        rc.setIndicatorLine(currentLocation, rushTarget, 0, 0, 0);
        dist = (int) Math.sqrt(currentLocation.distanceSquaredTo(rushTarget));
        dist = dist / 2;
        dist = dist*dist;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (enemyArchon != null)
        {
            System.out.println("rushing to where the enemy archon is");
            navigator.setTarget(enemyArchon);
            enemyArchon = null;
        }

        if (currentLocation != null && rushTarget != null)
        {
            rc.setIndicatorLine(currentLocation, rushTarget, 0, 0, 0);

            if (currentLocation.distanceSquaredTo(rushTarget) < dist)
            {
                rushing = true;
            }
            else
            {
                rushing = false;
            }
        }
    }

    @Override
    public MapLocation getNextSpot() {
        if (currentIndex != -1)
        {
            if (currentIndex < updatedLocs.length)
            {
                updatedLocs[currentIndex] = null;
            }
            else
            {
                currentIndex = 0;
                for (int i = updatedLocs.length; --i>=0; )
                {
                    updatedLocs[i] = enemyArchonStartLocs[i];
                }
            }
            rushTarget =  MapUtils.getNearestLocation(updatedLocs, currentLocation);
        }

        if (rushTarget != null)
        {
            rc.setIndicatorString(1, "Rush Target x: " + rushTarget.x + " y: " + rushTarget.y + " Round numb: " + rc.getRoundNum());
        }

        currentIndex++;
        return rushTarget;
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();
        if (target == null) return true;
        if (currentLocation.equals(target) || currentLocation.isAdjacentTo(target)) return true;
        return false;
    }

    @Override
    public boolean fight() throws GameActionException
    {
        return fightMicro.ViperAgressiveMicro(enemies);
//        return fightMicro.basicAttack(nearByEnemies, enemies);
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        return false;
//        if (zombies == null && zombies.length == 0 || (zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN))) {
//            return false;
//        }
//
//        return runAway(null);
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
