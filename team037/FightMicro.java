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
                1.193165124354791, 1.8287528707900687, -2.1023990942774207, -0.003556992400173195, -0.22606770937808926, -0.05308618118442325, 1.6913539059084814, -1.1489305388699802, -0.5052375108263749, 1.6022198629141418, 0.6756372223652438, -0.6549288283323469, 0.4386801594808763, -0.4989953324166482, 0.29965548862916, 1.9587384240484813, -1.0211360924767046, 0.6086968248547991, -1.4812899186545896, -0.21218374884528074, 0.16296987564646775, 1.4932032571043055, 1.0848678486236258, 0.49896387548672483, 0.27787841274809166, 0.4459950888154327, 1.4488618785299456, -1.2433936634339036, 1.2663678282072595, 1.8115059460893796, -0.8181759762408182, 2.3694411110905276, 0.47259225083484163, 0.3043034084051106, -0.7470583038065843, 0.3722681581525898, -0.46230725341755174, -0.19538855382987272, -3.0636143345754556, 1.1549209229463202, -0.27255184783408826, 1.8692018357585658, 0.8166911731949884, 0.41340598157311315, -0.43218038986540563, -0.035203345308934694, 0.14779397970211702, -2.568771521495324, 1.4116741446655878, 2.926199422765602, 0.9863678204198452, -0.2020657987983662, 0.818699682637316, -0.35838330413786973, 0.15129032298452078, -1.838163854231614, -1.651265874593832, 0.6369746015231079, 2.0968310774390795, 0.32071089336388064, 0.17877702860735822, -0.8043455088168293, 1.0868702666819388, 1.096393429206858, 0.0436513397794441, 0.21670341544241198, -0.0810691032028564, 0.4018507188078776, 1.2665593552909507, 0.970338826792109, 1.611938136192398, 1.5156478008460894, 0.2158014910966273, 1.6459437269227715, 1.3845619480211235, -0.5468418353085547, 0.2851569691182983, 1.105607323938716, 0.522235090987872, 1.2486713502794078, -0.997226289848698, 0.35034683833064006, -0.5153129822254544, 1.1502876470997165, -1.477072680312429, -1.217201536543857, -1.8355767990612832, -0.00415968639680557, 1.5556486814413575, -0.2728010789714863, -1.3222778834037585, -1.290831058952724, -0.8564376988566135, -0.2866619827866477, -2.3356543374930228, -0.506773888653692, -0.04803882896821189, -0.618960969917605, 1.2252487124537126, 1.5052466567705647, 0.7361631850199085, -1.4023771606670214, 1.4049219887235134, -0.4013479582266303, 1.9270038551291393, 0.27327816781067793, 0.8926653679091108, 0.5838429676935981, -0.08218981798264777, 0.48659463936591457, -1.4701113174522429, -0.6770407864093919, -0.48282969815873755, 0.36956102013284037, -1.366483493903653, -0.52144626849086, 3.2205796331605763, -0.3818967201226374, -0.5663897892391734, 0.10101696291741227
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
