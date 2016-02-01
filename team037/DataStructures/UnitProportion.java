package team037.DataStructures;

import battlecode.common.RobotType;

/**
 * Container for estimating army compositions.
 * Created by joshua on 1/22/16.
 */
public class UnitProportion
{
    public double scouts;
    public double soldiers;
    public double guards;
    public double vipers;
    public double turrets;
    public double totalProportion;

    public double actualScouts;
    public double actualSoldiers;
    public double actualGuards;
    public double actualVipers;
    public double actualTurrets;

    public UnitProportion(double scouts, double soldiers, double guards, double vipers, double turrets)
    {
        this.scouts = scouts;
        this.soldiers = soldiers;
        this.guards = guards;
        this.vipers = vipers;
        this.turrets = turrets;
        totalProportion = scouts + soldiers + guards + vipers + turrets;
    }

    public RobotType nextBot()
    {
        double totalActual = actualScouts + actualSoldiers + actualGuards + actualVipers + actualTurrets;
        if(totalActual == 0)
        {
            totalActual++;
        }

        double worst = 100;
        double tempWorst;
        RobotType toReturn = null;

        if(scouts > 0)
        {
            worst = (actualScouts / totalActual) / (scouts / totalProportion);
            toReturn = RobotType.SCOUT;
        }

        if(soldiers > 0)
        {
            tempWorst = (actualSoldiers / totalActual) / (soldiers / totalProportion);
            if(tempWorst < worst)
            {
                worst = tempWorst;
                toReturn = RobotType.SOLDIER;
            }
        }
        if(guards > 0)
        {
            tempWorst = (actualGuards / totalActual) / (guards / totalProportion);
            if(tempWorst < worst)
            {
                worst = tempWorst;
                toReturn = RobotType.GUARD;
            }
        }
        if(vipers > 0)
        {
            tempWorst = (actualVipers / totalActual) / (vipers / totalProportion);
            if(tempWorst < worst)
            {
                worst = tempWorst;
                toReturn = RobotType.VIPER;
            }
        }
        if(turrets > 0)
        {
            tempWorst = (actualTurrets / totalActual) / (turrets / totalProportion);
            if(tempWorst < worst)
            {
                toReturn = RobotType.TURRET;
            }
        }

        return toReturn;
    }

    public void addBot(RobotType bot)
    {
        switch(bot)
        {
            case SCOUT:
                actualScouts++;
                break;
            case SOLDIER:
                actualSoldiers++;
                break;
            case GUARD:
                actualGuards++;
                break;
            case VIPER:
                actualVipers++;
                break;
            case TURRET:
                actualTurrets++;
                break;
        }
    }
}
