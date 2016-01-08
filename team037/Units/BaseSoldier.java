package team037.Units;

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
        if (target == null || rc.getLocation() == target)
        {
            target = rc.getLocation().add(dirs[(int) (Math.random() * 8)], 5);
            navigator.setTarget(target);
        }
        return navigator.takeNextStep();
    }

    public void handleMessages() throws GameActionException
    {
        Signal signal = rc.readSignal();

        rc.setIndicatorString(0, "Messages: " + (signal != null));
        while (signal != null) {
            if (signal.getTeam() == rc.getTeam())
            {
                int msg = signal.getMessage()[0];
                int msg2 = signal.getMessage()[1];
                rc.setIndicatorString(1, "recieved msg from team: " + msg + " msg2: " + msg2);
                if (msg == msg2 && msg > 0)
                {
                    target = new MapLocation(msg / 100000, msg % 100000);
                    rc.setIndicatorString(1, "Found Archon");
                    rc.setIndicatorString(2, "x: " + target.x + " y: " + target.y);
                    break;
                }
            }
            signal = rc.readSignal();
        }
    }

    public boolean fight() throws GameActionException
    {
        return fightMicro.basicNetFightMicro(nearByEnemies, nearByAllies, enemies, allies, target);
    }

    public boolean fightZombies() throws GameActionException
    {
        return fightMicro.basicNetZombieFightMicro(nearByZombies, nearByAllies, zombies, allies, target);
    }

    public boolean carryOutAbility() throws GameActionException
    {
        return false;
    }
}
