package team037.Units.BaseUnits;

import battlecode.common.*;
import battlecode.common.RobotController;
import team037.Unit;

public class BaseTTM extends Unit
{
    public BaseTTM(RobotController rc)
    {
        super(rc);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.runPassiveFightMicro(enemies, nearByAllies, allies, target, nearByEnemies);
    }

    public boolean precondition()
    {
        return !rc.isCoreReady();
    }
}
