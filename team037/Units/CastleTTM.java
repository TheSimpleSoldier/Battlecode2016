package team037.Units;

import battlecode.common.*;
import battlecode.common.RobotController;
import team037.Unit;

public class CastleTTM extends BaseTTM
{
    public CastleTTM(RobotController rc) {
        super(rc);
    }

    @Override
    public boolean act() throws GameActionException {
        if (!rc.isCoreReady()) {
            rc.unpack();
            return true;
        }
        return false;
    }


}
