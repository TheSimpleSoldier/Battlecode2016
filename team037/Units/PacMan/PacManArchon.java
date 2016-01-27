package team037.Units.PacMan;

import battlecode.common.*;
import team037.DataStructures.BuildOrder;
import team037.DataStructures.SortedParts;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.Messages.*;
import team037.Navigation;
import team037.ScoutMapKnowledge;
import team037.Unit;
import team037.Units.BaseUnits.BaseArchon;
import team037.Utilites.BuildOrderCreation;
import team037.Utilites.MapUtils;
import team037.Utilites.Utilities;

/**
 * PacMan bot runs away. That's it.
 * Created by davej on 1/13/2016.
 */
public class PacManArchon extends BaseArchon implements PacMan {

    // These are the weights.
    static final double[][] PACMAN_WEIGHTS = new double[][]
            {
                    {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
                    {1, .25, .333333, .5, .5},  // enemy weights (enemies in sensor range)
                    {-8, -4, -2, -1, 0},            // target constants (attract towards target)
                    {1, .5, .5, .5, .5},        // parts weights (move towards parts locations in sensor range)
            };

    public RobotInfo[] myScouts;
    public int scoutCount;
    private BuildOrder buildOrder;
    Bots nextBot;
    RobotType nextType;
    RobotInfo[] neutralBots;
    public static SortedParts sortedParts = new SortedParts();
    public static ScoutMapKnowledge mKnowledge = new ScoutMapKnowledge();

    public PacManArchon(RobotController rc) {
        super(rc);
        buildOrder = BuildOrderCreation.createBuildOrder();
        nextBot = buildOrder.nextBot();
        nextType = Bots.typeFromBot(nextBot);
        myScouts = new RobotInfo[5];
        scoutCount = 0;
    }

    @Override
    public boolean aidDistressedArchon() {
        return false;
    }

    @Override
    public boolean act() throws GameActionException {
        boolean ability = carryOutAbility();
        if (!locationLastTurn.equals(currentLocation)) {
            Navigation.map.scan(currentLocation);
            Navigation.lastScan = currentLocation;
        }

        if (sortedParts.contains(currentLocation)) {
            sortedParts.remove(sortedParts.getIndexOfMapLocation(currentLocation));
//            Communication communication = new BotInfoCommunication();
//            communication.setValues(new int[]{CommunicationType.toInt(CommunicationType.GOING_AFTER_PARTS), Utilities.intFromType(type), Utilities.intFromTeam(us), id, currentLocation.x, currentLocation.y});
//            communicator.sendCommunication(400, communication);
        }

        if (updateTarget()) {
            navigator.setTarget(sortedParts.getBestSpot(currentLocation));
        }

        return ability || fightZombies() || runAway(null,true,true);
    }

    public boolean takeNextStep() throws GameActionException {
        return navigator.takeNextStep();
    }


    public boolean fight() throws GameActionException {
        // Call vipers
        return false;
    }

    public boolean fightZombies() throws GameActionException {
        // No need to fight zombies if there aren't any
        if (zombies == null || zombies.length == 0 || !rc.isCoreReady()) {
            return false;
        }

        return runAway(null,true,true);
    }
    public int[] applyAdditionalConstants(int[] directions) {

        MapLocation[] myArchons = alliedArchonStartLocs;
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
        directions = PacManUtils.applyWeights(currentLocation, directions, allies, new double[]{-.5,-.5,-.5,-.5,-.5});
        return directions;
    }
}
