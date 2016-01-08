package team037.Units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.SimpleBotInfoCommunication;

/**
 * Created by joshua on 1/8/16.
 */
public class AttackingSoldier extends BaseSoldier
{
    public AttackingSoldier(RobotController rc)
    {
        super(rc);
        target = null;
    }

    @Override
    public void handleMessages() throws GameActionException
    {
        super.handleMessages();
        if(target == null && nearByEnemies.length == 0)
        {
            for(int k = 0; k < communications.length; k++)
            {
                CommunicationType opcode = communications[k].opcode;

                if(opcode == CommunicationType.ENEMY)
                {
                    BotInfoCommunication comm = (BotInfoCommunication) communications[k];
                    if(comm.type == RobotType.ARCHON)
                    {
                        target = new MapLocation(comm.x, comm.y);
                        navigator.setTarget(target);
                        break;
                    }
                }
                else if(opcode == CommunicationType.SARCHON)
                {
                    SimpleBotInfoCommunication comm = (SimpleBotInfoCommunication) communications[k];
                    target = new MapLocation(comm.x, comm.y);
                    navigator.setTarget(target);
                    break;
                }
            }
        }
    }

    @Override
    public void collectData() throws GameActionException
    {
        super.collectData();
        if(nearByEnemies.length > 0 || rc.getLocation().equals(target))
        {
            target = null;
        }
    }

    @Override
    public boolean takeNextStep() throws GameActionException
    {
        if(target == null)
        {
            navigator.setTarget(rc.getLocation());
        }
        else
        {
            navigator.setTarget(target);
        }
        return navigator.takeNextStep();
    }
}
