package team037.Units.CastleUnits;

import battlecode.common.*;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseTTM;

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
