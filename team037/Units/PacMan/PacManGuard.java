package team037.Units.PacMan;

import battlecode.common.*;
import team037.Units.BaseUnits.BaseGaurd;

/**
 * Created by davej on 1/15/2016.
 */
public class PacManGuard extends BaseGaurd implements PacMan {

    // These are the weights.
    static final double[][] PACMAN_WEIGHTS = new double[][]
            {
                    {1, .5, .5, .5, .5},        // zombie weights (zombies in sensor range)
                    {1, .25, .333333, .5, .5},  // enemy weights (enemies in sensor range)
                    {-8, -4, -2, -1, 0},            // target constants (attract towards target)
//                    {1, .5, .5, .5, .5},   // friendly unit weights (friendlies in sensor range)
                    {2, .5, 0, 0, 0},        // Archon weights (constantly repel from friendly Archons)
            };

    public PacManGuard(RobotController rc) {
        super(rc);
        try {
            MapLocation[] badArchons = rc.getInitialArchonLocations(opponent);
            if (updateTarget() && badArchons != null && badArchons.length > 0) {
                int max = -1;
                for (int i = badArchons.length; --i >= 0;) {
                    if (badArchons[i].distanceSquaredTo(currentLocation) > max) {
                        navigator.setTarget(badArchons[i]);
                    }
                }
            }
        } catch (Exception e) {}
    }

    public boolean fight() throws GameActionException {
        return fightMicro.basicNetFightMicro(nearByEnemies,nearByAllies,enemies,allies,navigator.getTarget());
    }


    public boolean fightZombies() {
        // No need to fight zombies if there aren't any
        if (zombies == null || zombies.length == 0) {
            return false;
        }

        return runAway(PACMAN_WEIGHTS);
    }

    public boolean takeNextStep() {
        if (allies == null || allies.length < 1) {
            return false;
        }

        return runAway(PACMAN_WEIGHTS);
    }

    public int[] applyAdditionalWeights(int[] directions, double[][] weights) {

        directions = applyUnitWeights(currentLocation,directions,allies,weights[3]);

//        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
//        if (myArchons != null && myArchons.length > 0) {
//            directions = applyConstants(currentLocation,directions,myArchons,weights[4]);
//        }

        return directions;
    }

//    public int[] applyAdditionalConstants(int[] directions, double[][] weights) {
//        MapLocation[] myArchons = mapKnowledge.getArchonLocations(true);
//        if (myArchons == null || myArchons.length == 0) {
//            return directions;
//        }
//
//        directions = applyConstants(currentLocation,directions,myArchons,weights[4]);
//
//        return directions;
//    }

    public boolean updateTarget() throws GameActionException
    {
        if (target == null || currentLocation.equals(navigator.getTarget())) {
            return true;
        }
        return false;
    }


    public MapLocation getNextSpot() throws GameActionException
    {
        MapLocation newTarget = currentLocation;
        try {
            MapLocation[] badArchons = rc.getInitialArchonLocations(opponent);
            if (badArchons != null && badArchons.length > 0) {
                int max = -1;
                for (int i = badArchons.length; --i >= 0;) {
                    if (badArchons[i].distanceSquaredTo(currentLocation) > max) {
                        newTarget = badArchons[i];
                    }
                }
            }
        } catch (Exception e) {}
        return newTarget;
    }

    public boolean aidDistressedArchon() throws GameActionException {return false;}
//    public void handleMessages() throws GameActionException { }
    public void sendMessages()
    {
        return;
    }


