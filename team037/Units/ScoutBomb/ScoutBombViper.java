package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseViper;

public class ScoutBombViper extends BaseViper {

    private boolean rushing = false;
    private MapLocation lastTarget = null;
    private MapLocation[] updatedLocs;
    private int currentIndex = -1;
    private int dist = Integer.MAX_VALUE;

    private static int closestFast;
    private static int closestAlly;
    private static RobotInfo closestAllyInfo;
    private static int numScouts;

    private static boolean nonScoutEnemies;

    public ScoutBombViper(RobotController rc) {
        super(rc);

        rushTarget = enemyArchonStartLocs[0];

        dist = (int) Math.sqrt(currentLocation.distanceSquaredTo(rushTarget));
        dist = dist / 2;
        dist = dist*dist;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();
        if (navigator.getTarget() == null) {
            navigator.setTarget(enemyArchonStartLocs[0]);
        }

        if (currentLocation != null && rushTarget != null) {
            if (currentLocation.distanceSquaredTo(rushTarget) < dist) {
                rushing = true;
            } else {
                rushing = false;
            }
        }

        //zombies
        closestFast = Integer.MAX_VALUE;
        for (int i = zombies.length; --i >= 0;) {
            if (zombies[i].type.equals(RobotType.FASTZOMBIE)) {
                int dist = zombies[i].location.distanceSquaredTo(currentLocation);
                if (dist < closestFast) {
                    closestFast = dist;
                }

            }

        }

        //allies
        closestAlly = Integer.MAX_VALUE;
        closestAllyInfo = null;
        numScouts = 0;
        for (int i = allies.length; --i >= 0;) {
            int dist = allies[i].location.distanceSquaredTo(currentLocation);
            if (dist < closestAlly) {
                closestAlly = dist;
                closestAllyInfo = allies[i];
            }
            if (allies[i].type.equals(RobotType.SCOUT)) {
                numScouts += 1;
            }
        }

        // enemies
        nonScoutEnemies = false;
        for (int i = enemies.length; --i >= 0;) {
            if (!enemies[i].type.equals(RobotType.SCOUT)) {
                nonScoutEnemies = true;
            }
        }

        if (closestFast < closestAlly || closestFast < 16 ) {
            if (rushing) {
                if (currentLocation.distanceSquaredTo(enemyArchonStartLocs[0]) < currentLocation.distanceSquaredTo(start) ) {
                    if (rc.isWeaponReady()) {
                        rc.attackLocation(currentLocation);
                        rc.disintegrate();
                    }
                } else {
                    rc.disintegrate();
                }
            }
        }
    }

    @Override
    public boolean act() throws GameActionException {
        if (rushing) {
            if (rc.isWeaponReady() && numScouts > 4) {
                int distToTarget = currentLocation.distanceSquaredTo(navigator.getTarget());
                for (int i = allies.length; --i >= 0;) {
                    if (allies[i].type.equals(RobotType.SCOUT) && allies[i].location.distanceSquaredTo(navigator.getTarget()) < distToTarget && rc.getViperInfectedTurns() == 0) {
                        rc.attackLocation(allies[i].location);
                        return true;
                    }
                }
            }
        }
        if (nonScoutEnemies && rushing) {
            return fightMicro.aggressiveFightMicro(nearByEnemies, enemies, nearByAllies);
        } else if (nonScoutEnemies) {
            return super.fight();
        }
        if (rushing && numScouts > 10) {
            return true;
        }
        return navigator.takeNextStep();
    }


    @Override
    public boolean fight() throws GameActionException
    {
        if (rushing)
        {
            return fightMicro.aggressiveFightMicro(nearByEnemies, enemies, nearByAllies);
        }
        return super.fight();
    }

    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (rushing)
            return false;

        return super.fightZombies();
    }
}
