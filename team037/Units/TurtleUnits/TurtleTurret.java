package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseTurret;
import team037.Utilites.MapUtils;
import team037.Utilites.PartsUtilities;

public class TurtleTurret extends BaseTurret
{
    private boolean updatedTurtleSpot = false;

    public TurtleTurret(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);
        setTargetLoc(turtlePoint);
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

        if (rc.getRoundNum() > 500 && !updatedTurtleSpot)
        {
            updatedTurtleSpot = true;
            turtlePoint = turtlePoint.add(Direction.NORTH, 10);
            setTargetLoc(turtlePoint);
        }

    }
}
