package team037;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import team037.Messages.Communication;
import team037.Utilites.CommunicationUtilities;

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
                if(signals[k].getMessage() == null)
                {
                    communications[k] = CommunicationUtilities.readSimpleCommunication(signals[k], rc.getRoundNum());
                }
                else
                {
                    communications[k] = CommunicationUtilities.readCommunication(signals[k]);
                }
            }
            else
            {
                communications[k] = CommunicationUtilities.readEnemyCommunication(signals[k]);
            }
        }

        return communications;
    }

    /**
     * For scouts and archons to send messages
     * @param radius radius to broadcast
     * @param communication the communication to send, will be translated, use the following as a guide
     *     message formats:
     *     i-type(info): den, parts, enemy
     *     required: type, id, btype, x, y
     *
     *     c-type(change mission): mission
     *     required: type, id(0 if doesn't matter), btype, sbot, nbot
     * @throws GameActionException
     */
    public void sendCommunication(int radius, Communication communication) throws GameActionException
    {
        int[] message = CommunicationUtilities.createCommunication(communication);
        rc.broadcastMessageSignal(message[0], message[1], radius);
    }

    /**
     * For other bots who can't send messages.
     * Keep running this method till it returns true
     * @param radius radius to broadcast
     * @param communication the communication to send, use the following fo a guide
     *     for enemy archons: type is enemy and btype is archon
     *     for enemies(not archon): type is enemy and btype is soldier
     *     for zombies(not dens): type is enemy and btype is standardzombie
     *     for zombie dens: type is den and btype is zombieden
     *     for part stashes: type is parts and btype is soldier
     * @return true if it sent, false if it didn't
     * @throws GameActionException
     */
    public boolean sendSimpleCommunication(int radius, Communication communication) throws GameActionException
    {
        if(CommunicationUtilities.shouldCommunicateSimple(communication, rc.getRoundNum()))
        {
            rc.broadcastSignal(radius);
            return true;
        }

        return false;
    }

    /**
     * This function will send a simple communication with radius
     *
     * @param radius
     * @return
     * @throws GameActionException
     */
    public void forceSendSimpleCommunication(int radius) throws GameActionException
    {
        rc.broadcastSignal(radius);
    }

    /**
     * This method returns the core and weapon delay cost of sending a signal
     *
     * @param radius
     * @param sightRange
     * @return
     */
    public double coreCostForMsg(int radius, int sightRange)
    {
        double cost = radius / sightRange;
        cost -= 2;

        if (cost <= 0)
        {
            cost = 0;
        }

        return 0.05 + 0.03 * cost;
    }
}
