package team037.Messages;

import battlecode.common.MapLocation;
import battlecode.common.Team;
import team037.Enums.CommunicationType;
import team037.Utilites.CommunicationUtilities;

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
 *   val1 is the maxX indicator
 *   loc1x is the x coordinate
 *   val2 is the maxX
 *   val3 is the maxY indicator
 *   loc1y is the y coordinate
 *   val4 is the maxY
 *
 * turret SupportFormat
 *      opcode is the specific opcode
 *      val1 is the x cord of enemy
 *      val2 is the y cord of enemy
 */
public class Communication
{
    public CommunicationType opcode;
    public int signalID;
    public MapLocation signalLoc;
    public Team signalTeam;

    //Prints values by opcode
    public void print()
    {
        System.out.println("opcode: " + opcode.toString());
    }

    public int[] getValues()
    {
        return new int[]{CommunicationType.toInt(opcode)};
    }

    public void setValues(int[] values)
    {
        opcode = CommunicationType.fromInt(values[0]);
    }

    public int[] getLengths()
    {
        return new int[]{CommunicationUtilities.opcodeSize};
    }

    public void setSignalValues(int signalID, MapLocation signalLoc, Team signalTeam)
    {
        this.signalID = signalID;
        this.signalLoc = signalLoc;
        this.signalTeam = signalTeam;
    }
}
