package team037.DataStructures;

import battlecode.common.RobotType;

/**
 * Created by joshua on 1/22/16.
 */
public class UnitProportion
{
    public int scouts;
    public int soldiers;
    public int guards;
    public int vipers;
    public int turrets;
    public int totalProportion;

    public int actualScouts;
    public int actualSoldiers;
    public int actualGuards;
    public int actualVipers;
    public int actualTurrets;

    public UnitProportion(int scouts, int soldiers, int guards, int vipers, int turrets)
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
        int totalActual = actualScouts + actualSoldiers + actualGuards + actualVipers + actualTurrets;

        int worst = 100;
        int tempWorst;
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
