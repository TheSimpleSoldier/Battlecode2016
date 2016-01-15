package team037.DataStructures;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Team;

/**
 * Created by joshua on 1/13/16.
 */
public class SimpleRobotInfo
{
    public int id;
    public MapLocation location;
    public RobotType type;
    public Team team;

    public SimpleRobotInfo(int id, MapLocation location, RobotType type, Team team)
    {
        this.id = id;
        this.location = location;
        this.type = type;
        this.team = team;
    }
}
