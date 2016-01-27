package team037.Units.BaseUnits;

import team037.Unit;
import battlecode.common.*;

public class BaseSoldier extends Unit
{
    public BaseSoldier(RobotController rc)
    {
        super(rc);
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return false;
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.soldierZombieFightMicro(zombies, nearByZombies, allies);
    }

    public boolean precondition()
    {
        return !rc.isCoreReady() && !rc.isWeaponReady();
    }
}
