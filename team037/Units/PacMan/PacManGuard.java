package team037.Units.PacMan;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseGaurd;

/**
 * Created by davej on 1/15/2016.
 */
public class PacManGuard extends BaseGaurd implements PacMan {

    public PacManGuard(RobotController rc) {
        super(rc);
    }

    public boolean fightZombies() throws GameActionException {
        // No need to fight zombies if there aren't any
        if (rc.isArmageddon() || zombies == null || zombies.length == 0) {
            return runAway(null,false,true);
        }

        return fightMicro.guardZombieMicro(zombies,nearByZombies,allies);
    }

    public boolean takeNextStep() {
        if (allies == null || allies.length < 1) {
            return false;
        }

        return runAway(null);
    }

    public int[] applyAdditionalConstants(int[] directions) {

        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
        if (myArchons == null) {
            myArchons = rc.getInitialArchonLocations(us);
        }

        if (rc.isArmageddon()) {
            directions = PacManUtils.applySimpleConstants(currentLocation, directions, myArchons, new int[]{32, 16, 8});
        } else {
            directions = PacManUtils.applySimpleConstants(currentLocation, directions, myArchons, new int[]{-32, -16, -8});
        }

        return directions;
    }

    public int[] applyAdditionalWeights(int[] directions) {

        if (rc.isArmageddon()) {
            directions = PacManUtils.applyWeights(currentLocation, directions, allies, DEFAULT_WEIGHTS[ENEMIES]);
        } else {
            directions = PacManUtils.applyWeights(currentLocation, directions, allies, DEFAULT_WEIGHTS[NEUTRALS_AND_PARTS]);
        }
        return directions;
    }
}
