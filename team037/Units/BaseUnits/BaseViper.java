package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Unit;

/**
 * Base extension of the Unit class for units of RobotType VIPER.
 */
public class BaseViper extends Unit
{
    // Constructor
    public BaseViper(RobotController rc)
    {
        super(rc);
        archonDistressComs = false;
        rc.setIndicatorString(0, "Base Archon");
    }

    /**
     * Viper-specific implementations of abstract methods required by extension of class Unit.
     */

    @Override // takeNextStep() in class Unit by contract of extension.
    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }
    @Override // fight() in class Unit by contract of extension. Uses basic fight micro.
    public boolean fight() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByEnemies);
    }
    @Override // fightZombies() in class Unit by contract of extension. Uses basic fight micro.
    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.basicFightMicro(nearByZombies);
    }
    @Override // precondition() in class Unit. Return true if we cannot move and cannot shoot.
    public boolean precondition()
    {
        return !rc.isCoreReady() && !rc.isWeaponReady();
    }
}
