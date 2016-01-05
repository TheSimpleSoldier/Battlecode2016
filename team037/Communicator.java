package team037;

import battlecode.common.RobotController;
import battlecode.common.Signal;
import team037.Utilites.Communication;
import team037.Utilites.CommunicationUtilities;

/**
 * Created by joshua on 1/5/16.
 */
public class Communicator
{
    public Communication[] processCommunications(RobotController rc)
    {
        Signal[] signals = rc.emptySignalQueue();
        Communication[] communications = new Communication[signals.length];
        for(int k = signals.length; --k >= 0;)
        {
            communications[k] = CommunicationUtilities.readCommunication(signals[k]);
        }

        return communications;
    }
}
