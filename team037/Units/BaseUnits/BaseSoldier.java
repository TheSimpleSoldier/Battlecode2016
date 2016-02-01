package team037.Units.BaseUnits;

import team037.Unit;
import battlecode.common.*;

/**
 * Base extension of the Unit class for units of RobotType SOLDIER.
 */
public class BaseSoldier extends Unit
{
    // Constructor
    public BaseSoldier(RobotController rc)
    {
        super(rc);
    }

    /**
     * Soldier-specific implementations of abstract methods required by extension of class Unit.
     */
    @Override // takeNextStep() in class Unit by contract of extension.
    public boolean takeNextStep() throws GameActionException
    {
        return navigator.takeNextStep();
    }
    @Override // fight() in class Unit by contract of extension. Uses fight micro of a trained neural net.
    public boolean fight() throws GameActionException
    {
        return fightMicro.basicNetFightMicro(nearByEnemies, nearByAllies, enemies, allies, target);
    }
    @Override // fightZombies() in class Unit by contract of extension. Uses zombie-specific fight micro.
    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.soldierZombieFightMicro(zombies, nearByZombies, allies);
    }
    @Override // precondition() in class Unit. Return true if we cannot move and cannot shoot.
    public boolean precondition()
    {
        return !rc.isCoreReady() && !rc.isWeaponReady();
    }
}
