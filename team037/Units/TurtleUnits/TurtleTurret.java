package team037.Units.TurtleUnits;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team037.Units.BaseUnits.BaseTurret;
import team037.Utilites.MapUtils;

public class TurtleTurret extends BaseTurret
{
    public TurtleTurret(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);
        setTargetLoc(turtlePoint);
    }
}
