package team037;

import battlecode.common.*;

public class FightMicro
{
    public static RobotController rc;

    public FightMicro(RobotController robotController)
    {
        rc = robotController;
    }

    public boolean basicFightMicro(RobotInfo[] nearByEnemies) throws GameActionException
    {
        if (nearByEnemies != null && nearByEnemies.length > 0)
        {
            for (int i = 0; i < nearByEnemies.length; i++)
            {
                if (rc.canAttackLocation(nearByEnemies[i].location))
                {
                    rc.attackLocation(nearByEnemies[i].location);
                    return true;
                }
            }
        }

        return false;
    }

    // TODO: create fight micro using a trained neural net

}
