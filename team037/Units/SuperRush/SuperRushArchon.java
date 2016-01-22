package team037.Units.SuperRush;

import battlecode.common.*;
import team037.DataStructures.UnitProportion;
import team037.Unit;

/**
 * Created by joshua on 1/22/16.
 */
public class SuperRushArchon extends Unit
{
    public UnitProportion unitProportion;

    public RobotType first;
    public boolean spawnedFirst;

    public MapLocation targetArchon;
    public Direction dirTo;

    public SuperRushArchon(RobotController rc)
    {
        super(rc);
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();
        first = RobotType.GUARD;
        unitProportion = new UnitProportion(0, 1, 0, 0, 0);

        spawnedFirst = false;
    }

    @Override
    public boolean act() throws GameActionException
    {
        if(!rc.isCoreReady())
        {
            return false;
        }
        else if(!spawnedFirst)
        {
            if(spawnUnit())
            {
                spawnedFirst = true;
            }
        }
        else if(activateUnit()){}
        else if(collectParts()){}
        else if(spawnUnit()){}
        else if(moveToSeeEnemy()){}
        else if(runAway()){}

        return true;
    }

    private boolean activateUnit() throws GameActionException
    {
        RobotInfo[] neutrals = rc.senseNearbyRobots(RobotType.ARCHON.sensorRadiusSquared, Team.NEUTRAL);
        if(neutrals.length > 0)
        {
            RobotInfo nearest = null;
            int minDist = 99999999;
            for(int k = neutrals.length; --k >= 0;)
            {
                int tempDist = currentLocation.distanceSquaredTo(neutrals[k].location);
                if(tempDist < minDist)
                {
                    minDist = tempDist;
                    nearest = neutrals[k];
                }
            }

            if(minDist <= 2)
            {
                rc.activate(nearest.location);
                unitProportion.addBot(nearest.type);
                return true;
            }

            navigator.setTarget(nearest.location);
            navigator.takeNextStep();
            return true;
        }

        return false;
    }

    private boolean collectParts() throws GameActionException
    {
        if(rc.getTeamParts() < 30)
        {
            MapLocation[] locs = rc.sensePartLocations(5);
            if(locs.length > 0)
            {
                MapLocation nearest = null;
                int minDist = 99999999;
                for(int k = locs.length; --k >= 0;)
                {
                    int tempDist = currentLocation.distanceSquaredTo(locs[k]);
                    if(tempDist < minDist)
                    {
                        minDist = tempDist;
                        nearest = locs[k];
                    }
                }

                navigator.setTarget(nearest);
                navigator.takeNextStep();
                return true;
            }
        }
        return false;
    }

    private boolean spawnUnit() throws GameActionException
    {
        RobotType toSpawn;

        if(!spawnedFirst)
        {
            toSpawn = first;
        }
        else
        {
            toSpawn = unitProportion.nextBot();
        }

        double minRubble = 999999999;
        Direction minDirection = Direction.NONE;
        if(rc.hasBuildRequirements(toSpawn))
        {
            double tempRubble = tryBuilding(dirTo, toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo;
            }
            tempRubble = tryBuilding(dirTo.rotateLeft(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateLeft();
            }
            tempRubble = tryBuilding(dirTo.rotateRight(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateRight();
            }
            tempRubble = tryBuilding(dirTo.rotateLeft().rotateLeft(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateLeft().rotateLeft();
            }
            tempRubble = tryBuilding(dirTo.rotateRight().rotateRight(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateRight().rotateRight();
            }
            tempRubble = tryBuilding(dirTo.rotateLeft().rotateLeft().rotateLeft(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateLeft().rotateLeft().rotateLeft();
            }
            tempRubble = tryBuilding(dirTo.rotateRight().rotateRight().rotateRight(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minRubble = tempRubble;
                minDirection = dirTo.rotateRight().rotateRight().rotateRight();
            }
            tempRubble = tryBuilding(dirTo.opposite(), toSpawn);
            if(tempRubble < -50)
            {
                return true;
            }
            else if(tempRubble > -.5 && tempRubble < minRubble)
            {
                minDirection = dirTo.opposite();
            }

            if(minDirection != Direction.NONE)
            {
                rc.clearRubble(minDirection);
            }
        }
        return false;
    }

    private double tryBuilding(Direction dir, RobotType toSpawn) throws GameActionException
    {
        if(rc.onTheMap(currentLocation.add(dir)))
        {
            if(rc.canBuild(dir, toSpawn))
            {
                rc.build(dir, toSpawn);
                return -100;
            }
            else
            {
                return rc.senseRubble(currentLocation.add(dir));
            }
        }

        return -1;
    }

    private boolean moveToSeeEnemy()
    {
        return false;
    }

    private boolean runAway()
    {
        return false;
    }

    @Override
    public boolean takeNextStep() throws GameActionException
    {
        return false;
    }

    @Override
    public boolean fight() throws GameActionException
    {
        return false;
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        return false;
    }
}
