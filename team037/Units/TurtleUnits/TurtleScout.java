package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.FightMicroUtilites;
import team037.Utilites.MapUtils;

public class TurtleScout extends BaseScout
{
    // This determines if the scout is going left or right around the turtle location
    private static boolean goingLeft = false;
    // this is the minimum distance that a scout will keep between it and the turtle center at all times
    private static final int minDist = 5;
    // this is the max distance that a scout will keep between it and the turtle center
    private static final int maxDist = 6;
    private static int directionUpdateTurn = 0;
    private static int furthestTurret = 0;

    public TurtleScout(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot2(alliedArchonStartLocs, enemyArchonStartLocs);
        rc.setIndicatorString(0, "Turtle scout x: " + turtlePoint.x + " y: " + turtlePoint.y);
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        MapLocation target = navigator.getTarget();

        if (target == null) return true;
        if (currentLocation.equals(target)) return true;
        if (rc.canSense(target) && !rc.onTheMap(target)) return true;
        if (rc.canSense(target) && rc.senseRobotAtLocation(target) != null) return true;
        if (currentLocation.distanceSquaredTo(target) < getMinDist()) return true;
        if (currentLocation.distanceSquaredTo(target) > getMaxDist()) return true;

        return false;
    }

    /**
     * This method gets the closest dist to the turtle point that the scout should be
     * @return
     */
    public int getMinDist()
    {
        int newMinDist = (int) ((minDist + (furthestTurret * 0.75)) * (minDist + (furthestTurret * 0.75)) - 1);
        rc.setIndicatorString(0, "MinDist: " + newMinDist);
        return newMinDist;
    }

    /**
     * This method returns the furthest distance a turtle scout should ever be from the
     * turtle point
     * @return
     */
    public int getMaxDist()
    {
        int newMaxDist = ((maxDist + furthestTurret) * (maxDist + furthestTurret) + 1);
        rc.setIndicatorString(2, "MaxDist: " + newMaxDist);
        return newMaxDist;
    }

    @Override
    public MapLocation getNextSpot() throws GameActionException
    {
        if (currentLocation.distanceSquaredTo(turtlePoint) < getMinDist())
        {
            // set target to run away from the turtle point
            return currentLocation.add(turtlePoint.directionTo(currentLocation), 100);
        }

        if (currentLocation.distanceSquaredTo(turtlePoint) > getMaxDist())
        {
            return turtlePoint;
        }

        if (MapUtils.nextToEdge(currentLocation, rc) && rc.getRoundNum() - directionUpdateTurn > 5)
        {
            goingLeft = !goingLeft;
            directionUpdateTurn = rc.getRoundNum();
        }

        return goToLocation();
    }

    private Direction goToDirection()
    {
        Direction goTo = currentLocation.directionTo(turtlePoint);
        if (goTo != null)
        {
            if (goingLeft)
            {
                goTo = goTo.rotateLeft().rotateLeft();
            }
            else
            {
                goTo = goTo.rotateRight().rotateRight();
            }
        }
        return goTo;
    }

    private MapLocation goToLocation()
    {
        return currentLocation.add(goToDirection(), 100);
    }

    @Override
    public boolean fight() throws GameActionException
    {
        if (!FightMicroUtilites.offensiveEnemies(enemies)) return false;

        Direction dir = goToDirection();
        MapLocation next = currentLocation.add(dir);

        // if we can move in
        if (rc.canMove(dir) && !fightMicro.EnemiesInRangeOfLoc(next, enemies))
        {
            rc.move(dir);
            return true;
        }
        else if (rc.canMove(dir.opposite()) && !fightMicro.EnemiesInRangeOfLoc(currentLocation.add(dir.opposite()), enemies))
        {
            goingLeft = !goingLeft;
            rc.move(dir.opposite());
            return true;
        }

        int bestEnemiesInRangeDamage = Integer.MAX_VALUE;
        Direction bestDir = null;

        for (int i = dirs.length; --i>=0; )
        {
            if (!rc.canMove(dirs[i])) continue;

            next = currentLocation.add(dirs[i]);
            int enemiesInRangeDamage = 0;

            for (int j = enemies.length; --j>=0; )
            {
                if (enemies[j].location.distanceSquaredTo(next) <= enemies[j].type.attackRadiusSquared)
                {
                    enemiesInRangeDamage += enemies[j].type.attackPower;

                    if (enemies[j].type == RobotType.VIPER)
                    {
                        enemiesInRangeDamage += 40;
                    }
                }
            }

            if (enemiesInRangeDamage < bestEnemiesInRangeDamage)
            {
                bestEnemiesInRangeDamage = enemiesInRangeDamage;
                bestDir = dirs[i];

                if (enemiesInRangeDamage == 0)
                {
                    break;
                }
            }
        }

        if (bestDir != null && fightMicro.EnemiesInRangeOfLoc(currentLocation.add(bestDir), enemies) && !fightMicro.EnemiesInRangeOfLoc(currentLocation, enemies))
        {
            // stay put
            return true;
        }
        else
        {
            if (bestDir != null)
            {
                rc.move(bestDir);
                return true;
            }
        }

        return false;
    }

