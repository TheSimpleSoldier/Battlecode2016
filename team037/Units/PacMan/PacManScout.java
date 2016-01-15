package team037.Units.PacMan;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team037.Units.HerdingScout;

/**
 * PacManArchon runs away. PacManScout escorts PacManArchon.
 * Created by davej on 1/13/2016.
 */
public class PacManScout extends HerdingScout {
    RobotInfo myArchon;

    public PacManScout(RobotController rc) {
        super(rc);
        nearByAllies = rc.senseNearbyRobots(2,us);
        for (int i = nearByAllies.length; --i >= 0;) {
            if (nearByAllies[i].type.equals(RobotType.ARCHON)) {
                myArchon = nearByAllies[i];
                break;
            }
        }
    }

    public MapLocation getTargetToWait()
    {
        MapLocation loc = mapKnowledge.closestDen(currentLocation);
        if(loc == null)
        {
            loc = currentLocation.add(currentLocation.directionTo(getTargetForHerding()), 5);
        }
        return loc;
    }
}
