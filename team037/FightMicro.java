package team037;

import battlecode.common.*;
import team037.NeuralNet.FeedForwardNeuralNet;
import team037.Utilites.FightMicroUtilites;

public class FightMicro
{
    public static RobotController rc;
    public static FeedForwardNeuralNet net;
    public static double[] weights;

    public FightMicro(RobotController robotController)
    {
        rc = robotController;
        weights = new double[]{
                4.549417219302615E21, 2.0074003328642874E22, -2.4630583825782664E22, -2.731078514624002E22, -7.573539097062148E21, 3.7292945919613866E22, -7.989374910157952E21, 2.469386448011587E22, 2.8828213832804483E22, 6.375508599134999E22, -1.7923606554878883E22, 2.018712322025682E22, 2.4114522656928506E22, 4.888713810923471E22, -1.8249392398971768E22, 4.130596727300113E22, 8.442327854773987E21, 4.7897583157586925E22, 1.5026255072046057E22, -8.378623723688634E21, 2.5332704545550216E22, 1.8926245974874333E22, 3.7156835647982676E21, -3.810340243307774E22, -1.0828597549308995E22, -1.3443908409039314E22, -3.5276809425517795E22, -2.0377725438499695E22, 3.998929124648245E22, 8.402795892823491E21, -4.3032429662596964E21, 5.347849171329663E21, -2.5585905828899756E20, -3.1371112200563075E22, 3.1985787479128136E22, -2.5836382097778397E22, -2.053760900928255E22, 4.255447864027854E22, 1.4151935666918843E21, -3.398180851908427E22, 3.0438051828563437E22, -2.7566972322498237E22, 9.282132190581863E21, 4.545337917445819E22, 2.0121269964177756E22, -2.1233266338035814E22, -1.0677452170045623E22, 3.3863216415916513E22, 1.9496006732897547E22, -4.7639663680092314E20, -1.9108320234042605E22, 2.3089667243038034E22, 1.0185464934251777E22, -1.9749782863941617E22, -8.542622530430644E21, -1.1471796331492747E22, 2.1664661235530025E21, -8.452837623122378E21, 1.5116028192602007E22, -3.17190682862494E22, 1.7071179193222118E22, -2.644798116537111E22, -2.884702424413284E22, -4.428590416899201E22, -5.642242278046152E21, 4.1993029901204624E22, -2.008744897399846E22, 1.9809531629320618E22, -1.150284178417309E22, -3.929281408122773E22, -3.414463821791779E22, -9.422585213728939E21, -3.902326812349972E22, -3.4705731760291857E22, 2.8969173348064973E21, -2.694956338286894E21, 4.3807070245967107E21, 8.899181067167853E21, -2.5285024605578245E22, -2.6124651025557967E22, -4.324342544229091E21, 4.456423803001286E22, 2.0562679659166603E22, 3.078389756332957E22, -3.423223952559416E22, 4.491934284212911E22, 1.9433400480566514E22, -3.3605289394942232E22, 4.1619500206340975E21, -1.4827702591817484E22, -1.4844161106978294E22, 3.6593770681970584E21, 1.0495267839900687E22, -1.5792065225125075E22, 1.2923434033017515E21, -4.24481842446659E22, -1.21015103785474E22, -4.6736238167708385E22, 1.4176168499864274E22, -1.2174084191505992E22, 2.8155505265780636E22, -1.33155618232755E22, -5.692537516506381E22, -2.0081774836504016E22, 4.3709199687748036E22, 1.0420687483252075E22, 1.5262718587585983E22, 1.7972295476188807E22, 2.1529302155822144E22, -5.812350493487406E21, -2.6490036632430644E21, 2.0124789503099836E22, -5.628889199470962E22, 1.817137975518054E22, 3.0226443176915864E22, 4.347543439762878E22, 6.79904179227573E22, 3.2472269682978935E22, -3.2355356266084516E22, -2.2461337287038467E22, -4.725876632403073E21, -5.256907260476178E21, 3.215849111699565E21, -2.3002827242898782E21, -4.58442826779579E22, -3.092725763036667E22, 1.5577026971188595E22, -3.793972509285752E22, 2.847651139701318E21, -6.314957393724579E21, -2.9267458318245156E22,
        };

        net = new FeedForwardNeuralNet(weights);
    }

