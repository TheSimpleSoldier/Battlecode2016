package team037.Utilites;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.Messages.Communication;
import team037.Unit;

public class FightMicroUtilites
{
    /**
     * This method returns the RobotInfo for the Robot with the lowest health
     */
    public static RobotInfo findWeakestEnemy(RobotInfo[] nearByEnemies)
    {
        RobotInfo bestTarget = nearByEnemies[nearByEnemies.length - 1];
        double value = bestTarget.attackPower * Math.sqrt(bestTarget.type.attackRadiusSquared) / (bestTarget.health * bestTarget.type.attackDelay);

        for (int i = nearByEnemies.length-1; --i >= 0; )
        {
            if (nearByEnemies[i] != null)
            {
                RobotInfo enemy = nearByEnemies[i];
                double newValue = enemy.attackPower * Math.sqrt(bestTarget.type.attackRadiusSquared) / (enemy.health * enemy.type.attackDelay);

                if (newValue > value)
                {
                    value = newValue;
                    bestTarget = enemy;
                }
            }
        }

        return bestTarget;
    }

    public static boolean unitsEngaged(RobotInfo[] units, RobotInfo[] opponents)
    {
        for (int i = units.length; --i>=0; )
        {
            for (int j = opponents.length; --j>=0; )
            {
                if (opponents[j].location.distanceSquaredTo(units[i].location) <= units[i].type.attackRadiusSquared)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static int totalZombieDamage(RobotInfo[] zombies, MapLocation current)
    {
        int total = 0;
        for (int i = zombies.length; --i>=0; )
        {
            if (zombies[i].location.distanceSquaredTo(current) <= zombies[i].type.attackRadiusSquared)
            {
                double delay = zombies[i].weaponDelay;

                if (delay < 1) delay = 1;

                total += zombies[i].attackPower / delay;
            }
        }

        return total;
    }

    /**
     * This is a targeting method for vipers to infect their own units
     *
     * @param nearByAllies
     * @param nearByEnemies
     * @return
     */
    public static RobotInfo pickViperTarget(RobotInfo[] nearByAllies, RobotInfo[] nearByEnemies)
    {
        double lowestHealth = Double.MAX_VALUE;
        RobotInfo weakest = null;

        for (int i = nearByEnemies.length; --i>=0; )
        {
            if (nearByEnemies[i].viperInfectedTurns == 0 && nearByEnemies[i].zombieInfectedTurns == 0)
            {
                if (nearByEnemies[i].health <= 20 && nearByEnemies[i].health < lowestHealth)
                {
                    lowestHealth = nearByEnemies[i].health;
                    weakest = nearByEnemies[i];
                }
            }
        }

        if (weakest != null)
        {
            return weakest;
        }

        // else look to infect an ally right before death
        for (int i = nearByAllies.length; --i>=0; )
        {
            if (nearByAllies[i].viperInfectedTurns == 0 && nearByAllies[i].zombieInfectedTurns == 0)
            {
                if (nearByAllies[i].health <= 10 && nearByAllies[i].health < lowestHealth)
                {
                    lowestHealth = nearByAllies[i].health;
                    weakest = nearByAllies[i];
                }
            }
        }

        if (weakest != null)
        {
            return weakest;
        }

        int infectionAmount = 20;

        for (int i = nearByEnemies.length; --i>=0; )
        {
            if (nearByEnemies[i].viperInfectedTurns < infectionAmount)
            {
                infectionAmount = nearByEnemies[i].viperInfectedTurns;
                weakest = nearByEnemies[i];
            }
        }

        return weakest;
    }

    /**
     * This method returns the best target from a list of enemies
     *
     * @param nearByEnemies
     * @param rc
     * @return
     */
    public static MapLocation getBestTurretTarget(RobotInfo[] nearByEnemies, RobotController rc)
    {
        MapLocation target = null;

        if (nearByEnemies.length > 0)
        {
            double weakestHealth = 99999;
            for (int i = 0; i < nearByEnemies.length; i++)
            {
                MapLocation enemy = nearByEnemies[i].location;
                if (rc.getLocation().distanceSquaredTo(enemy) > 5)
                {
                    double enemyHealth = nearByEnemies[i].health;

                    // we want to hit enemy with highest health that we can kill with one shot
                    if (enemyHealth <= RobotType.TURRET.attackPower && weakestHealth < RobotType.TURRET.attackPower)
                    {
                        if (weakestHealth < enemyHealth)
                        {
                            weakestHealth = enemyHealth;
                            target = enemy;
                        }
                    }
                    // other wise we kill the weak
                    else if (weakestHealth > enemyHealth)
                    {
                        weakestHealth = enemyHealth;
                        target = enemy;
                    }
                }
            }
        }

        return target;
    }

    /**
     * This method gets the best location for a turret to attack
     *
     * @param nearByEnemies
     * @param rc
     * @return
     */
    public static MapLocation getTurretAttackPoint(RobotInfo[] nearByEnemies, RobotController rc, Communication[] communications)
    {
        MapLocation target = getBestTurretTarget(nearByEnemies, rc);

        // search signals for scouts telling us locations as well as
        // for enemy broadcasts
        if (target == null)
        {
            MapLocation loc = rc.getLocation();
            int dist = RobotType.TURRET.attackRadiusSquared;
            double coreDelay = 0;

            for (int i = communications.length; --i>=0; )
            {
                if (communications[i].opcode == CommunicationType.TURRET_SUPPORT || communications[i].opcode == CommunicationType.OENEMY)
                {
                    int[] cords = communications[i].getValues();
                    MapLocation enemy = new MapLocation(cords[2], cords[3]);
                    if (enemy.distanceSquaredTo(loc) <= dist && cords[1] > coreDelay)
                    {
                        target = enemy;
                        coreDelay = cords[1];
                    }
                }
            }
        }

        return target;
    }

    public static Direction getDir(RobotController rc, MapLocation target)
    {
        if (target == null)
            return Direction.NONE;

        return rc.getLocation().directionTo(target);
    }

    public static void moveDir(RobotController rc, Direction dir, boolean clearRubble) throws GameActionException
    {
        if (dir == Direction.NONE || dir == Direction.OMNI)
        {
            return;
        }

        if (rc.isCoreReady())
        {
            if (rc.canMove(dir))
            {
                rc.move(dir);
            }
            else if (rc.canMove(dir.rotateLeft()))
            {
                rc.move(dir.rotateLeft());
            }
            else if (rc.canMove(dir.rotateRight()))
            {
                rc.move(dir.rotateRight());
            }
            else if (clearRubble && rc.senseRubble(rc.getLocation().add(dir)) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            {
                rc.clearRubble(dir);
            }
            else if (clearRubble && rc.senseRubble(rc.getLocation().add(dir.rotateLeft())) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            {
                rc.clearRubble(dir.rotateLeft());
            }
            else if (clearRubble && rc.senseRubble(rc.getLocation().add(dir.rotateRight())) > GameConstants.RUBBLE_OBSTRUCTION_THRESH)
            {
                rc.clearRubble(dir.rotateRight());
            }
        }
    }

    public static boolean offensiveEnemies(RobotInfo[] enemies)
    {
        for (int i = enemies.length; --i>=0;)
        {
            switch(enemies[i].type)
            {
                case STANDARDZOMBIE:
                case FASTZOMBIE:
                case BIGZOMBIE:
                case RANGEDZOMBIE:
                case SOLDIER:
                case VIPER:
                case GUARD:
                case TURRET:
                    return true;
            }
        }

        return false;
    }


    public RobotInfo findFirstUnitType(RobotInfo[] checkBots, RobotType type) {
        try {
            if (checkBots[0].type.equals(type)) {
                return checkBots[0];
            }
            if (checkBots[1].type.equals(type)) {
                return checkBots[1];
            }
            if (checkBots[2].type.equals(type)) {
                return checkBots[2];
            }
            if (checkBots[3].type.equals(type)) {
                return checkBots[3];
            }
            if (checkBots[4].type.equals(type)) {
                return checkBots[4];
            }
            if (checkBots[5].type.equals(type)) {
                return checkBots[5];
            }
            if (checkBots[6].type.equals(type)) {
                return checkBots[6];
            }
            if (checkBots[7].type.equals(type)) {
                return checkBots[7];
            }
            if (checkBots[8].type.equals(type)) {
                return checkBots[8];
            }
            if (checkBots[9].type.equals(type)) {
                return checkBots[9];
            }
            if (checkBots[10].type.equals(type)) {
                return checkBots[10];
            }
            if (checkBots[11].type.equals(type)) {
                return checkBots[11];
            }
            if (checkBots[12].type.equals(type)) {
                return checkBots[12];
            }
            if (checkBots[13].type.equals(type)) {
                return checkBots[13];
            }
            if (checkBots[14].type.equals(type)) {
                return checkBots[14];
            }
            if (checkBots[15].type.equals(type)) {
                return checkBots[15];
            }
            if (checkBots[16].type.equals(type)) {
                return checkBots[16];
            }
            if (checkBots[17].type.equals(type)) {
                return checkBots[17];
            }
            if (checkBots[18].type.equals(type)) {
                return checkBots[18];
            }
            if (checkBots[19].type.equals(type)) {
                return checkBots[19];
            }
            if (checkBots[20].type.equals(type)) {
                return checkBots[20];
            }
            if (checkBots[21].type.equals(type)) {
                return checkBots[21];
            }
            if (checkBots[22].type.equals(type)) {
                return checkBots[22];
            }
            if (checkBots[23].type.equals(type)) {
                return checkBots[23];
            }
            if (checkBots[24].type.equals(type)) {
                return checkBots[24];
            }
            if (checkBots[25].type.equals(type)) {
                return checkBots[25];
            }
            if (checkBots[26].type.equals(type)) {
                return checkBots[26];
            }
            if (checkBots[27].type.equals(type)) {
                return checkBots[27];
            }
            if (checkBots[28].type.equals(type)) {
                return checkBots[28];
            }
            if (checkBots[29].type.equals(type)) {
                return checkBots[29];
            }
            if (checkBots[30].type.equals(type)) {
                return checkBots[30];
            }
            if (checkBots[31].type.equals(type)) {
                return checkBots[31];
            }
            if (checkBots[32].type.equals(type)) {
                return checkBots[32];
            }
            if (checkBots[33].type.equals(type)) {
                return checkBots[33];
            }
            if (checkBots[34].type.equals(type)) {
                return checkBots[34];
            }
            if (checkBots[35].type.equals(type)) {
                return checkBots[35];
            }
            if (checkBots[36].type.equals(type)) {
                return checkBots[36];
            }
            if (checkBots[37].type.equals(type)) {
                return checkBots[37];
            }
            if (checkBots[38].type.equals(type)) {
                return checkBots[38];
            }
            if (checkBots[39].type.equals(type)) {
                return checkBots[39];
            }
            if (checkBots[40].type.equals(type)) {
                return checkBots[40];
            }
            if (checkBots[41].type.equals(type)) {
                return checkBots[41];
            }
            if (checkBots[42].type.equals(type)) {
                return checkBots[42];
            }
            if (checkBots[43].type.equals(type)) {
                return checkBots[43];
            }
            if (checkBots[44].type.equals(type)) {
                return checkBots[44];
            }
            if (checkBots[45].type.equals(type)) {
                return checkBots[45];
            }
            if (checkBots[46].type.equals(type)) {
                return checkBots[46];
            }
            if (checkBots[47].type.equals(type)) {
                return checkBots[47];
            }
            if (checkBots[48].type.equals(type)) {
                return checkBots[48];
            }
            if (checkBots[49].type.equals(type)) {
                return checkBots[49];
            }
            if (checkBots[50].type.equals(type)) {
                return checkBots[50];
            }
            if (checkBots[51].type.equals(type)) {
                return checkBots[51];
            }
            if (checkBots[52].type.equals(type)) {
                return checkBots[52];
            }
            if (checkBots[53].type.equals(type)) {
                return checkBots[53];
            }
            if (checkBots[54].type.equals(type)) {
                return checkBots[54];
            }
            if (checkBots[55].type.equals(type)) {
                return checkBots[55];
            }
            if (checkBots[56].type.equals(type)) {
                return checkBots[56];
            }
            if (checkBots[57].type.equals(type)) {
                return checkBots[57];
            }
            if (checkBots[58].type.equals(type)) {
                return checkBots[58];
            }
            if (checkBots[59].type.equals(type)) {
                return checkBots[59];
            }
            if (checkBots[60].type.equals(type)) {
                return checkBots[60];
            }
            if (checkBots[61].type.equals(type)) {
                return checkBots[61];
            }
            if (checkBots[62].type.equals(type)) {
                return checkBots[62];
            }
            if (checkBots[63].type.equals(type)) {
                return checkBots[63];
            }
            if (checkBots[64].type.equals(type)) {
                return checkBots[64];
            }
            if (checkBots[65].type.equals(type)) {
                return checkBots[65];
            }
            if (checkBots[66].type.equals(type)) {
                return checkBots[66];
            }
            if (checkBots[67].type.equals(type)) {
                return checkBots[67];
            }
            if (checkBots[68].type.equals(type)) {
                return checkBots[68];
            }
            if (checkBots[69].type.equals(type)) {
                return checkBots[69];
            }
            if (checkBots[70].type.equals(type)) {
                return checkBots[70];
            }
            if (checkBots[71].type.equals(type)) {
                return checkBots[71];
            }
            if (checkBots[72].type.equals(type)) {
                return checkBots[72];
            }
            if (checkBots[73].type.equals(type)) {
                return checkBots[73];
            }
            if (checkBots[74].type.equals(type)) {
                return checkBots[74];
            }
            if (checkBots[75].type.equals(type)) {
                return checkBots[75];
            }
            if (checkBots[76].type.equals(type)) {
                return checkBots[76];
            }
            if (checkBots[77].type.equals(type)) {
                return checkBots[77];
            }
            if (checkBots[78].type.equals(type)) {
                return checkBots[78];
            }
            if (checkBots[79].type.equals(type)) {
                return checkBots[79];
            }
            if (checkBots[80].type.equals(type)) {
                return checkBots[80];
            }
            if (checkBots[81].type.equals(type)) {
                return checkBots[81];
            }
            if (checkBots[82].type.equals(type)) {
                return checkBots[82];
            }
            if (checkBots[83].type.equals(type)) {
                return checkBots[83];
            }
            if (checkBots[84].type.equals(type)) {
                return checkBots[84];
            }
            if (checkBots[85].type.equals(type)) {
                return checkBots[85];
            }
            if (checkBots[86].type.equals(type)) {
                return checkBots[86];
            }
            if (checkBots[87].type.equals(type)) {
                return checkBots[87];
            }
            if (checkBots[88].type.equals(type)) {
                return checkBots[88];
            }
            if (checkBots[89].type.equals(type)) {
                return checkBots[89];
            }
            if (checkBots[90].type.equals(type)) {
                return checkBots[90];
            }
            if (checkBots[91].type.equals(type)) {
                return checkBots[91];
            }
            if (checkBots[92].type.equals(type)) {
                return checkBots[92];
            }
            if (checkBots[93].type.equals(type)) {
                return checkBots[93];
            }
            if (checkBots[94].type.equals(type)) {
                return checkBots[94];
            }
            if (checkBots[95].type.equals(type)) {
                return checkBots[95];
            }
            if (checkBots[96].type.equals(type)) {
                return checkBots[96];
            }
            if (checkBots[97].type.equals(type)) {
                return checkBots[97];
            }
            if (checkBots[98].type.equals(type)) {
                return checkBots[98];
            }
            if (checkBots[99].type.equals(type)) {
                return checkBots[99];
            }
            if (checkBots[100].type.equals(type)) {
                return checkBots[100];
            }
            if (checkBots[101].type.equals(type)) {
                return checkBots[101];
            }
            if (checkBots[102].type.equals(type)) {
                return checkBots[102];
            }
            if (checkBots[103].type.equals(type)) {
                return checkBots[103];
            }
            if (checkBots[104].type.equals(type)) {
                return checkBots[104];
            }
            if (checkBots[105].type.equals(type)) {
                return checkBots[105];
            }
            if (checkBots[106].type.equals(type)) {
                return checkBots[106];
            }
            if (checkBots[107].type.equals(type)) {
                return checkBots[107];
            }
            if (checkBots[108].type.equals(type)) {
                return checkBots[108];
            }
            if (checkBots[109].type.equals(type)) {
                return checkBots[109];
            }
            if (checkBots[110].type.equals(type)) {
                return checkBots[110];
            }
            if (checkBots[111].type.equals(type)) {
                return checkBots[111];
            }
            if (checkBots[112].type.equals(type)) {
                return checkBots[112];
            }
            if (checkBots[113].type.equals(type)) {
                return checkBots[113];
            }
            if (checkBots[114].type.equals(type)) {
                return checkBots[114];
            }
            if (checkBots[115].type.equals(type)) {
                return checkBots[115];
            }
            if (checkBots[116].type.equals(type)) {
                return checkBots[116];
            }
            if (checkBots[117].type.equals(type)) {
                return checkBots[117];
            }
            if (checkBots[118].type.equals(type)) {
                return checkBots[118];
            }
            if (checkBots[119].type.equals(type)) {
                return checkBots[119];
            }
            if (checkBots[120].type.equals(type)) {
                return checkBots[120];
            }
            if (checkBots[121].type.equals(type)) {
                return checkBots[121];
            }
            if (checkBots[122].type.equals(type)) {
                return checkBots[122];
            }
            if (checkBots[123].type.equals(type)) {
                return checkBots[123];
            }
            if (checkBots[124].type.equals(type)) {
                return checkBots[124];
            }
            if (checkBots[125].type.equals(type)) {
                return checkBots[125];
            }
            if (checkBots[126].type.equals(type)) {
                return checkBots[126];
            }
            if (checkBots[127].type.equals(type)) {
                return checkBots[127];
            }
            if (checkBots[128].type.equals(type)) {
                return checkBots[128];
            }
            if (checkBots[129].type.equals(type)) {
                return checkBots[129];
            }
            if (checkBots[130].type.equals(type)) {
                return checkBots[130];
            }
            if (checkBots[131].type.equals(type)) {
                return checkBots[131];
            }
            if (checkBots[132].type.equals(type)) {
                return checkBots[132];
            }
            if (checkBots[133].type.equals(type)) {
                return checkBots[133];
            }
            if (checkBots[134].type.equals(type)) {
                return checkBots[134];
            }
            if (checkBots[135].type.equals(type)) {
                return checkBots[135];
            }
            if (checkBots[136].type.equals(type)) {
                return checkBots[136];
            }
            if (checkBots[137].type.equals(type)) {
                return checkBots[137];
            }
            if (checkBots[138].type.equals(type)) {
                return checkBots[138];
            }
            if (checkBots[139].type.equals(type)) {
                return checkBots[139];
            }
            if (checkBots[140].type.equals(type)) {
                return checkBots[140];
            }
            if (checkBots[141].type.equals(type)) {
                return checkBots[141];
            }
            if (checkBots[142].type.equals(type)) {
                return checkBots[142];
            }
            if (checkBots[143].type.equals(type)) {
                return checkBots[143];
            }
            if (checkBots[144].type.equals(type)) {
                return checkBots[144];
            }
            if (checkBots[145].type.equals(type)) {
                return checkBots[145];
            }
            if (checkBots[146].type.equals(type)) {
                return checkBots[146];
            }
            if (checkBots[147].type.equals(type)) {
                return checkBots[147];
            }
            if (checkBots[148].type.equals(type)) {
                return checkBots[148];
            }
            if (checkBots[149].type.equals(type)) {
                return checkBots[149];
            }
            if (checkBots[150].type.equals(type)) {
                return checkBots[150];
            }
            if (checkBots[151].type.equals(type)) {
                return checkBots[151];
            }
            if (checkBots[152].type.equals(type)) {
                return checkBots[152];
            }
            if (checkBots[153].type.equals(type)) {
                return checkBots[153];
            }
            if (checkBots[154].type.equals(type)) {
                return checkBots[154];
            }
            if (checkBots[155].type.equals(type)) {
                return checkBots[155];
            }
            if (checkBots[156].type.equals(type)) {
                return checkBots[156];
            }
            if (checkBots[157].type.equals(type)) {
                return checkBots[157];
            }
            if (checkBots[158].type.equals(type)) {
                return checkBots[158];
            }
            if (checkBots[159].type.equals(type)) {
                return checkBots[159];
            }
            if (checkBots[160].type.equals(type)) {
                return checkBots[160];
            }
            if (checkBots[161].type.equals(type)) {
                return checkBots[161];
            }
            if (checkBots[162].type.equals(type)) {
                return checkBots[162];
            }
            if (checkBots[163].type.equals(type)) {
                return checkBots[163];
            }
            if (checkBots[164].type.equals(type)) {
                return checkBots[164];
            }
            if (checkBots[165].type.equals(type)) {
                return checkBots[165];
            }
            if (checkBots[166].type.equals(type)) {
                return checkBots[166];
            }
            if (checkBots[167].type.equals(type)) {
                return checkBots[167];
            }
            if (checkBots[168].type.equals(type)) {
                return checkBots[168];
            }
            if (checkBots[169].type.equals(type)) {
                return checkBots[169];
            }
            if (checkBots[170].type.equals(type)) {
                return checkBots[170];
            }
            if (checkBots[171].type.equals(type)) {
                return checkBots[171];
            }
            if (checkBots[172].type.equals(type)) {
                return checkBots[172];
            }
            if (checkBots[173].type.equals(type)) {
                return checkBots[173];
            }
            if (checkBots[174].type.equals(type)) {
                return checkBots[174];
            }
            if (checkBots[175].type.equals(type)) {
                return checkBots[175];
            }
            if (checkBots[176].type.equals(type)) {
                return checkBots[176];
            }
            if (checkBots[177].type.equals(type)) {
                return checkBots[177];
            }
            if (checkBots[178].type.equals(type)) {
                return checkBots[178];
            }
            if (checkBots[179].type.equals(type)) {
                return checkBots[179];
            }
            if (checkBots[180].type.equals(type)) {
                return checkBots[180];
            }
            return null;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
