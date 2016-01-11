package team037.Units;

import battlecode.common.RobotController;

public class RegionScout extends BaseScout
{
    public RegionScout(RobotController rc)
    {
        super(rc);
        rc.setIndicatorString(0, "Region Scout");
        System.out.println("Region scout");
    }
}
