package team037.Units.PacMan;

import battlecode.common.*;
import team037.Communicator;
import team037.Enums.Bots;
import team037.Enums.CommunicationType;
import team037.MapKnowledge;
import team037.Messages.Communication;
import team037.Messages.MissionCommunication;
import team037.Messages.SimpleBotInfoCommunication;
import team037.Unit;
import team037.Utilites.TurretMemory;

public class PacManUtils {

    public static final RobotType SCOUT = RobotType.SCOUT;
    public static RobotInfo countermeasure = null;
    public static RobotInfo scout1 = null;
    public static RobotInfo scout2 = null;
    public static RobotInfo scout3 = null;
    public static double[] rubble;

    public static MapLocation getClosestHarmfulUnit(RobotInfo[] badBots) {
        MapLocation currentLocation = Unit.currentLocation;
        RobotInfo[] allies = Unit.allies;
        MapLocation closestUnitLocation = null;
        if (badBots != null) {
            for (int i = badBots.length; --i >= 0; ) {
                RobotType type = badBots[i].type;

                if (type.equals(RobotType.ZOMBIEDEN) || type.equals(RobotType.SCOUT)) continue;

                MapLocation nextBadBot = badBots[i].location;
                int myDistance = currentLocation.distanceSquaredTo(nextBadBot);

                for (int j = allies.length; --j >= 0; ) {
                    if (myDistance > allies[j].location.distanceSquaredTo(nextBadBot)) {
                        myDistance = -1;
                        break;
                    }
                }

                if (myDistance > 0) {
                    closestUnitLocation = nextBadBot;
                    break;
                }
            }
        }
        
        return closestUnitLocation;
    }

    public static MapLocation centerOfMass(RobotInfo[] bots) {
        if (bots == null || bots.length == 0)
            return null;

        int x = 0, y = 0;
        for (int i = bots.length; --i >= 0;) {
            MapLocation location = bots[i].location;
            x += location.x;
            y += location.y;
        }

        x /= bots.length;
        y /= bots.length;

        return new MapLocation(x,y);
    }

