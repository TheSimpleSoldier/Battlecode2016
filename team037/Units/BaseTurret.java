package team037.Units;

import battlecode.common.*;
import team037.Messages.Communication;
import team037.Unit;

public class BaseTurret extends Unit
{
    public BaseTurret(RobotController rc)
    {
        super(rc);
    }

    // Turrets don't move
    public boolean takeNextStep() throws GameActionException
    {
        return false;
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.turretFightMicro(nearByEnemies, nearByZombies, enemies, allies, target, communications);
    }

    // zombie fight micro happens in fight()
    public boolean fightZombies() throws GameActionException
    {
        return false;
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
