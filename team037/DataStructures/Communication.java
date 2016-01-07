package team037.DataStructures;

import battlecode.common.RobotType;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;

/**
 * Created by joshua on 1/6/16.
 * Holds all information about a communication
 * The following is the different types of communications and what
 * the values are for:
 * i-format(den, parts, oenemy, senemy, sarchon, szombie, sden, sparts)
 *   opcode is the specific opcode
 *   val1 can be whatever info(number of parts for the parts op, id of bot sending for others)
 *   the location of the subject is in loc1X and loc1Y
 *
 * bi-format(enemy)
 *   opcode is the specific opcode
 *   val1 is the id of the bot
 *   rType1 is the type of the bot
 *   the location of the bot is in loc1X and loc1Y
 *
 * cm-format(changemission)
 *   opcode is the specific opcode
 *   val1 is the id(0 if unimportant)
 *   bType1 is the type of bot(only used if id is 0)
 *   bType2 is what bot to change to
 *
 * im-format(initial mission)
 *   opcode is the specific opcode
 *   val1 is the id(0 for blanket to all base bots)
 *   bType1 is what bot to change to
 *
 * mk-format(map bounds)
 *   opcode is the specific opcode
 *   val1 is the width indicator
 *   loc1x is the x coordinate
 *   val2 is the width
 *   val3 is the height indicator
 *   loc1y is the y coordinate
 *   val4 is the height
 */
public class Communication
{
    public CommunicationType opcode;
    public int val1;
    public int val2;
    public int val3;
    public int val4;
    public RobotType rType1;
    public RobotType rType2;
    public Bots bType1;
    public Bots bType2;
    public int loc1X;
    public int loc1Y;
    public int loc2X;
    public int loc2Y;

    //Prints values by opcode
    public void print()
    {
    }

    //Prints all values
    public void printAll()
    {
        System.out.println("opcode: " + opcode.toString());
        System.out.println("val1: " + val1);
        System.out.println("val2: " + val2);
        System.out.println("val3: " + val3);
        System.out.println("val4: " + val4);
        if(rType1 != null)
        {
            System.out.println("rtype1: " + rType1.toString());
        }
        if(rType2 != null)
        {
            System.out.println("rtype2: " + rType2.toString());
        }
        if(bType1 != null)
        {
            System.out.println("btype1: " + bType1.toString());
        }
        if(bType2 != null)
        {
            System.out.println("btype2: " + bType2.toString());
        }
        System.out.println("loc1: (" + loc1X + ", " + loc1Y + ")");
        System.out.println("loc2: (" + loc2X + ", " + loc2Y + ")");
    }
}