    public boolean deployScavengerScout() throws GameActionException {
        RobotController rc = Unit.rc;
        MapLocation currentLocation = Unit.currentLocation;

        if (scout1 != null && (!rc.canSenseRobot(scout1.ID) || currentLocation.distanceSquaredTo(scout1.location) > 25)) {
            scout1 = null;
        } else if (scout1 != null) {
            scout1 = rc.senseRobot(scout1.ID);
        }

        if (scout2 != null && (!rc.canSenseRobot(scout2.ID) || currentLocation.distanceSquaredTo(scout2.location) > 25)) {
            scout2 = null;
        } else if (scout2 != null) {
            scout2 = rc.senseRobot(scout2.ID);
        }

        if (scout3 != null && (!rc.canSenseRobot(scout3.ID) || currentLocation.distanceSquaredTo(scout3.location) > 25)) {
            scout3 = null;
        } else if (scout3 != null) {
            scout3 = rc.senseRobot(scout3.ID);
        }

        if (!(rc.isCoreReady() && rc.hasBuildRequirements(SCOUT))) {
            return false;
        }

        if (scout1 == null) {
            if (rc.canBuild(Direction.NORTH,SCOUT)) {
                rc.build(Direction.NORTH,SCOUT);
                scout1 = rc.senseRobotAtLocation(currentLocation.add(Direction.NORTH));
                sendInitialMessages(Direction.NORTH,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            } else if (rc.canBuild(Direction.NORTH_WEST,SCOUT)) {
                rc.build(Direction.NORTH_WEST,SCOUT);
                scout1 = rc.senseRobotAtLocation(currentLocation.add(Direction.NORTH_WEST));
                sendInitialMessages(Direction.NORTH_WEST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            } else if (rc.canBuild(Direction.NORTH_EAST,SCOUT)) {
                rc.build(Direction.NORTH_EAST,SCOUT);
                scout1 = rc.senseRobotAtLocation(currentLocation.add(Direction.NORTH_EAST));
                sendInitialMessages(Direction.NORTH_EAST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            }
        } else if (scout2 == null) {
            if (rc.canBuild(Direction.EAST,SCOUT)) {
                rc.build(Direction.EAST,SCOUT);
                scout2 = rc.senseRobotAtLocation(currentLocation.add(Direction.EAST));
                sendInitialMessages(Direction.EAST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            } else if (rc.canBuild(Direction.SOUTH_EAST,SCOUT)) {
                rc.build(Direction.SOUTH_EAST,SCOUT);
                scout2 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH_EAST));
                sendInitialMessages(Direction.SOUTH_EAST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            } else if (rc.canBuild(Direction.SOUTH,SCOUT)) {
                rc.build(Direction.SOUTH,SCOUT);
                scout2 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH));
                sendInitialMessages(Direction.SOUTH,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            }
        } else if (scout3 == null) {
            if (rc.canBuild(Direction.WEST,SCOUT)) {
                rc.build(Direction.WEST,SCOUT);
                scout3 = rc.senseRobotAtLocation(currentLocation.add(Direction.WEST));
                sendInitialMessages(Direction.WEST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            } else if (rc.canBuild(Direction.SOUTH_WEST,SCOUT)) {
                rc.build(Direction.SOUTH_WEST,SCOUT);
                scout3 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH_WEST));
                sendInitialMessages(Direction.SOUTH_WEST,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            } else if (rc.canBuild(Direction.SOUTH,SCOUT)) {
                rc.build(Direction.SOUTH,SCOUT);
                scout3 = rc.senseRobotAtLocation(currentLocation.add(Direction.SOUTH));
                sendInitialMessages(Direction.SOUTH,RobotType.SCOUT,Bots.SCAVENGERSCOUT,false);
                return true;
            }
        }
        return false;
    }

    public static boolean canDeployCountermeasure() {
        if ((countermeasure == null || !Unit.rc.canSenseRobot(countermeasure.ID))) {
            countermeasure = null;
            return true;
        }
        return false;
    }

    public static Direction getDeployDirection(Direction toEnemy) {
        RobotController rc = Unit.rc;
        RobotType nextType = RobotType.GUARD;
        if (rc.canBuild(toEnemy, nextType)) {
            return toEnemy;
        } else if (rc.canBuild(toEnemy.rotateLeft(), nextType)) {
            return toEnemy.rotateLeft();
        } else if (rc.canBuild(toEnemy.rotateRight(), nextType)) {
            return toEnemy.rotateRight();
        } else if (rc.canBuild(toEnemy.rotateLeft().rotateLeft(), nextType)) {
            return toEnemy.rotateLeft().rotateLeft();
        } else if (rc.canBuild(toEnemy.rotateRight().rotateRight(), nextType)) {
            return toEnemy.rotateRight().rotateRight();
        }

        return null;
    }

    public static RobotInfo deployCountermeasure(Direction toEnemy) throws GameActionException {

        RobotController rc = Unit.rc;


        if (toEnemy == null || toEnemy == Direction.NONE || toEnemy == Direction.OMNI || !rc.isCoreReady() || !rc.hasBuildRequirements(RobotType.GUARD)) {
            return null;
        }

        toEnemy = getDeployDirection(toEnemy);

        if (toEnemy == null) {
            return null;
        }
        rc.build(toEnemy,RobotType.GUARD);

        sendInitialMessages(toEnemy);

        countermeasure = rc.senseRobotAtLocation(Unit.currentLocation.add(toEnemy));

        return countermeasure;
    }

    public static void sendInitialMessages(Direction dir, RobotType nextType, Bots nextBot, boolean sendDenLocs) throws GameActionException {
        RobotController rc = Unit.rc;
        Communicator communicator = Unit.communicator;
        MapKnowledge mapKnowledge = Unit.mapKnowledge;
        int id = rc.senseRobotAtLocation(rc.getLocation().add(dir)).ID;
        MissionCommunication communication = new MissionCommunication();
        communication.opcode = CommunicationType.CHANGEMISSION;
        communication.id = id;
        communication.newBType = nextBot;
        communicator.sendCommunication(2, communication);

        Communication mapBoundsCommunication = mapKnowledge.getMapBoundsCommunication();
        communicator.sendCommunication(2, mapBoundsCommunication);

        if (sendDenLocs) {
            for (int j = mapKnowledge.dens.length; --j >= 0; ) {
                MapLocation den = mapKnowledge.dens.array[j];

                if (den != null) {
                    Communication communicationDen = new SimpleBotInfoCommunication();
                    communicationDen.setValues(new int[]{CommunicationType.toInt(CommunicationType.SDEN), 0, den.x, den.y});
                    communicator.sendCommunication(2, communicationDen);
                }
            }
        }
    }

    public static void sendInitialMessages(Direction dir) throws GameActionException
    {
        int id = Unit.rc.senseRobotAtLocation(Unit.rc.getLocation().add(dir)).ID;
        MissionCommunication communication = new MissionCommunication();
        communication.opcode = CommunicationType.CHANGEMISSION;
        communication.id = id;
        communication.newBType = Bots.COUNTERMEASUREGUARD;
        Unit.communicator.sendCommunication(2, communication);

        Communication mapBoundsCommunication = Unit.mapKnowledge.getMapBoundsCommunication();
        Unit.communicator.sendCommunication(2, mapBoundsCommunication);

        for (int j = Unit.mapKnowledge.dens.length; --j>=0; )
        {
            MapLocation den = Unit.mapKnowledge.dens.array[j];

            if (den != null)
            {
                Communication communicationDen = new SimpleBotInfoCommunication();
                communicationDen.setValues(new int[] {CommunicationType.toInt(CommunicationType.SDEN), 0, den.x, den.y});
                Unit.communicator.sendCommunication(2, communicationDen);
            }
        }
    }

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param units      RobotInfo array whose locations will be used to find distances to the currentLocation variable
     * @param scalars    values used to scale the distances, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     *                   Set to 0 to ignore a specific direction, negative to attract unit to units, & positive to
     *                   repel unit from locations.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applyWeights(MapLocation currentLocation, int[] directions, RobotInfo[] units, double[] scalars) {
        if (units == null) {
            return directions;
        }

        int length = units.length;
        double resetScalars = scalars[0];

        for (int i = length; --i >= 0; ) {
            RobotInfo unit = units[i];
            scalars[0] = resetScalars;
            switch(unit.type) {
                case ARCHON:
                    if (unit.team.equals(Unit.opponent)) continue;
                    if (unit.team.equals(Team.NEUTRAL)) scalars[0] -= 10;
                    break;
                case SCOUT:
                case ZOMBIEDEN:
                    if (!unit.team.equals(Unit.us)) continue;
                    break;
                case TURRET:
                    if (!unit.team.equals(Unit.us)) {
                        TurretMemory.addTurretLocation(unit.location);
                    }
                    break;
                case FASTZOMBIE:
                    scalars[0] += .5;
                    break;

            }
            MapLocation nextUnit = unit.location;
            double add = (38 - nextUnit.distanceSquaredTo(currentLocation)) * scalars[0];
            double addAdjacent = add * scalars[1];
            double addPerp = addAdjacent * scalars[2];
            double addPerpAdj = addPerp * scalars[3];
            double addOpp = addPerpAdj * scalars[4];
            switch (currentLocation.directionTo(nextUnit)) {
                case NORTH:
                    directions[4] += addOpp;
                    directions[5] += addPerpAdj;
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    directions[3] += addPerpAdj;
                    break;
                case NORTH_EAST:
                    directions[5] += addOpp;
                    directions[6] += addPerpAdj;
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    directions[4] += addPerpAdj;
                    break;
                case EAST:
                    directions[6] += addOpp;
                    directions[7] += addPerpAdj;
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    directions[5] += addPerpAdj;
                    break;
                case SOUTH_EAST:
                    directions[7] += addOpp;
                    directions[0] += addPerpAdj;
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    directions[6] += addPerpAdj;
                    break;
                case SOUTH:
                    directions[0] += addOpp;
                    directions[1] += addPerpAdj;
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    directions[7] += addPerpAdj;
                    break;
                case SOUTH_WEST:
                    directions[1] += addOpp;
                    directions[2] += addPerpAdj;
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    directions[0] += addPerpAdj;
                    break;
                case WEST:
                    directions[2] += addOpp;
                    directions[3] += addPerpAdj;
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    directions[1] += addPerpAdj;
                    break;
                case NORTH_WEST:
                    directions[3] += addOpp;
                    directions[4] += addPerpAdj;
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
                    directions[2] += addPerpAdj;
                    break;
            }
        }

        return directions;
    }
    public static int[] applyPartsWeights(MapLocation currentLocation, int[] directions, MapLocation[] locations, double[] scalars) {

        int length = locations.length;

        for (int i = length; --i >= 0; ) {
            MapLocation nextLocation = locations[i];
            if (Unit.rc.senseRubble(nextLocation) > GameConstants.RUBBLE_OBSTRUCTION_THRESH) continue;
            double add = (38 - nextLocation.distanceSquaredTo(currentLocation)) * scalars[0];
            double addAdjacent = add * scalars[1];
            double addPerp = addAdjacent * scalars[2];
            switch (currentLocation.directionTo(nextLocation)) {
                case NORTH:
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    break;
                case NORTH_EAST:
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    break;
                case EAST:
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    break;
                case SOUTH_EAST:
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    break;
                case SOUTH:
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    break;
                case SOUTH_WEST:
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    break;
                case WEST:
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    break;
                case NORTH_WEST:
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
                    break;
            }
        }

        return directions;
    }

    public static int[] applyTurretWeights(MapLocation currentLocation, int[] directions, MapLocation[] locations, double[] scalars) {

        if (locations == null) {
            return directions;
        }

        int length = locations.length;

        for (int i = length; --i >= 0; ) {
            MapLocation nextLocation = locations[i];
            if (nextLocation == null) continue;
            double distance = 64 - nextLocation.distanceSquaredTo(currentLocation);
            if (distance <= 0) {
                continue;
            }
            double add = distance * scalars[0];
            double addAdjacent = add * scalars[1];
            double addPerp = addAdjacent * scalars[2];
            switch (currentLocation.directionTo(nextLocation)) {
                case NORTH:
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    break;
                case NORTH_EAST:
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    break;
                case EAST:
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    break;
                case SOUTH_EAST:
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    break;
                case SOUTH:
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    break;
                case SOUTH_WEST:
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    break;
                case WEST:
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    break;
                case NORTH_WEST:
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
                    break;
            }
        }

        return directions;
    }

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param units      RobotInfo array whose locations will be used to find distances to the currentLocation variable
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applySimpleWeights(MapLocation currentLocation, int[] directions, RobotInfo[] units) {

        if (units == null) {
            return directions;
        }

        int length = units.length;
        int range = Unit.sightRange + 1;

        for (int i = length; --i >= 0; ) {
            MapLocation nextUnit = units[i].location;
            double add = range - nextUnit.distanceSquaredTo(currentLocation);
            double addAdjacent = add / 2;
            double addPerp = addAdjacent / 2;
            switch (currentLocation.directionTo(nextUnit)) {
                case NORTH:
                    directions[6] += addPerp;
                    directions[7] += addAdjacent;
                    directions[0] += add;
                    directions[1] += addAdjacent;
                    directions[2] += addPerp;
                    break;
                case NORTH_EAST:
                    directions[7] += addPerp;
                    directions[0] += addAdjacent;
                    directions[1] += add;
                    directions[2] += addAdjacent;
                    directions[3] += addPerp;
                    break;
                case EAST:
                    directions[0] += addPerp;
                    directions[1] += addAdjacent;
                    directions[2] += add;
                    directions[3] += addAdjacent;
                    directions[4] += addPerp;
                    break;
                case SOUTH_EAST:
                    directions[1] += addPerp;
                    directions[2] += addAdjacent;
                    directions[3] += add;
                    directions[4] += addAdjacent;
                    directions[5] += addPerp;
                    break;
                case SOUTH:
                    directions[2] += addPerp;
                    directions[3] += addAdjacent;
                    directions[4] += add;
                    directions[5] += addAdjacent;
                    directions[6] += addPerp;
                    break;
                case SOUTH_WEST:
                    directions[3] += addPerp;
                    directions[4] += addAdjacent;
                    directions[5] += add;
                    directions[6] += addAdjacent;
                    directions[7] += addPerp;
                    break;
                case WEST:
                    directions[4] += addPerp;
                    directions[5] += addAdjacent;
                    directions[6] += add;
                    directions[7] += addAdjacent;
                    directions[0] += addPerp;
                    break;
                case NORTH_WEST:
                    directions[5] += addPerp;
                    directions[6] += addAdjacent;
                    directions[7] += add;
                    directions[0] += addAdjacent;
                    directions[1] += addPerp;
                    break;
            }
        }

        return directions;
    }

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param location   MapLocation to be used to find distance to the currentLocation variable
     * @param constants  values added to the directions array, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applyConstant(MapLocation currentLocation, int[] directions, MapLocation location, double[] constants) {
        if (location != null) {
            switch (currentLocation.directionTo(location)) {
                case NORTH:
                    directions[4] += constants[4];
                    directions[5] += constants[3];
                    directions[6] += constants[2];
                    directions[7] += constants[1];
                    directions[0] += constants[0];
                    directions[1] += constants[1];
                    directions[2] += constants[2];
                    directions[3] += constants[3];
                    break;
                case NORTH_EAST:
                    directions[5] += constants[4];
                    directions[6] += constants[3];
                    directions[7] += constants[2];
                    directions[0] += constants[1];
                    directions[1] += constants[0];
                    directions[2] += constants[1];
                    directions[3] += constants[2];
                    directions[4] += constants[3];
                    break;
                case EAST:
                    directions[6] += constants[4];
                    directions[7] += constants[3];
                    directions[0] += constants[2];
                    directions[1] += constants[1];
                    directions[2] += constants[0];
                    directions[3] += constants[1];
                    directions[4] += constants[2];
                    directions[5] += constants[3];
                    break;
                case SOUTH_EAST:
                    directions[7] += constants[4];
                    directions[0] += constants[3];
                    directions[1] += constants[2];
                    directions[2] += constants[1];
                    directions[3] += constants[0];
                    directions[4] += constants[1];
                    directions[5] += constants[2];
                    directions[6] += constants[3];
                    break;
                case SOUTH:
                    directions[0] += constants[4];
                    directions[1] += constants[3];
                    directions[2] += constants[2];
                    directions[3] += constants[1];
                    directions[4] += constants[0];
                    directions[5] += constants[1];
                    directions[6] += constants[2];
                    directions[7] += constants[3];
                    break;
                case SOUTH_WEST:
                    directions[1] += constants[4];
                    directions[2] += constants[3];
                    directions[3] += constants[2];
                    directions[4] += constants[1];
                    directions[5] += constants[0];
                    directions[6] += constants[1];
                    directions[7] += constants[2];
                    directions[0] += constants[3];
                    break;
                case WEST:
                    directions[2] += constants[4];
                    directions[3] += constants[3];
                    directions[4] += constants[2];
                    directions[5] += constants[1];
                    directions[6] += constants[0];
                    directions[7] += constants[1];
                    directions[0] += constants[2];
                    directions[1] += constants[3];
                    break;
                case NORTH_WEST:
                    directions[3] += constants[4];
                    directions[4] += constants[3];
                    directions[5] += constants[2];
                    directions[6] += constants[1];
                    directions[7] += constants[0];
                    directions[0] += constants[1];
                    directions[1] += constants[2];
                    directions[2] += constants[3];
                    break;
            }
        }

        return directions;
    }

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param location   MapLocation to be used to find distance to the currentLocation variable
     * @param constants  values added to the directions array, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applySimpleConstant(MapLocation currentLocation, int[] directions, MapLocation location, int[] constants) {
        if (location != null) {
            switch (currentLocation.directionTo(location)) {
                case NORTH:
                    directions[6] += constants[2];
                    directions[7] += constants[1];
                    directions[0] += constants[0];
                    directions[1] += constants[1];
                    directions[2] += constants[2];
                    break;
                case NORTH_EAST:
                    directions[7] += constants[2];
                    directions[0] += constants[1];
                    directions[1] += constants[0];
                    directions[2] += constants[1];
                    directions[3] += constants[2];
                    break;
                case EAST:
                    directions[0] += constants[2];
                    directions[1] += constants[1];
                    directions[2] += constants[0];
                    directions[3] += constants[1];
                    directions[4] += constants[2];
                    break;
                case SOUTH_EAST:
                    directions[1] += constants[2];
                    directions[2] += constants[1];
                    directions[3] += constants[0];
                    directions[4] += constants[1];
                    directions[5] += constants[2];
                    break;
                case SOUTH:
                    directions[2] += constants[2];
                    directions[3] += constants[1];
                    directions[4] += constants[0];
                    directions[5] += constants[1];
                    directions[6] += constants[2];
                    break;
                case SOUTH_WEST:
                    directions[3] += constants[2];
                    directions[4] += constants[1];
                    directions[5] += constants[0];
                    directions[6] += constants[1];
                    directions[7] += constants[2];
                    break;
                case WEST:
                    directions[4] += constants[2];
                    directions[5] += constants[1];
                    directions[6] += constants[0];
                    directions[7] += constants[1];
                    directions[0] += constants[2];
                    break;
                case NORTH_WEST:
                    directions[5] += constants[2];
                    directions[6] += constants[1];
                    directions[7] += constants[0];
                    directions[0] += constants[1];
                    directions[1] += constants[2];
                    break;
            }
        }

        return directions;
    }

    /**
     * Applies weights to an array of 8 integers (representing weights for each compass direction).
     * Uses the distance squared of each unit in the RobotInfo array to this bot's currentLocation variable.
     *
     * @param directions 8-integer array holding the weights for each direction {north, northeast, E, SE, S, SW, W, NW}
     * @param locations  MapLocations to be used to find distance to the currentLocation variable
     * @param constants  values added to the directions array, {currentLoc.directionTo(unit.location),
     *                   directionTo.rotateLeft&right, rotate 90 left&right, rotate 135 left&right, directionTo.opposite}.
     * @return directions array with weights applied. Minimum value is the ideal direction of movement.
     */
    public static int[] applySimpleConstants(MapLocation currentLocation, int[] directions, MapLocation[] locations, int[] constants) {
        if (locations == null) {
            return directions;
        }
        for (int i = locations.length; --i >= 0; ) {
            MapLocation location = locations[i];
            if (location != null) {
                switch (currentLocation.directionTo(location)) {
                    case NORTH:
                        directions[6] += constants[2];
                        directions[7] += constants[1];
                        directions[0] += constants[0];
                        directions[1] += constants[1];
                        directions[2] += constants[2];
                        break;
                    case NORTH_EAST:
                        directions[7] += constants[2];
                        directions[0] += constants[1];
                        directions[1] += constants[0];
                        directions[2] += constants[1];
                        directions[3] += constants[2];
                        break;
                    case EAST:
                        directions[0] += constants[2];
                        directions[1] += constants[1];
                        directions[2] += constants[0];
                        directions[3] += constants[1];
                        directions[4] += constants[2];
                        break;
                    case SOUTH_EAST:
                        directions[1] += constants[2];
                        directions[2] += constants[1];
                        directions[3] += constants[0];
                        directions[4] += constants[1];
                        directions[5] += constants[2];
                        break;
                    case SOUTH:
                        directions[2] += constants[2];
                        directions[3] += constants[1];
                        directions[4] += constants[0];
                        directions[5] += constants[1];
                        directions[6] += constants[2];
                        break;
                    case SOUTH_WEST:
                        directions[3] += constants[2];
                        directions[4] += constants[1];
                        directions[5] += constants[0];
                        directions[6] += constants[1];
                        directions[7] += constants[2];
                        break;
                    case WEST:
                        directions[4] += constants[2];
                        directions[5] += constants[1];
                        directions[6] += constants[0];
                        directions[7] += constants[1];
                        directions[0] += constants[2];
                        break;
                    case NORTH_WEST:
                        directions[5] += constants[2];
                        directions[6] += constants[1];
                        directions[7] += constants[0];
                        directions[0] += constants[1];
                        directions[1] += constants[2];
                        break;
                }
            }
        }
        return directions;
    }
}
