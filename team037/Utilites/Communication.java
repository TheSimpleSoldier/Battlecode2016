package team037.Utilites;

import battlecode.common.RobotType;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;

/**
 * Created by joshua on 1/5/16.
 * Holds information on what a communication is saying
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
}
