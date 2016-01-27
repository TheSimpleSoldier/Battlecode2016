package team037.Units.CastleUnits;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.Units.BaseUnits.BaseTurret;

public class CastleTurret extends BaseTurret
{
    AppendOnlyMapLocationArray enemyBroadcasingLocations;
    public CastleTurret(RobotController rc)
    {
        super(rc);
    }

    @Override
    public boolean act() throws GameActionException {
        return fight();
    }

}