    @Override
    public void sendMessages() throws GameActionException
    {
        // override default behavior
        msgTurrets();
        msgParts();
        msgDens();
        msgEnemyArchons();
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (FightMicroUtilites.offensiveEnemies(zombies))
        {
            rc.setIndicatorString(1, "");

            // if enemies can attack us then try to move to get them to chase us while the turrets blast them
            if (fightMicro.EnemiesInRangeOfLoc(currentLocation, zombies))
            {
                Direction goTo = currentLocation.directionTo(turtlePoint);
                Direction left = goTo.rotateLeft().rotateLeft();
                Direction right = goTo.rotateRight().rotateRight();

                int leftCount = fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(left), zombies);
                int rightCount = fightMicro.NumbOfEnemiesInRangeOfLoc(currentLocation.add(right), zombies);

                if (rc.canMove(left) && leftCount == 0)
                {
                    rc.move(left);
                    return true;
                }
                else if (rc.canMove(right) && rightCount == 0)
                {
                    rc.move(right);
                    return true;
                }
                else
                {
                    Direction safeDir = null;
                    for (int i = dirs.length; --i>=0; )
                    {
                        if (dirs[i] != left && dirs[i] != right && rc.canMove(dirs[i]))
                        {
                            MapLocation next = currentLocation.add(dirs[i]);

                            if (!fightMicro.EnemiesInRangeOfLoc(next, zombies))
                            {
                                safeDir = dirs[i];
                                break;
                            }
                        }
                    }

                    if (safeDir != null)
                    {
                        rc.move(safeDir);
                        return true;
                    }
                    else if (rc.canMove(left))
                    {
                        rc.move(left);
                        return true;
                    }
                    else if (rc.canMove(goTo.rotateLeft()))
                    {
                        rc.move(goTo.rotateLeft());
                        return true;
                    }
                    else if (rc.canMove(goTo.rotateRight()))
                    {
                        rc.move(goTo.rotateRight());
                        return true;
                    }
                }
            }
            else
            {
                boolean herding = false;

                for (int i = zombies.length; --i>=0; )
                {
                    switch (zombies[i].type)
                    {
                        case FASTZOMBIE:
                        case STANDARDZOMBIE:
                        case RANGEDZOMBIE:
                        case BIGZOMBIE:

                            MapLocation zombie = zombies[i].location;
                            int ourDist = currentLocation.distanceSquaredTo(zombie);

                            for (int j = allies.length; --j>=0;)
                            {
                                if (allies[j].type == RobotType.SCOUT)
                                {
                                    MapLocation ally = allies[j].location;
                                    int theirDist = ally.distanceSquaredTo(zombie);

                                    // check to see if we are herding
                                    if (theirDist >= ourDist)
                                    {
                                        herding = true;

                                        // break out of for loops
                                        i = -1;
                                        j = -1;
                                    }
                                }
                            }
                            break;
                    }
                }

                if (!herding)
                {
                    rc.setIndicatorString(1, "Not herding: " + rc.getRoundNum());
                    MapLocation navigatorTarget = currentLocation.add(goToDirection());

                    boolean keepGoing = true;
                    int ourDist = currentLocation.distanceSquaredTo(navigatorTarget);

                    for (int i = zombies.length; --i>=0; )
                    {
                        switch (zombies[i].type)
                        {
                            case FASTZOMBIE:
                            case STANDARDZOMBIE:
                            case RANGEDZOMBIE:
                            case BIGZOMBIE:
                                if (ourDist > zombies[i].location.distanceSquaredTo(navigatorTarget))
                                {
                                    keepGoing = false;
                                    break;
                                }
                                break;
                        }
                    }

                    // if we should just keep going around the circle
                    if (keepGoing)
                    {
                        rc.setIndicatorString(2, "Continue in current dir: " + rc.getRoundNum());
                        return false;
                    }
                    else
                    {
                        Direction current = currentLocation.directionTo(navigatorTarget).opposite();

                        MapLocation goal = currentLocation.add(current);

                        keepGoing = true;
                        ourDist = currentLocation.distanceSquaredTo(goal);

                        for (int i = zombies.length; --i>=0; )
                        {
                            switch (zombies[i].type)
                            {
                                case FASTZOMBIE:
                                case STANDARDZOMBIE:
                                case RANGEDZOMBIE:
                                case BIGZOMBIE:
                                    if (ourDist > zombies[i].location.distanceSquaredTo(goal))
                                    {
                                        keepGoing = false;
                                        break;
                                    }
                                    break;
                            }
                        }

                        // if we should go the opposite way then do it
                        if (keepGoing)
                        {
                            rc.setIndicatorString(2, "Going opposite way now: " + rc.getRoundNum());
                            goingLeft = !goingLeft;
                            navigator.setTarget(goToLocation());
                            return false;
                        }
                        else
                        {
                            rc.setIndicatorString(2, "staying put");
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (rallyPoint != null)
        {
            rc.setIndicatorLine(currentLocation, rallyPoint, 0, 0, 255);
            rc.setIndicatorString(1, "Going to new rally point");
            turtlePoint = rallyPoint;
            furthestTurret = 0;
        }

        // furthest turret dist should degrade if we don't have any turrets in vision
        if (rc.getRoundNum() % 10 == 0 && furthestTurret > 0)
        {
            furthestTurret--;
        }

        for (int i = allies.length; --i>=0;)
        {
            if (allies[i].type == RobotType.TURRET)
            {
                int newDist = (int) Math.sqrt(allies[i].location.distanceSquaredTo(turtlePoint));

                if (newDist > furthestTurret)
                {
                    furthestTurret = newDist;
                }
            }
        }
    }
}
