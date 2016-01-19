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

       if (rallyPoint != null)
       {
           rc.setIndicatorLine(currentLocation, rallyPoint, 0, 0, 255);
           rc.setIndicatorString(1, "Going to new rally point");
           turtlePoint = rallyPoint;
           setTargetLoc(turtlePoint);
       }
    }
}
