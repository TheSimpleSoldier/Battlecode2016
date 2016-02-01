package team037.Units.BaseUnits;

import battlecode.common.*;
import team037.Unit;

/**
 * Base extension of the Unit class for units of RobotType GUARD.
 */
public class BaseGuard extends Unit
{
    public BaseGuard(RobotController rc)
    {
        super(rc);
    }

    /**
     * Guard-specific implementations of abstract methods required by extension of class Unit.
     */
    @Override // takeNextStep() in class Unit by contract of extension
    public boolean takeNextStep() throws GameActionException
    {
        // Try to move
        return navigator.takeNextStep();
    }
    @Override // fight() in class Unit by contract of extension
    public boolean fight() throws GameActionException
    {
        // Use FightMicro designed for guards against enemy units.
        return fightMicro.basicGuardMicro(enemies, nearByEnemies, allies);
    }
    @Override // fightZombies() in class Unit by contract of extension.
    public boolean fightZombies() throws GameActionException
    {
        // Use FightMicro designed for guards against zombie units.
        return fightMicro.guardZombieMicro(zombies, nearByZombies, allies);
    }
    @Override // precondition() in class Unit. Return true if we cannot move and cannot shoot.
    public boolean precondition()
    {
        return !rc.isCoreReady() && !rc.isWeaponReady();
    }
}
