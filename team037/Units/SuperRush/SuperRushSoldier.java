package team037.Units.SuperRush;

import _teamSeedingTournament.Units.BaseUnits.BaseSoldier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/**
 * Created by joshua on 1/22/16.
 */
public class SuperRushSoldier extends BaseSoldier
{

    public SuperRushSoldier(RobotController rc)
    {
        super(rc);
    }

    @Override
    public boolean updateTarget() throws GameActionException
    {
        navigator.setTarget(currentLocation.add(Direction.EAST, 7));
        return true;
    }
}
