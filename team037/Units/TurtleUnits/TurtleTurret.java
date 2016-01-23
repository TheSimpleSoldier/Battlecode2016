package team037.Units.TurtleUnits;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseTurret;
import team037.Utilites.MapUtils;

public class TurtleTurret extends BaseTurret
{
    public TurtleTurret(RobotController rc)
    {
        super(rc);
        turtlePoint = MapUtils.getTurtleSpot(alliedArchonStartLocs);

        try
        {
            setTargetLoc(MapUtils.getClosestUnoccupiedSquareCheckeredBoard(currentLocation, turtlePoint));
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
           rc.setIndicatorString(1, "Going to new rally point x: " + rallyPoint.x + " y: " + rallyPoint.y);
           turtlePoint = rallyPoint;
           setTargetLoc(MapUtils.getClosestUnoccupiedSquareCheckeredBoard(currentLocation, turtlePoint));

           rallyPoint = null;
       }
    }
}
