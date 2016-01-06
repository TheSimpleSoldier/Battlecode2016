package team037;

import battlecode.common.*;
import team037.DataStructures.AppendOnlyMapLocationSet;
import team037.Utilites.MapUtils;

public class MapKnowledge {

    // robot's knowledge of the map where MapLocation(x, y)
    public int minX = -1;
    public int maxX = -1;
    public int minY = -1;
    public int maxY = -1;

    public AppendOnlyMapLocationSet denLocations;
    public AppendOnlyMapLocationSet archonStartPosition;

    public MapKnowledge() {
        denLocations = new AppendOnlyMapLocationSet();
        archonStartPosition = new AppendOnlyMapLocationSet();
    }

    public void setMinX(int x) { minX = x; }
    public void setMaxX(int x) { maxX = x; }
    public void setMinY(int y) { minY = y; }
    public void setMaxY(int y) { maxY = y; }

    public void addDenLocation(MapLocation m) { denLocations.add(m); }
    public void addStartingArchonLocation(MapLocation m) { archonStartPosition.add(m); }

    public MapLocation nearestCorner(MapLocation m) {
        // TODO: implement this
        return null;
    }

    /**
     * In addition to reading in info from the comm, this should be called regularly
     */
    public void updateEdges(RobotController rc) throws GameActionException {
        if (minY == -1) {
            minY = MapUtils.senseEdge(rc, Direction.NORTH);
        }
        if (maxY == -1) {
            maxY = MapUtils.senseEdge(rc, Direction.SOUTH);
        }
        if (minX == -1) {
            minX = MapUtils.senseEdge(rc, Direction.EAST);
        }
        if (maxX == -1) {
            maxX = MapUtils.senseEdge(rc, Direction.WEST);
        }
    }


}