    public RobotInfo foundArchon() {
        RobotType archon = RobotType.ARCHON;
        try {
            if (allies[0].type.equals(archon)) {
                return allies[0];
            }
            if (allies[1].type.equals(archon)) {
                return allies[1];
            }
            if (allies[2].type.equals(archon)) {
                return allies[2];
            }
            if (allies[3].type.equals(archon)) {
                return allies[3];
            }
            if (allies[4].type.equals(archon)) {
                return allies[4];
            }
            if (allies[5].type.equals(archon)) {
                return allies[5];
            }
            if (allies[6].type.equals(archon)) {
                return allies[6];
            }
            if (allies[7].type.equals(archon)) {
                return allies[7];
            }
            if (allies[8].type.equals(archon)) {
                return allies[8];
            }
            if (allies[9].type.equals(archon)) {
                return allies[9];
            }
            if (allies[10].type.equals(archon)) {
                return allies[10];
            }
            if (allies[11].type.equals(archon)) {
                return allies[11];
            }
            if (allies[12].type.equals(archon)) {
                return allies[12];
            }
            if (allies[13].type.equals(archon)) {
                return allies[13];
            }
            if (allies[14].type.equals(archon)) {
                return allies[14];
            }
            if (allies[15].type.equals(archon)) {
                return allies[15];
            }
            if (allies[16].type.equals(archon)) {
                return allies[16];
            }
            if (allies[17].type.equals(archon)) {
                return allies[17];
            }
            if (allies[18].type.equals(archon)) {
                return allies[18];
            }
            if (allies[19].type.equals(archon)) {
                return allies[19];
            }
            if (allies[20].type.equals(archon)) {
                return allies[20];
            }
            if (allies[21].type.equals(archon)) {
                return allies[21];
            }
            if (allies[22].type.equals(archon)) {
                return allies[22];
            }
            if (allies[23].type.equals(archon)) {
                return allies[23];
            }
            if (allies[24].type.equals(archon)) {
                return allies[24];
            }
            if (allies[25].type.equals(archon)) {
                return allies[25];
            }
            if (allies[26].type.equals(archon)) {
                return allies[26];
            }
            if (allies[27].type.equals(archon)) {
                return allies[27];
            }
            if (allies[28].type.equals(archon)) {
                return allies[28];
            }
            if (allies[29].type.equals(archon)) {
                return allies[29];
            }
            if (allies[30].type.equals(archon)) {
                return allies[30];
            }
            if (allies[31].type.equals(archon)) {
                return allies[31];
            }
            if (allies[32].type.equals(archon)) {
                return allies[32];
            }
            if (allies[33].type.equals(archon)) {
                return allies[33];
            }
            if (allies[34].type.equals(archon)) {
                return allies[34];
            }
            if (allies[35].type.equals(archon)) {
                return allies[35];
            }
            if (allies[36].type.equals(archon)) {
                return allies[36];
            }
            if (allies[37].type.equals(archon)) {
                return allies[37];
            }
            if (allies[38].type.equals(archon)) {
                return allies[38];
            }
            if (allies[39].type.equals(archon)) {
                return allies[39];
            }
            if (allies[40].type.equals(archon)) {
                return allies[40];
            }
            if (allies[41].type.equals(archon)) {
                return allies[41];
            }
            if (allies[42].type.equals(archon)) {
                return allies[42];
            }
            if (allies[43].type.equals(archon)) {
                return allies[43];
            }
            if (allies[44].type.equals(archon)) {
                return allies[44];
            }
            if (allies[45].type.equals(archon)) {
                return allies[45];
            }
            if (allies[46].type.equals(archon)) {
                return allies[46];
            }
            if (allies[47].type.equals(archon)) {
                return allies[47];
            }
            if (allies[48].type.equals(archon)) {
                return allies[48];
            }
            if (allies[49].type.equals(archon)) {
                return allies[49];
            }
            if (allies[50].type.equals(archon)) {
                return allies[50];
            }
            if (allies[51].type.equals(archon)) {
                return allies[51];
            }
            if (allies[52].type.equals(archon)) {
                return allies[52];
            }
            if (allies[53].type.equals(archon)) {
                return allies[53];
            }
            if (allies[54].type.equals(archon)) {
                return allies[54];
            }
            if (allies[55].type.equals(archon)) {
                return allies[55];
            }
            if (allies[56].type.equals(archon)) {
                return allies[56];
            }
            if (allies[57].type.equals(archon)) {
                return allies[57];
            }
            if (allies[58].type.equals(archon)) {
                return allies[58];
            }
            if (allies[59].type.equals(archon)) {
                return allies[59];
            }
            if (allies[60].type.equals(archon)) {
                return allies[60];
            }
            if (allies[61].type.equals(archon)) {
                return allies[61];
            }
            if (allies[62].type.equals(archon)) {
                return allies[62];
            }
            if (allies[63].type.equals(archon)) {
                return allies[63];
            }
            if (allies[64].type.equals(archon)) {
                return allies[64];
            }
            if (allies[65].type.equals(archon)) {
                return allies[65];
            }
            if (allies[66].type.equals(archon)) {
                return allies[66];
            }
            if (allies[67].type.equals(archon)) {
                return allies[67];
            }
            if (allies[68].type.equals(archon)) {
                return allies[68];
            }
            if (allies[69].type.equals(archon)) {
                return allies[69];
            }
            if (allies[70].type.equals(archon)) {
                return allies[70];
            }
            if (allies[71].type.equals(archon)) {
                return allies[71];
            }
            if (allies[72].type.equals(archon)) {
                return allies[72];
            }
            if (allies[73].type.equals(archon)) {
                return allies[73];
            }
            if (allies[74].type.equals(archon)) {
                return allies[74];
            }
            if (allies[75].type.equals(archon)) {
                return allies[75];
            }
            if (allies[76].type.equals(archon)) {
                return allies[76];
            }
            if (allies[77].type.equals(archon)) {
                return allies[77];
            }
            if (allies[78].type.equals(archon)) {
                return allies[78];
            }
            if (allies[79].type.equals(archon)) {
                return allies[79];
            }
            if (allies[80].type.equals(archon)) {
                return allies[80];
            }
            if (allies[81].type.equals(archon)) {
                return allies[81];
            }
            if (allies[82].type.equals(archon)) {
                return allies[82];
            }
            if (allies[83].type.equals(archon)) {
                return allies[83];
            }
            if (allies[84].type.equals(archon)) {
                return allies[84];
            }
            if (allies[85].type.equals(archon)) {
                return allies[85];
            }
            if (allies[86].type.equals(archon)) {
                return allies[86];
            }
            if (allies[87].type.equals(archon)) {
                return allies[87];
            }
            if (allies[88].type.equals(archon)) {
                return allies[88];
            }
            if (allies[89].type.equals(archon)) {
                return allies[89];
            }
            if (allies[90].type.equals(archon)) {
                return allies[90];
            }
            if (allies[91].type.equals(archon)) {
                return allies[91];
            }
            if (allies[92].type.equals(archon)) {
                return allies[92];
            }
            if (allies[93].type.equals(archon)) {
                return allies[93];
            }
            if (allies[94].type.equals(archon)) {
                return allies[94];
            }
            if (allies[95].type.equals(archon)) {
                return allies[95];
            }
            if (allies[96].type.equals(archon)) {
                return allies[96];
            }
            if (allies[97].type.equals(archon)) {
                return allies[97];
            }
            if (allies[98].type.equals(archon)) {
                return allies[98];
            }
            if (allies[99].type.equals(archon)) {
                return allies[99];
            }
            if (allies[100].type.equals(archon)) {
                return allies[100];
            }
            if (allies[101].type.equals(archon)) {
                return allies[101];
            }
            if (allies[102].type.equals(archon)) {
                return allies[102];
            }
            if (allies[103].type.equals(archon)) {
                return allies[103];
            }
            if (allies[104].type.equals(archon)) {
                return allies[104];
            }
            if (allies[105].type.equals(archon)) {
                return allies[105];
            }
            if (allies[106].type.equals(archon)) {
                return allies[106];
            }
            if (allies[107].type.equals(archon)) {
                return allies[107];
            }
            if (allies[108].type.equals(archon)) {
                return allies[108];
            }
            if (allies[109].type.equals(archon)) {
                return allies[109];
            }
            if (allies[110].type.equals(archon)) {
                return allies[110];
            }
            if (allies[111].type.equals(archon)) {
                return allies[111];
            }
            if (allies[112].type.equals(archon)) {
                return allies[112];
            }
            if (allies[113].type.equals(archon)) {
                return allies[113];
            }
            if (allies[114].type.equals(archon)) {
                return allies[114];
            }
            if (allies[115].type.equals(archon)) {
                return allies[115];
            }
            if (allies[116].type.equals(archon)) {
                return allies[116];
            }
            if (allies[117].type.equals(archon)) {
                return allies[117];
            }
            if (allies[118].type.equals(archon)) {
                return allies[118];
            }
            if (allies[119].type.equals(archon)) {
                return allies[119];
            }
            if (allies[120].type.equals(archon)) {
                return allies[120];
            }
            if (allies[121].type.equals(archon)) {
                return allies[121];
            }
            if (allies[122].type.equals(archon)) {
                return allies[122];
            }
            if (allies[123].type.equals(archon)) {
                return allies[123];
            }
            if (allies[124].type.equals(archon)) {
                return allies[124];
            }
            if (allies[125].type.equals(archon)) {
                return allies[125];
            }
            if (allies[126].type.equals(archon)) {
                return allies[126];
            }
            if (allies[127].type.equals(archon)) {
                return allies[127];
            }
            if (allies[128].type.equals(archon)) {
                return allies[128];
            }
            if (allies[129].type.equals(archon)) {
                return allies[129];
            }
            if (allies[130].type.equals(archon)) {
                return allies[130];
            }
            if (allies[131].type.equals(archon)) {
                return allies[131];
            }
            if (allies[132].type.equals(archon)) {
                return allies[132];
            }
            if (allies[133].type.equals(archon)) {
                return allies[133];
            }
            if (allies[134].type.equals(archon)) {
                return allies[134];
            }
            if (allies[135].type.equals(archon)) {
                return allies[135];
            }
            if (allies[136].type.equals(archon)) {
                return allies[136];
            }
            if (allies[137].type.equals(archon)) {
                return allies[137];
            }
            if (allies[138].type.equals(archon)) {
                return allies[138];
            }
            if (allies[139].type.equals(archon)) {
                return allies[139];
            }
            if (allies[140].type.equals(archon)) {
                return allies[140];
            }
            if (allies[141].type.equals(archon)) {
                return allies[141];
            }
            if (allies[142].type.equals(archon)) {
                return allies[142];
            }
            if (allies[143].type.equals(archon)) {
                return allies[143];
            }
            if (allies[144].type.equals(archon)) {
                return allies[144];
            }
            if (allies[145].type.equals(archon)) {
                return allies[145];
            }
            if (allies[146].type.equals(archon)) {
                return allies[146];
            }
            if (allies[147].type.equals(archon)) {
                return allies[147];
            }
            if (allies[148].type.equals(archon)) {
                return allies[148];
            }
            if (allies[149].type.equals(archon)) {
                return allies[149];
            }
            if (allies[150].type.equals(archon)) {
                return allies[150];
            }
            if (allies[151].type.equals(archon)) {
                return allies[151];
            }
            if (allies[152].type.equals(archon)) {
                return allies[152];
            }
            if (allies[153].type.equals(archon)) {
                return allies[153];
            }
            if (allies[154].type.equals(archon)) {
                return allies[154];
            }
            if (allies[155].type.equals(archon)) {
                return allies[155];
            }
            if (allies[156].type.equals(archon)) {
                return allies[156];
            }
            if (allies[157].type.equals(archon)) {
                return allies[157];
            }
            if (allies[158].type.equals(archon)) {
                return allies[158];
            }
            if (allies[159].type.equals(archon)) {
                return allies[159];
            }
            if (allies[160].type.equals(archon)) {
                return allies[160];
            }
            if (allies[161].type.equals(archon)) {
                return allies[161];
            }
            if (allies[162].type.equals(archon)) {
                return allies[162];
            }
            if (allies[163].type.equals(archon)) {
                return allies[163];
            }
            if (allies[164].type.equals(archon)) {
                return allies[164];
            }
            if (allies[165].type.equals(archon)) {
                return allies[165];
            }
            if (allies[166].type.equals(archon)) {
                return allies[166];
            }
            if (allies[167].type.equals(archon)) {
                return allies[167];
            }
            if (allies[168].type.equals(archon)) {
                return allies[168];
            }
            if (allies[169].type.equals(archon)) {
                return allies[169];
            }
            if (allies[170].type.equals(archon)) {
                return allies[170];
            }
            if (allies[171].type.equals(archon)) {
                return allies[171];
            }
            if (allies[172].type.equals(archon)) {
                return allies[172];
            }
            if (allies[173].type.equals(archon)) {
                return allies[173];
            }
            if (allies[174].type.equals(archon)) {
                return allies[174];
            }
            if (allies[175].type.equals(archon)) {
                return allies[175];
            }
            if (allies[176].type.equals(archon)) {
                return allies[176];
            }
            if (allies[177].type.equals(archon)) {
                return allies[177];
            }
            if (allies[178].type.equals(archon)) {
                return allies[178];
            }
            if (allies[179].type.equals(archon)) {
                return allies[179];
            }
            if (allies[180].type.equals(archon)) {
                return allies[180];
            }
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
