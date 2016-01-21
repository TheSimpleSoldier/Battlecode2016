package team037.Units.Scavenger;

import battlecode.common.*;
import team037.Messages.Communication;
import team037.ScoutMapKnowledge;
import team037.Units.ScoutBomb.ScoutBombScout;

/**
 * Created by davej on 1/20/2016.
 */
public class ScavengerScout extends ScoutBombScout {

    public static boolean BOMB;
    public static MapLocation archonLastLoc;
    public static RobotInfo myArchon;

    public ScavengerScout(RobotController rc) {
        super(rc);
        if (zombies != null && zombies.length > 0 && !zombies[0].type.equals(RobotType.ZOMBIEDEN)) {
            BOMB = true;
        } else {
            BOMB = false;
            try {
                myArchon = null;
                int scouts = 0;
                for (int i = 8; --i >= 0; ) {
                    MapLocation loc = currentLocation.add(dirs[i]);
                    if (rc.isLocationOccupied(loc)) {
                        RobotInfo checkBot = rc.senseRobotAtLocation(loc);
                        if (checkBot.type.equals(RobotType.ARCHON)) {
                            if (myArchon == null)
                                myArchon = checkBot;
                            else {
                                myArchon = null;
                                break;
                            }
                        } else if (checkBot.type.equals(RobotType.SCOUT)) {
                            scouts++;
                        }
                    }
                }
                if (scouts > 4) {
                    BOMB = true;
                }
            } catch (Exception e) {}

            if (myArchon == null) {
                BOMB = true;
            } else {
                archonLastLoc = myArchon.location;
                if (rc.isCoreReady() && rc.canMove(currentLocation.directionTo(archonLastLoc).opposite())) {
                    try {
                        rc.move(currentLocation.directionTo(archonLastLoc).opposite());
                    } catch (Exception e) {}
                }
            }
        }
    }

    public boolean act() throws GameActionException
    {
        if (BOMB || zombies.length > 0 && !zombies[0].type.equals(RobotType.ZOMBIEDEN)) {
            BOMB = true;
            return super.act();
        }

        boolean moved = scavengerMove();

        return moved;
    }

    private boolean scavengerMove() throws GameActionException {
        if (myArchon == null || !rc.canSenseRobot(myArchon.ID)) {
            BOMB = true;
            return super.act();
        }

        int distance = currentLocation.distanceSquaredTo(myArchon.location);
        if (!rc.isCoreReady() ||
                distance > 2 && distance < 13 &&
                        archonLastLoc.equals(myArchon.location)) return false;


        if (distance < 3) {
            try {
                rc.move(currentLocation.directionTo(archonLastLoc).opposite());
            } catch (Exception e) {}
        }

        Direction moved = archonLastLoc.directionTo(myArchon.location);
        if (rc.canMove(moved)) {
            rc.move(moved);
            archonLastLoc = myArchon.location;
            return true;
        } else {
            MapLocation adjacent = currentLocation.add(moved.rotateRight());
            if (adjacent.distanceSquaredTo(myArchon.location) < 11 && rc.canMove(currentLocation.directionTo(adjacent))) {
                rc.move(currentLocation.directionTo(adjacent));
                archonLastLoc = myArchon.location;
                return true;
            }
            adjacent = currentLocation.add(moved.rotateLeft());
            if (adjacent.distanceSquaredTo(myArchon.location) < 11 && rc.canMove(currentLocation.directionTo(adjacent))) {
                rc.move(currentLocation.directionTo(adjacent));
                archonLastLoc = myArchon.location;
                return true;
            }
        }

        return false;
    }

//    @Override
//    public void sendMessages()
//    {
//        try {
//            if (mKnowledge.firstFoundEdge && msgsSent < 20) {
//                Communication com = mKnowledge.getMapBoundsCommunication();
//                communicator.sendCommunication(ScoutMapKnowledge.getMaxRange(), com);
//                msgsSent++;
//                mKnowledge.firstFoundEdge = false;
//                mKnowledge.updated = false;
//            }
//            if (mKnowledge.updated && msgsSent < 20) {
//                Communication com = mKnowledge.getMapBoundsCommunication();
//                communicator.sendCommunication(ScoutMapKnowledge.getRange(), com);
//                msgsSent++;
//                mKnowledge.updated = false;
//            }
//            msgArchons();
//            msgParts();
//            msgDens();
//            msgRubble();
//        } catch (GameActionException e) {}
//    }
}
