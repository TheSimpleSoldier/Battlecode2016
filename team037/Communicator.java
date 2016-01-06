package team037;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import team037.Utilites.Communication;
import team037.Utilites.CommunicationUtilities;

/**
 * Created by joshua on 1/5/16.
 */
public class Communicator
{
    RobotController rc;

    public Communicator(RobotController rc)
    {
        this.rc = rc;
    }

    public Communication[] processCommunications()
    {
        Signal[] signals = rc.emptySignalQueue();
        Communication[] communications = new Communication[signals.length];
        for(int k = signals.length; --k >= 0;)
        {
            if(rc.getTeam().equals(signals[k].getTeam()))
            {
                communications[k] = CommunicationUtilities.readCommunication(signals[k]);
            }
            else
            {
                communications[k] = CommunicationUtilities.readEnemyCommunication(signals[k]);
            }
        }

        return communications;
    }

    public void sendCommunication(int radius, Communication communication) throws GameActionException
    {
        int[] message = CommunicationUtilities.createCommunication(communication);
        rc.broadcastMessageSignal(message[0], message[1], radius);
    }
}
