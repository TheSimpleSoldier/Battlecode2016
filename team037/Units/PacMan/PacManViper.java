package team037.Units.PacMan;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Units.Rushers.RushingViper;

/**
 * Created by davej on 1/13/2016.
 */
public class PacManViper extends RushingViper implements PacMan {
    public PacManViper(RobotController rc) {
        super(rc);
    }


    @Override
    public boolean fightZombies() throws GameActionException
    {
        if (zombies == null && zombies.length == 0 || zombies.length == 1 && zombies[0].type.equals(RobotType.ZOMBIEDEN)) {
            return false;
        }
        return runAway(null);
    }

    /**
     * Add additional constants to push the viper towards enemy Archons AND away from allied Archons
     * @param directions
     * @param weights
     * @return
     */
    public int[] applyAdditionalConstants(int[] directions, double[][] weights) {

        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
        if (myArchons == null) {
            myArchons = rc.getInitialArchonLocations(us);
        }
        directions = applyConstants(currentLocation,directions,myArchons,new double[]{8,4,2,0,0});

        MapLocation[] badArchons = mapKnowledge.getArchonLocations(false);
        if (badArchons == null) {
            badArchons = rc.getInitialArchonLocations(us);
        }
        directions = applyConstants(currentLocation,directions,badArchons,new double[]{-16,-8,-4,0,0});

        return directions;
    }
}