    public boolean basicFightMicro(RobotInfo[] nearByEnemies) throws GameActionException
    {
        if (nearByEnemies != null && nearByEnemies.length > 0)
        {
            for (int i = 0; i < nearByEnemies.length; i++)
            {
                if (rc.canAttackLocation(nearByEnemies[i].location))
                {
                    rc.attackLocation(nearByEnemies[i].location);
                    return true;
                }
            }
        }

        return false;
    }

    // TODO: create fight micro using a trained neural net
    public boolean basicNetFightMicro(RobotInfo[] nearByEnemies, RobotInfo[] nearByAllies, RobotInfo[] enemies, RobotInfo[] allies, MapLocation target) throws GameActionException
    {
        if (enemies == null || enemies.length == 0)
        {
            return false;
        }

        double alliedHealth = 0;

        for (int i = allies.length; --i >= 0; )
        {
            alliedHealth += allies[i].health;
        }

        double enemyHealth = 0;

        for (int i = enemies.length; --i >= 0; )
        {
            enemyHealth += enemies[i].health;
        }

        double[] inputs = new double[] {
                enemies.length / 10,
                allies.length / 10,
                alliedHealth / 10,
                enemyHealth / 10,
                nearByEnemies.length / 10,
                nearByAllies.length / 10
        };

        Direction dir;

        boolean retreat = false;
        boolean advance = false;
        boolean cluster = false;
        boolean pursue = false;
        double[] output = net.compute(inputs);

        // retreat
        if (output[0] > 0.5)
        {
            retreat = true;
        }

        // advance
        if (output[1] > 0.5) {
            advance = true;
        }

        // cluster
        if (output[2] > 0.5) {
            cluster = true;
        }

        // pursue
        if (output[3] > 0.5) {
            pursue = true;
        }

        if (rc.isCoreReady()) {
            if (retreat) {
                MapLocation enemy = new MapLocation((int) inputs[0] + rc.getLocation().x, (int) inputs[1] + rc.getLocation().y);
                dir = rc.getLocation().directionTo(enemy).opposite();
                FightMicroUtilites.moveDir(rc, dir);
            }

            if (rc.isCoreReady() && cluster) {
                MapLocation ally = new MapLocation((int) inputs[2] + rc.getLocation().x, (int) inputs[3] + rc.getLocation().y);
                dir = rc.getLocation().directionTo(ally);
                FightMicroUtilites.moveDir(rc, dir);
            }

            if (rc.isCoreReady() && advance) {
                dir = FightMicroUtilites.getDir(rc, target);
                FightMicroUtilites.moveDir(rc, dir);
            }

            if (rc.isCoreReady() && pursue) {
                MapLocation enemy = new MapLocation((int) inputs[0] + rc.getLocation().x, (int) inputs[1] + rc.getLocation().y);
                dir = rc.getLocation().directionTo(enemy);
                FightMicroUtilites.moveDir(rc, dir);
            }
        }


        if (rc.isWeaponReady() && nearByEnemies.length > 0) {
            try {
                RobotInfo weakEnemy = FightMicroUtilites.findWeakestEnemy(nearByEnemies);
                if (weakEnemy != null) {
                    MapLocation attackSpot = weakEnemy.location;
                    if (attackSpot != null && rc.canAttackLocation(attackSpot)) {
                        rc.attackLocation(attackSpot);
                    }
                }
            } catch (Exception e) {
                System.out.println("failed when trying to attack");
                e.printStackTrace();
            }
        }
        return true;
    }
}
