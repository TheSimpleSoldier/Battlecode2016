package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Unit;

public class BaseGaurd extends Unit
{
    public BaseGaurd(RobotController rc)
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
        return fightMicro.guardZombieMicro(zombies, nearByZombies, allies);
    }

    public boolean precondition()
    {
        return !rc.isCoreReady() && !rc.isWeaponReady();
    }
}
