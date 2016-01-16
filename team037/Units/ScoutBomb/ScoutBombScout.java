package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.ScoutMapKnowledge;
import team037.SlugNavigator;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.MapUtils;

public class ScoutBombScout extends BaseScout
{
    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();

    public ScoutBombScout(RobotController rc)
    {
        super(rc);
        mapKnowledge = mKnowledge;
        navigator = new SlugNavigator(rc);
    }

    @Override
    public boolean updateTarget() throws GameActionException {
        boolean enemySide = true;
        if (currentLocation.distanceSquaredTo(MapUtils.getCenterOfMass(alliedArchonStartLocs)) < currentLocation.distanceSquaredTo(MapUtils.getCenterOfMass(enemyArchonStartLocs))) {
            enemySide = false;
        }
        if (zombies.length > 0) {
            if (rc.getZombieInfectedTurns() < 5) {

            }
            
        }
        return false;

    }


    @Override
    public void sendMessages() {

    }

    @Override
    public boolean fight() throws GameActionException
    {
        return false;
    }

    public boolean fightZombies() throws GameActionException
    {
        return false;
    }

    public boolean precondition()
    {
        return !rc.isCoreReady();
    }
}
