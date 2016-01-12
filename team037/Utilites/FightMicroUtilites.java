package team037.Utilites;

import battlecode.common.*;
import battlecode.util.SquareArray;
import team037.Communicator;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;

public class FightMicroUtilites
{
    /**
     * This method returns the RobotInfo for the Robot with the lowest health
     */
    public static RobotInfo findWeakestEnemy(RobotInfo[] nearByEnemies)
    {
        RobotInfo weakest = nearByEnemies[nearByEnemies.length - 1];

        for (int i = nearByEnemies.length-1; --i >= 0; )
        {
            if (nearByEnemies[i] == null)
            {
                System.out.println("Enemy is null");
            }
            else if (nearByEnemies[i].health < weakest.health)
            {
                weakest = nearByEnemies[i];
            }
        }

        return weakest;
    }

    /**
     * This is a targeting method for vipers to infect their own units
     *
     * @param nearByAllies
     * @param nearByEnemies
     * @return
     */
    public static RobotInfo pickViperTarget(RobotInfo[] nearByAllies, RobotInfo[] nearByEnemies)
    {
        double lowestHealth = Double.MAX_VALUE;
        RobotInfo weakest = null;

        for (int i = nearByEnemies.length; --i>=0; )
        {
            if (nearByEnemies[i].viperInfectedTurns == 0 && nearByEnemies[i].zombieInfectedTurns == 0)
            {
                if (nearByEnemies[i].health <= 20 && nearByEnemies[i].health < lowestHealth)
                {
                    lowestHealth = nearByEnemies[i].health;
                    weakest = nearByEnemies[i];
                }
            }
        }

        if (weakest != null)
        {
            return  weakest;
        }

        // else look to infect an ally right before death
        for (int i = nearByAllies.length; --i>=0; )
        {
            if (nearByAllies[i].viperInfectedTurns == 0 && nearByAllies[i].zombieInfectedTurns == 0)
            {
                if (nearByAllies[i].health <= 10 && nearByAllies[i].health < lowestHealth)
                {
                    lowestHealth = nearByAllies[i].health;
                    weakest = nearByAllies[i];
                }
            }
        }

        if (weakest != null)
        {
            return weakest;
        }

        int infectionAmount = 20;

        for (int i = nearByEnemies.length; --i>=0; )
        {
            if (nearByEnemies[i].viperInfectedTurns < infectionAmount)
            {
                infectionAmount = nearByEnemies[i].viperInfectedTurns;
                weakest = nearByEnemies[i];
            }
        }

        return weakest;
    }

    /**
     * This method returns the best target from a list of enemies
     *
     * @param nearByEnemies
     * @param rc
     * @return
     */
    public static MapLocation getBestTurretTarget(RobotInfo[] nearByEnemies, RobotController rc)
    {
        MapLocation target = null;

        if (nearByEnemies.length > 0)
        {
            double weakestHealth = 999;
            for (int i = 0; i < nearByEnemies.length; i++)
            {
                MapLocation enemy = nearByEnemies[i].location;
                if (rc.getLocation().distanceSquaredTo(enemy) > 5)
                {
                    double enemyHealth = nearByEnemies[i].health;

                    // we want to hit enemy with highest health that we can kill with one shot
                    if (enemyHealth <= RobotType.TURRET.attackPower && weakestHealth < RobotType.TURRET.attackPower)
                    {
                        if (weakestHealth < enemyHealth)
                        {
                            weakestHealth = enemyHealth;
                            target = enemy;
                        }
                    }
                    // other wise we kill the weak
                    else if (weakestHealth > enemyHealth)
                    {
                        weakestHealth = enemyHealth;
                        target = enemy;
                    }
                }
            }
        }

        return target;
    }

    /**
     * This method gets the best location for a turret to attack
     *
     * @param nearByEnemies
     * @param rc
     * @return
     */
    public static MapLocation getTurretAttackPoint(RobotInfo[] nearByEnemies, RobotController rc, Communication[] communications)
    {
        MapLocation target = getBestTurretTarget(nearByEnemies, rc);

        // search signals for scouts telling us locations as well as
        // for enemy broadcasts
        if (target == null)
        {
            MapLocation loc = rc.getLocation();
            int dist = rc.getType().attackRadiusSquared;
            double coreDelay = 0;

            for (int i = communications.length; --i>=0; )
            {
                if (communications[i].opcode == CommunicationType.TURRET_SUPPORT || communications[i].opcode == CommunicationType.OENEMY)
                {
                    int[] cords = communications[i].getValues();
                    MapLocation enemy = new MapLocation(cords[2], cords[3]);
                    if (enemy.distanceSquaredTo(loc) <= dist && cords[1] > coreDelay)
                    {
                        target = enemy;
                        coreDelay = cords[1];
                    }
                }
            }
        }

        return target;
    }

    public static Direction getDir(RobotController rc, MapLocation target)
    {
        if (target == null)
            return Direction.NONE;

        return rc.getLocation().directionTo(target);
    }

    public static void moveDir(RobotController rc, Direction dir, boolean clearRubble) throws GameActionException
    {
        if (rc.isCoreReady())
        {
            if (rc.canMove(dir))
            {
                rc.move(dir);
            }
            else if (rc.canMove(dir.rotateLeft()))
            {
                rc.move(dir.rotateLeft());
            }
            else if (rc.canMove(dir.rotateRight()))
            {
                rc.move(dir.rotateRight());
            }
            else if (clearRubble && rc.senseRubble(rc.getLocation().add(dir)) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            {
                rc.clearRubble(dir);
            }
            else if (clearRubble && rc.senseRubble(rc.getLocation().add(dir.rotateLeft())) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            {
                rc.clearRubble(dir.rotateLeft());
            }
            else if (clearRubble && rc.senseRubble(rc.getLocation().add(dir.rotateRight())) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            {
                rc.clearRubble(dir.rotateRight());
            }
        }
    }
}
