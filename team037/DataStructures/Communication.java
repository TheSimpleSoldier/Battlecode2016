package team037.DataStructures;

import battlecode.common.RobotType;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;

/**
 * Created by joshua on 1/5/16.
 * Holds information on what a communication is saying
 *
 * for i type communications the following need to be filled out:
 *     type
 *     id
 *     btype
 *     x
 *     y
 *
 * for c type communication the following need to be filled out:
 *     type
 *     id
 *     btype
 *     stype
 *     ntype
 */
public class Communication
{
    public CommunicationType type; //The type of communication
    public int id;                 //The id to be used for a specific mission change
    public RobotType bType;        //The basic type of the robot
    public Bots sType;             //The type to be used for a blanket mission change
    public Bots nType;             //The type to turn in to
    public int x;                  //X location
    public int y;                  //y location
    public int[] coords;           // list of integers

    public void print()
    {
        System.out.println("op: " + type.toString());
        System.out.println("id: " + id);
        System.out.println("bot type: " + bType);
        System.out.println("start type: " + sType);
        System.out.println("new type: " + nType);
        System.out.println("loc: (" + x + ", " + y + ")");
    }
}
