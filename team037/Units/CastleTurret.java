package team037.Units;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.Unit;

public class CastleTurret extends BaseTurret
{
    AppendOnlyMapLocationArray enemyBroadcasingLocations;
    public CastleTurret(RobotController rc)
    {
        super(rc);
    }

    @Override
    public void handleMessages() throws GameActionException {
        Signal[] messages = rc.emptySignalQueue();
        enemyBroadcasingLocations = new AppendOnlyMapLocationArray();
        for (int i = Math.min(messages.length, 250); --i>=0;) {
            if (messages[i].getTeam().equals(opponent)) {
                if (currentLocation.distanceSquaredTo(messages[i].getLocation()) <= type.attackRadiusSquared) {
                    enemyBroadcasingLocations.add(messages[i].getLocation());
                }
            }
        }
    }

    @Override
    public boolean act() throws GameActionException {
        return fight();
    }

    public boolean fight() throws GameActionException {
        if (rc.getWeaponDelay() >= 1) {
            return false;
        }
        if (nearByEnemies.length == 0 && nearByZombies.length == 0 && enemyBroadcasingLocations.length == 0) {
            return false;
        }

        // try attacking enemies or zombies
        if (nearByEnemies.length > 0 && tryAttack(nearByEnemies)) {
            return true;
        } else if (nearByZombies.length > 0 && tryAttack(nearByZombies)) {
            return true;
        }

        if (enemyBroadcasingLocations.length > 0 && rc.canAttackLocation(enemyBroadcasingLocations.array[0])) {
            rc.attackLocation(enemyBroadcasingLocations.array[0]);
            return true;
        }

        return false;
    }

    private boolean tryAttack(RobotInfo[] enemies) throws GameActionException {
        double lowestHP = Integer.MAX_VALUE;
        MapLocation toAttack = null;
        for (int i = enemies.length; --i >= 0;) {
            int distance = currentLocation.distanceSquaredTo(enemies[i].location);
            if(distance <= type.attackRadiusSquared && distance >= GameConstants.TURRET_MINIMUM_RANGE && enemies[i].health < lowestHP) {
                lowestHP = enemies[i].health;
                toAttack = enemies[i].location;
            }
        }
        if (toAttack != null) {
            rc.attackLocation(toAttack);
            return true;
        }
        return false;
    }



}
