package team037.Units;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationArray;
import team037.Unit;

public class CastleTurret extends BaseTurret
{
    AppendOnlyMapLocationArray enemyBroadcasingLocations;
    public CastleTurret(RobotController rc)
    {
        super(rc);
    }

    @Override
    public void handleMessages() throws GameActionException {
        Signal[] messages = rc.emptySignalQueue();
        enemyBroadcasingLocations = new AppendOnlyMapLocationArray();
        for (int i = Math.min(messages.length, 250); --i>=0;) {
            if (messages[i].getTeam().equals(opponent)) {
                if (currentLocation.distanceSquaredTo(messages[i].getLocation()) <= type.attackRadiusSquared) {
                    enemyBroadcasingLocations.add(messages[i].getLocation());
                }
            }
        }
    }

    @Override
    public boolean act() throws GameActionException {
        return fight();
    }

}
