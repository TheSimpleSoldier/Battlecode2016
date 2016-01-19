package team037.Units.PacMan;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseGaurd;

/**
 * Created by davej on 1/15/2016.
 */
public class PacManGuard extends BaseGaurd implements PacMan {
    static RobotInfo myArchon = null;
    static RobotInfo badArchon = null;

    // These are the weights.
    static final double[][] PACMAN_WEIGHTS = new double[][]
            {
                    {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
                    {1, .5, .5, .5, .5},        // enemy weights (enemies in sensor range)
                    {-8, -4, -2, -1, 0},        // target constants (attract towards target)
                    {1, .5, .25, .5, 1},            // Archon weights (repel from friendly Archons moving by us)
                    {1, .5, .5, .5, .5},          // friendly unit weights (friendlies in sensor range)
            };

    public PacManGuard(RobotController rc) {
        super(rc);
        myArchon = findArchon(allies);
        if (myArchon != null) {
            navigator.setTarget(myArchon.location);
        } else {
            navigator.setTarget(currentLocation);
        }
    }

    public boolean fight() throws GameActionException {
        if (enemies == null || enemies.length == 0 || badArchon == null || myArchon == null) {
            return false;
        }
        return fightMicro.basicNetFightMicro(nearByEnemies, nearByAllies, enemies, allies, target);
    }


    public boolean fightZombies() throws GameActionException {
        // No need to fight zombies if there aren't any
        if (zombies == null || zombies.length == 0) {
            if (updateTarget()) {
                navigator.setTarget(getNextSpot());
            }
            return false;
        }
        if (updateTarget()) {
            navigator.setTarget(getNextSpotOffensive());
        }
        try {
            return fightMicro.guardZombieMicro(zombies, nearByZombies, allies);
        } catch (Exception e) {
            return runAway(PACMAN_WEIGHTS);
        }
    }

    public boolean takeNextStep() throws GameActionException {
        if (allies != null && allies.length > 2) {
            return runAway(PACMAN_WEIGHTS);
        }

        return navigator.takeNextStep();
    }

    public int[] applyAdditionalWeights(int[] directions, double[][] weights) {

        if (myArchon != null) {
            directions = applyArchonWeights(directions, myArchon, weights[3]);
        } else {
            directions = applyAlliedWeights(directions, allies, weights[4]);
        }

        return directions;
    }

    public boolean updateTarget() throws GameActionException {
        if (target == null || currentLocation.equals(navigator.getTarget())) {
            return true;
        } else if (myArchon != null) {
            return true;
        }
        return false;
    }

    public MapLocation getNextSpot() throws GameActionException {
        return getNextSpotDefensive();
    }

    public MapLocation getNextSpotDefensive() {
        MapLocation newTarget = start;
        if (myArchon != null) {
            newTarget = myArchon.location;
        } else {
            try {
                MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
                if (myArchons != null && myArchons.length > 0) {
                    int min = 9999999;
                    for (int i = myArchons.length; --i >= 0; ) {
                        if (myArchons[i].distanceSquaredTo(currentLocation) < min) {
                            newTarget = myArchons[i];
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return newTarget;
    }


    public MapLocation getNextSpotOffensive() throws GameActionException {
        MapLocation newTarget = currentLocation;
        if (badArchon != null) {
            newTarget = badArchon.location;
        } else {
            try {
                MapLocation[] badArchons = rc.getInitialArchonLocations(opponent);
                if (badArchons != null && badArchons.length > 0) {
                    int max = -1;
                    for (int i = badArchons.length; --i >= 0; ) {
                        if (badArchons[i].distanceSquaredTo(currentLocation) > max) {
                            newTarget = badArchons[i];
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return newTarget;
    }

    public boolean precondition() {
        return false;
    }

    public boolean aidDistressedArchon() throws GameActionException {
        return false;
    }

    //    public void handleMessages() throws GameActionException { }
    public void sendMessages() {
        return;
    }

    public void collectData() throws GameActionException {
        super.collectData();
        myArchon = findArchon(allies);
        badArchon = findArchon(enemies);
    }


    public RobotInfo findArchon(RobotInfo[] checkBots) {
        RobotType archon = RobotType.ARCHON;
        try {
            if (checkBots[0].type.equals(archon)) {
                return checkBots[0];
            }
            if (checkBots[1].type.equals(archon)) {
                return checkBots[1];
            }
            if (checkBots[2].type.equals(archon)) {
                return checkBots[2];
            }
            if (checkBots[3].type.equals(archon)) {
                return checkBots[3];
            }
            if (checkBots[4].type.equals(archon)) {
                return checkBots[4];
            }
            if (checkBots[5].type.equals(archon)) {
                return checkBots[5];
            }
            if (checkBots[6].type.equals(archon)) {
                return checkBots[6];
            }
            if (checkBots[7].type.equals(archon)) {
                return checkBots[7];
            }
            if (checkBots[8].type.equals(archon)) {
                return checkBots[8];
            }
            if (checkBots[9].type.equals(archon)) {
                return checkBots[9];
            }
            if (checkBots[10].type.equals(archon)) {
                return checkBots[10];
            }
            if (checkBots[11].type.equals(archon)) {
                return checkBots[11];
            }
            if (checkBots[12].type.equals(archon)) {
                return checkBots[12];
            }
            if (checkBots[13].type.equals(archon)) {
                return checkBots[13];
            }
            if (checkBots[14].type.equals(archon)) {
                return checkBots[14];
            }
            if (checkBots[15].type.equals(archon)) {
                return checkBots[15];
            }
            if (checkBots[16].type.equals(archon)) {
                return checkBots[16];
            }
            if (checkBots[17].type.equals(archon)) {
                return checkBots[17];
            }
            if (checkBots[18].type.equals(archon)) {
                return checkBots[18];
            }
            if (checkBots[19].type.equals(archon)) {
                return checkBots[19];
            }
            if (checkBots[20].type.equals(archon)) {
                return checkBots[20];
            }
            if (checkBots[21].type.equals(archon)) {
                return checkBots[21];
            }
            if (checkBots[22].type.equals(archon)) {
                return checkBots[22];
            }
            if (checkBots[23].type.equals(archon)) {
                return checkBots[23];
            }
            if (checkBots[24].type.equals(archon)) {
                return checkBots[24];
            }
            if (checkBots[25].type.equals(archon)) {
                return checkBots[25];
            }
            if (checkBots[26].type.equals(archon)) {
                return checkBots[26];
            }
            if (checkBots[27].type.equals(archon)) {
                return checkBots[27];
            }
            if (checkBots[28].type.equals(archon)) {
                return checkBots[28];
            }
            if (checkBots[29].type.equals(archon)) {
                return checkBots[29];
            }
            if (checkBots[30].type.equals(archon)) {
                return checkBots[30];
            }
            if (checkBots[31].type.equals(archon)) {
                return checkBots[31];
            }
            if (checkBots[32].type.equals(archon)) {
                return checkBots[32];
            }
            if (checkBots[33].type.equals(archon)) {
                return checkBots[33];
            }
            if (checkBots[34].type.equals(archon)) {
                return checkBots[34];
            }
            if (checkBots[35].type.equals(archon)) {
                return checkBots[35];
            }
            if (checkBots[36].type.equals(archon)) {
                return checkBots[36];
            }
            if (checkBots[37].type.equals(archon)) {
                return checkBots[37];
            }
            if (checkBots[38].type.equals(archon)) {
                return checkBots[38];
            }
            if (checkBots[39].type.equals(archon)) {
                return checkBots[39];
            }
            if (checkBots[40].type.equals(archon)) {
                return checkBots[40];
            }
            if (checkBots[41].type.equals(archon)) {
                return checkBots[41];
            }
            if (checkBots[42].type.equals(archon)) {
                return checkBots[42];
            }
            if (checkBots[43].type.equals(archon)) {
                return checkBots[43];
            }
            if (checkBots[44].type.equals(archon)) {
                return checkBots[44];
            }
            if (checkBots[45].type.equals(archon)) {
                return checkBots[45];
            }
            if (checkBots[46].type.equals(archon)) {
                return checkBots[46];
            }
            if (checkBots[47].type.equals(archon)) {
                return checkBots[47];
            }
            if (checkBots[48].type.equals(archon)) {
                return checkBots[48];
            }
            if (checkBots[49].type.equals(archon)) {
                return checkBots[49];
            }
            if (checkBots[50].type.equals(archon)) {
                return checkBots[50];
            }
            if (checkBots[51].type.equals(archon)) {
                return checkBots[51];
            }
            if (checkBots[52].type.equals(archon)) {
                return checkBots[52];
            }
            if (checkBots[53].type.equals(archon)) {
                return checkBots[53];
            }
            if (checkBots[54].type.equals(archon)) {
                return checkBots[54];
            }
            if (checkBots[55].type.equals(archon)) {
                return checkBots[55];
            }
            if (checkBots[56].type.equals(archon)) {
                return checkBots[56];
            }
            if (checkBots[57].type.equals(archon)) {
                return checkBots[57];
            }
            if (checkBots[58].type.equals(archon)) {
                return checkBots[58];
            }
            if (checkBots[59].type.equals(archon)) {
                return checkBots[59];
            }
            if (checkBots[60].type.equals(archon)) {
                return checkBots[60];
            }
            if (checkBots[61].type.equals(archon)) {
                return checkBots[61];
            }
            if (checkBots[62].type.equals(archon)) {
                return checkBots[62];
            }
            if (checkBots[63].type.equals(archon)) {
                return checkBots[63];
            }
            if (checkBots[64].type.equals(archon)) {
                return checkBots[64];
            }
            if (checkBots[65].type.equals(archon)) {
                return checkBots[65];
            }
            if (checkBots[66].type.equals(archon)) {
                return checkBots[66];
            }
            if (checkBots[67].type.equals(archon)) {
                return checkBots[67];
            }
            if (checkBots[68].type.equals(archon)) {
                return checkBots[68];
            }
            if (checkBots[69].type.equals(archon)) {
                return checkBots[69];
            }
            if (checkBots[70].type.equals(archon)) {
                return checkBots[70];
            }
            if (checkBots[71].type.equals(archon)) {
                return checkBots[71];
            }
            if (checkBots[72].type.equals(archon)) {
                return checkBots[72];
            }
            if (checkBots[73].type.equals(archon)) {
                return checkBots[73];
            }
            if (checkBots[74].type.equals(archon)) {
                return checkBots[74];
            }
            if (checkBots[75].type.equals(archon)) {
                return checkBots[75];
            }
            if (checkBots[76].type.equals(archon)) {
                return checkBots[76];
            }
            if (checkBots[77].type.equals(archon)) {
                return checkBots[77];
            }
            if (checkBots[78].type.equals(archon)) {
                return checkBots[78];
            }
            if (checkBots[79].type.equals(archon)) {
                return checkBots[79];
            }
            if (checkBots[80].type.equals(archon)) {
                return checkBots[80];
            }
            if (checkBots[81].type.equals(archon)) {
                return checkBots[81];
            }
            if (checkBots[82].type.equals(archon)) {
                return checkBots[82];
            }
            if (checkBots[83].type.equals(archon)) {
                return checkBots[83];
            }
            if (checkBots[84].type.equals(archon)) {
                return checkBots[84];
            }
            if (checkBots[85].type.equals(archon)) {
                return checkBots[85];
            }
            if (checkBots[86].type.equals(archon)) {
                return checkBots[86];
            }
            if (checkBots[87].type.equals(archon)) {
                return checkBots[87];
            }
            if (checkBots[88].type.equals(archon)) {
                return checkBots[88];
            }
            if (checkBots[89].type.equals(archon)) {
                return checkBots[89];
            }
            if (checkBots[90].type.equals(archon)) {
                return checkBots[90];
            }
            if (checkBots[91].type.equals(archon)) {
                return checkBots[91];
            }
            if (checkBots[92].type.equals(archon)) {
                return checkBots[92];
            }
            if (checkBots[93].type.equals(archon)) {
                return checkBots[93];
            }
            if (checkBots[94].type.equals(archon)) {
                return checkBots[94];
            }
            if (checkBots[95].type.equals(archon)) {
                return checkBots[95];
            }
            if (checkBots[96].type.equals(archon)) {
                return checkBots[96];
            }
            if (checkBots[97].type.equals(archon)) {
                return checkBots[97];
            }
            if (checkBots[98].type.equals(archon)) {
                return checkBots[98];
            }
            if (checkBots[99].type.equals(archon)) {
                return checkBots[99];
            }
            if (checkBots[100].type.equals(archon)) {
                return checkBots[100];
            }
            if (checkBots[101].type.equals(archon)) {
                return checkBots[101];
            }
            if (checkBots[102].type.equals(archon)) {
                return checkBots[102];
            }
            if (checkBots[103].type.equals(archon)) {
                return checkBots[103];
            }
            if (checkBots[104].type.equals(archon)) {
                return checkBots[104];
            }
            if (checkBots[105].type.equals(archon)) {
                return checkBots[105];
            }
            if (checkBots[106].type.equals(archon)) {
                return checkBots[106];
            }
            if (checkBots[107].type.equals(archon)) {
                return checkBots[107];
            }
            if (checkBots[108].type.equals(archon)) {
                return checkBots[108];
            }
            if (checkBots[109].type.equals(archon)) {
                return checkBots[109];
            }
            if (checkBots[110].type.equals(archon)) {
                return checkBots[110];
            }
            if (checkBots[111].type.equals(archon)) {
                return checkBots[111];
            }
            if (checkBots[112].type.equals(archon)) {
                return checkBots[112];
            }
            if (checkBots[113].type.equals(archon)) {
                return checkBots[113];
            }
            if (checkBots[114].type.equals(archon)) {
                return checkBots[114];
            }
            if (checkBots[115].type.equals(archon)) {
                return checkBots[115];
            }
            if (checkBots[116].type.equals(archon)) {
                return checkBots[116];
            }
            if (checkBots[117].type.equals(archon)) {
                return checkBots[117];
            }
            if (checkBots[118].type.equals(archon)) {
                return checkBots[118];
            }
            if (checkBots[119].type.equals(archon)) {
                return checkBots[119];
            }
            if (checkBots[120].type.equals(archon)) {
                return checkBots[120];
            }
            if (checkBots[121].type.equals(archon)) {
                return checkBots[121];
            }
            if (checkBots[122].type.equals(archon)) {
                return checkBots[122];
            }
            if (checkBots[123].type.equals(archon)) {
                return checkBots[123];
            }
            if (checkBots[124].type.equals(archon)) {
                return checkBots[124];
            }
            if (checkBots[125].type.equals(archon)) {
                return checkBots[125];
            }
            if (checkBots[126].type.equals(archon)) {
                return checkBots[126];
            }
            if (checkBots[127].type.equals(archon)) {
                return checkBots[127];
            }
            if (checkBots[128].type.equals(archon)) {
                return checkBots[128];
            }
            if (checkBots[129].type.equals(archon)) {
                return checkBots[129];
            }
            if (checkBots[130].type.equals(archon)) {
                return checkBots[130];
            }
            if (checkBots[131].type.equals(archon)) {
                return checkBots[131];
            }
            if (checkBots[132].type.equals(archon)) {
                return checkBots[132];
            }
            if (checkBots[133].type.equals(archon)) {
                return checkBots[133];
            }
            if (checkBots[134].type.equals(archon)) {
                return checkBots[134];
            }
            if (checkBots[135].type.equals(archon)) {
                return checkBots[135];
            }
            if (checkBots[136].type.equals(archon)) {
                return checkBots[136];
            }
            if (checkBots[137].type.equals(archon)) {
                return checkBots[137];
            }
            if (checkBots[138].type.equals(archon)) {
                return checkBots[138];
            }
            if (checkBots[139].type.equals(archon)) {
                return checkBots[139];
            }
            if (checkBots[140].type.equals(archon)) {
                return checkBots[140];
            }
            if (checkBots[141].type.equals(archon)) {
                return checkBots[141];
            }
            if (checkBots[142].type.equals(archon)) {
                return checkBots[142];
            }
            if (checkBots[143].type.equals(archon)) {
                return checkBots[143];
            }
            if (checkBots[144].type.equals(archon)) {
                return checkBots[144];
            }
            if (checkBots[145].type.equals(archon)) {
                return checkBots[145];
            }
            if (checkBots[146].type.equals(archon)) {
                return checkBots[146];
            }
            if (checkBots[147].type.equals(archon)) {
                return checkBots[147];
            }
            if (checkBots[148].type.equals(archon)) {
                return checkBots[148];
            }
            if (checkBots[149].type.equals(archon)) {
                return checkBots[149];
            }
            if (checkBots[150].type.equals(archon)) {
                return checkBots[150];
            }
            if (checkBots[151].type.equals(archon)) {
                return checkBots[151];
            }
            if (checkBots[152].type.equals(archon)) {
                return checkBots[152];
            }
            if (checkBots[153].type.equals(archon)) {
                return checkBots[153];
            }
            if (checkBots[154].type.equals(archon)) {
                return checkBots[154];
            }
            if (checkBots[155].type.equals(archon)) {
                return checkBots[155];
            }
            if (checkBots[156].type.equals(archon)) {
                return checkBots[156];
            }
            if (checkBots[157].type.equals(archon)) {
                return checkBots[157];
            }
            if (checkBots[158].type.equals(archon)) {
                return checkBots[158];
            }
            if (checkBots[159].type.equals(archon)) {
                return checkBots[159];
            }
            if (checkBots[160].type.equals(archon)) {
                return checkBots[160];
            }
            if (checkBots[161].type.equals(archon)) {
                return checkBots[161];
            }
            if (checkBots[162].type.equals(archon)) {
                return checkBots[162];
            }
            if (checkBots[163].type.equals(archon)) {
                return checkBots[163];
            }
            if (checkBots[164].type.equals(archon)) {
                return checkBots[164];
            }
            if (checkBots[165].type.equals(archon)) {
                return checkBots[165];
            }
            if (checkBots[166].type.equals(archon)) {
                return checkBots[166];
            }
            if (checkBots[167].type.equals(archon)) {
                return checkBots[167];
            }
            if (checkBots[168].type.equals(archon)) {
                return checkBots[168];
            }
            if (checkBots[169].type.equals(archon)) {
                return checkBots[169];
            }
            if (checkBots[170].type.equals(archon)) {
                return checkBots[170];
            }
            if (checkBots[171].type.equals(archon)) {
                return checkBots[171];
            }
            if (checkBots[172].type.equals(archon)) {
                return checkBots[172];
            }
            if (checkBots[173].type.equals(archon)) {
                return checkBots[173];
            }
            if (checkBots[174].type.equals(archon)) {
                return checkBots[174];
            }
            if (checkBots[175].type.equals(archon)) {
                return checkBots[175];
            }
            if (checkBots[176].type.equals(archon)) {
                return checkBots[176];
            }
            if (checkBots[177].type.equals(archon)) {
                return checkBots[177];
            }
            if (checkBots[178].type.equals(archon)) {
                return checkBots[178];
            }
            if (checkBots[179].type.equals(archon)) {
                return checkBots[179];
            }
            if (checkBots[180].type.equals(archon)) {
                return checkBots[180];
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public int[] applyArchonWeights(int[] directions, RobotInfo unit, double[] scalars) {

        if (unit == null) {
            return directions;
        }

        MapLocation nextUnit = unit.location;
        double add = (38 - nextUnit.distanceSquaredTo(currentLocation)) * scalars[0];
        double addAdjacent = add * scalars[1];
        double addPerp = add * scalars[2];
        double addPerpAdj = add * scalars[3];
        double addOpp = add * scalars[4];
        switch (currentLocation.directionTo(nextUnit)) {
            case NORTH:
                directions[4] += addOpp;
                directions[5] += addPerpAdj;
                directions[6] += addPerp;
                directions[7] += addAdjacent;
                directions[0] += add;
                directions[1] += addAdjacent;
                directions[2] += addPerp;
                directions[3] += addPerpAdj;
                break;
            case NORTH_EAST:
                directions[5] += addOpp;
                directions[6] += addPerpAdj;
                directions[7] += addPerp;
                directions[0] += addAdjacent;
                directions[1] += add;
                directions[2] += addAdjacent;
                directions[3] += addPerp;
                directions[4] += addPerpAdj;
                break;
            case EAST:
                directions[6] += addOpp;
                directions[7] += addPerpAdj;
                directions[0] += addPerp;
                directions[1] += addAdjacent;
                directions[2] += add;
                directions[3] += addAdjacent;
                directions[4] += addPerp;
                directions[5] += addPerpAdj;
                break;
            case SOUTH_EAST:
                directions[7] += addOpp;
                directions[0] += addPerpAdj;
                directions[1] += addPerp;
                directions[2] += addAdjacent;
                directions[3] += add;
                directions[4] += addAdjacent;
                directions[5] += addPerp;
                directions[6] += addPerpAdj;
                break;
            case SOUTH:
                directions[0] += addOpp;
                directions[1] += addPerpAdj;
                directions[2] += addPerp;
                directions[3] += addAdjacent;
                directions[4] += add;
                directions[5] += addAdjacent;
                directions[6] += addPerp;
                directions[7] += addPerpAdj;
                break;
            case SOUTH_WEST:
                directions[1] += addOpp;
                directions[2] += addPerpAdj;
                directions[3] += addPerp;
                directions[4] += addAdjacent;
                directions[5] += add;
                directions[6] += addAdjacent;
                directions[7] += addPerp;
                directions[0] += addPerpAdj;
                break;
            case WEST:
                directions[2] += addOpp;
                directions[3] += addPerpAdj;
                directions[4] += addPerp;
                directions[5] += addAdjacent;
                directions[6] += add;
                directions[7] += addAdjacent;
                directions[0] += addPerp;
                directions[1] += addPerpAdj;
                break;
            case NORTH_WEST:
                directions[3] += addOpp;
                directions[4] += addPerpAdj;
                directions[5] += addPerp;
                directions[6] += addAdjacent;
                directions[7] += add;
                directions[0] += addAdjacent;
                directions[1] += addPerp;
                directions[2] += addPerpAdj;
                break;
        }

        return directions;
    }


    public int[] applyAlliedWeights(int[] directions, RobotInfo[] units, double[] scalars) {

        if (units == null) {
            return directions;
        }
        for (int i = units.length; --i >= 0;) {
            MapLocation nextUnit = units[i].location;
            double add = (38 - nextUnit.distanceSquaredTo(currentLocation)) * scalars[0];
            double addAdjacent = add * scalars[1];
            double addPerp = add * scalars[2];
            double addPerpAdj = add * scalars[3];
            double addOpp = add * scalars[4];
            switch (currentLocation.directionTo(nextUnit)) {
                case NORTH:
                    directions[4] += addOpp;
                    directions[5] += addPerpAdj;
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    directions[3] += addPerpAdj;
                    break;
                case NORTH_EAST:
                    directions[5] += addOpp;
                    directions[6] += addPerpAdj;
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    directions[4] += addPerpAdj;
                    break;
                case EAST:
                    directions[6] += addOpp;
                    directions[7] += addPerpAdj;
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    directions[5] += addPerpAdj;
                    break;
                case SOUTH_EAST:
                    directions[7] += addOpp;
                    directions[0] += addPerpAdj;
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    directions[6] += addPerpAdj;
                    break;
                case SOUTH:
                    directions[0] += addOpp;
                    directions[1] += addPerpAdj;
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    directions[7] += addPerpAdj;
                    break;
                case SOUTH_WEST:
                    directions[1] += addOpp;
                    directions[2] += addPerpAdj;
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    directions[0] += addPerpAdj;
                    break;
                case WEST:
                    directions[2] += addOpp;
                    directions[3] += addPerpAdj;
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    directions[1] += addPerpAdj;
                    break;
                case NORTH_WEST:
                    directions[3] += addOpp;
                    directions[4] += addPerpAdj;
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
                    directions[2] += addPerpAdj;
                    break;
            }
        }
        return directions;
    }
}
