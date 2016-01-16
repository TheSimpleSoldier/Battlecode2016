package team037.Units.ScoutBomb;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.FlyingNavigator;
import team037.Messages.*;
import team037.ScoutMapKnowledge;
import team037.SlugNavigator;
import team037.Unit;
import team037.Units.BaseUnits.BaseScout;
import team037.Utilites.PartsUtilities;

public class ScoutBombScout extends BaseScout
{
    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();

    public ScoutBombScout(RobotController rc)
    {
        super(rc);
        mapKnowledge = mKnowledge;
        navigator = new SlugNavigator(rc);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.avoidEnemiesInRoute(enemies, navigator.getTarget());
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.avoidEnemiesInRoute(zombies, navigator.getTarget());
    }

    public boolean precondition()
    {
        return !rc.isCoreReady();
    }
}
