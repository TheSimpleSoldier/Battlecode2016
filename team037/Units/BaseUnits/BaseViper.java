package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Unit;

public class BaseViper extends Unit
{
    public BaseViper(RobotController rc)
    {
        super(rc);
        archonDistressComs = false;
        rc.setIndicatorString(0, "Base ARchon");
    }

    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByEnemies);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByZombies);
    }

    public boolean precondition()
    {
        return !rc.isCoreReady() && !rc.isWeaponReady();
    }
}
