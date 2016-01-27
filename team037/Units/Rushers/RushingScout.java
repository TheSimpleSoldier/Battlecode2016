package team037.Units.Rushers;

import battlecode.common.*;
import team037.Enums.CommunicationType;
import team037.Messages.BotInfoCommunication;
import team037.Messages.Communication;
import team037.Navigation;
import team037.Navigator;
import team037.Unit;
import team037.Units.PacMan.PacMan;
import team037.Units.Scouts.ScoutingScout;

/**
 * Created by davej on 1/19/2016.
 */
public class RushingScout extends ScoutingScout implements PacMan {

    private int retreatCall = 0;

    public RushingScout(RobotController rc) {
        super(rc);
    }

    public boolean fightZombies() throws GameActionException {
        if (zombies.length > 0 && zombies[0].type != RobotType.ZOMBIEDEN) {
            return runAway(null,false,true);
        }

        return super.fightZombies();
    }

    public void sendMessages() throws GameActionException {
        int offensiveEnemies = 0;
        MapLocation foundArchon = null;

        if (offensiveEnemies > 3 && (rc.getRoundNum() - retreatCall) > 25 && msgsSent < 20)
        {
            retreatCall = rc.getRoundNum();
            Communication distressCall = new BotInfoCommunication();
            if (foundArchon == null) {
                foundArchon = currentLocation;
            }
            distressCall.setValues(new int[]{CommunicationType.toInt(CommunicationType.ARCHON_DISTRESS), 0, 0, id, foundArchon.x, foundArchon.y});
            communicator.sendCommunication(mapKnowledge.getRange(), distressCall);
            msgsSent++;
        }
    }

    public void collectData() throws GameActionException {
        super.collectData();
        if (zombies != null) {
            int offensiveEnemies = 0;
            MapLocation foundArchon = null;
            for (int i =- zombies.length; --i >= 0;) {
                if (zombies[i].type.equals(RobotType.ZOMBIEDEN)) {
                    retreatCall = rc.getRoundNum();
                    Communication distressCall = new BotInfoCommunication();
                    if (foundArchon == null) {
                        foundArchon = currentLocation;
                    }
                    distressCall.setValues(new int[]{CommunicationType.toInt(CommunicationType.ARCHON_DISTRESS), 0, 0, id, foundArchon.x, foundArchon.y});
                    communicator.sendCommunication(mapKnowledge.getRange(), distressCall);
                    msgsSent++;
                }
            }
        }
    }
}
