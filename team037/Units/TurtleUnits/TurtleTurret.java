package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseTurret;
import team037.Utilites.MapUtils;
import team037.Utilites.MoveUtils;

public class TurtleTurret extends BaseTurret
{
    public TurtleTurret(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);

        try
        {
            MapLocation target = MapUtils.getClosestUnoccupiedSquareCheckeredBoard(currentLocation, turtlePoint);
            if (rc.senseParts(target) == 0) {
                setTargetLoc(target);
            } else {
                setTargetLoc(turtlePoint);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            setTargetLoc(turtlePoint);
        }
    }

    @Override
    public boolean fight() throws GameActionException
    {
        boolean value = super.fight();

        if (value)
        {
            communicator.forceSendSimpleCommunication(sightRange * 2);
        }

        return value;
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();

       if (rallyPoint != null)
       {
           rc.setIndicatorLine(currentLocation, rallyPoint, 0, 0, 255);

           // only look for a new location if we've moved rally points
           if (!rallyPoint.equals(turtlePoint)) {
               turtlePoint = rallyPoint;
               setTargetLoc(MapUtils.getClosestUnoccupiedSquareCheckeredBoard(currentLocation, turtlePoint));
           }

           rallyPoint = null;
       }
    }
}
