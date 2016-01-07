package team037;

/**
 * A multimap is an array where its elements link to more elements. Each index
 * in the array can be thought of as a bucket that holds any number of elements.
 * In this case, the heuristic of the search is restricted to integers and used
 * as indices for elements in the multimap, which enables O(1) insertion and
 * O(D) removal, where D is the distance between two consecutive jump points.
 *
 * The heuristic is set up to represent deviations from the ideal path, meaning
 * buckets can be expanded on from lowest index to highest, and elements in the
 * bucket with the lowest index are all potential parents of nodes on an optimal
 * path. The only way to improve the search would be to sort bucket by distance
 * to the goal. However, experiments have shown that the cost of sorting
 * outweighs the cost of expanding on a few extra nodes.
 *
 * @author David Bell
 */
public class Multimap {


    JumpPoint[] points;     // Array used to store this heap's nodes


    int goalX,goalY;        // Coordinates of the goal
    boolean[][] visited;    // Record of locations that have been visited

    int size;               // Number of nodes in this multimap
    int bestScore;          // Best f(n) in the multimap

    /**
     * Constructor. Creates four JumpPoint objects and prioritizes them based on
     * their directions of search relative to the goal. A 16-direction compass is
     * used to do this prioritization, which is then reduced to the 8-direction
     * compass used is all other areas of the Navigation package.
     *
     * @param startX x-coordinate of the starting jump point
     * @param startY y-coordinate of the starting jump point
     * @param goal_X x-coordinate of the end jump point
     * @param goal_Y y-coordinate of the end jump point
     */
    public Multimap(int startX, int startY, int goal_X, int goal_Y) {
        visited = new boolean[8][65536];  // Track which nodes have been visited
        points = new JumpPoint[8192];     // Memory for the hash table

        // Set the goal coordinates for the current search
        JumpPoint.goalX = goal_X;
        JumpPoint.goalY = goal_Y;
        goalX = goal_X;
        goalY = goal_Y;

        // Calculate the Chebyshev distance to the goal
        bestScore = Math.max(Math.abs(goalX - startX), Math.abs(goalY - startY));
        JumpPoint.bestScore = bestScore;
        // Create four new JumpPoint objects at this location
        JumpPoint front = new JumpPoint(startX,startY, 1, bestScore);
        JumpPoint next = new JumpPoint(startX,startY, 1, bestScore);
        JumpPoint next2 = new JumpPoint(startX,startY, 2, bestScore);
        JumpPoint next3 = new JumpPoint(startX,startY, 3, bestScore);
        bestScore = 1;
        // Mark the initial coordinates as visited
        int index = startY*256+startX;
        visited[1][index] = true;
        visited[3][index] = true;
        visited[5][index] = true;
        visited[7][index] = true;

        switch (front.directionTo(new int[]{goalX,goalY})) {
            case 1:     // NNE
                front.direction = 1;    // NE
                next.direction = 7;     // NW
                next2.direction = 3;    // SE
                next3.direction = 5;    // SW
                break;
            case 3:     // ENE
                front.direction = 1;    // NE
                next.direction = 3;     // SE
                next2.direction = 7;    // NW
                next3.direction = 5;    // SW
                break;
            case 5:     // ESE
                front.direction = 3;    // SE
                next.direction = 1;     // NE
                next2.direction = 5;    // SW
                next3.direction = 7;    // NW
                break;
            case 7:     // SSE
                front.direction = 3;    // SE
                next.direction = 5;     // SW
                next2.direction = 1;    // NE
                next3.direction = 7;    // NW
                break;
            case 9:     // SSW
                front.direction = 5;    // SW
                next.direction = 3;     // SE
                next2.direction = 7;    // NW
                next3.direction = 1;    // NE
                break;
            case 11:    // WSW
                front.direction = 5;    // SW
                next.direction = 7;     // NW
                next2.direction = 3;    // SE
                next3.direction = 1;    // NE
                break;
            case 13:    // WNW
                front.direction = 7;    // NW
                next.direction = 5;     // SW
                next2.direction = 1;    // NE
                next3.direction = 3;    // SE
                break;
            default:    // NNW
                front.direction = 7;    // NW
                next.direction = 1;     // NE
                next2.direction = 5;    // SW
                next3.direction = 3;    // SE
                break;
        }



        // Start at index = 1 for efficient access to objects in the heap
        points[bestScore] = front;  // top of the heap
        front.hashNext = next;   // top's left node
        next.hashNext = next2;  // top's right node
        next2.hashNext = next3;  // top left's left node

        size = 4;
    }

    /**
     * Constructor. Creates four JumpPoint objects and prioritizes them based on
     * their directions of search relative to the goal. A 16-direction compass is
     * used to do this prioritization, which is then reduced to the 8-direction
     * compass used is all other areas of the Navigation package.
     *
     * @param startX x-coordinate of the starting jump point
     * @param startY y-coordinate of the starting jump point
     * @param goal_X x-coordinate of the end jump point
     * @param goal_Y y-coordinate of the end jump point
     * @param omitDirection Direction of search to omit
     */
    public Multimap(int startX, int startY, int goal_X, int goal_Y, int omitDirection) {
        visited = new boolean[8][65536];  // Track which nodes have been visited
        points = new JumpPoint[8192];     // Memory for the hash table

        // Set the goal coordinates for the current search
        JumpPoint.goalX = goal_X;
        JumpPoint.goalY = goal_Y;
        goalX = goal_X;
        goalY = goal_Y;

        // Calculate the Chebyshev distance to the goal
        bestScore = Math.max(Math.abs(goalX - startX), Math.abs(goalY - startY));
        JumpPoint.bestScore = bestScore;
        // Create four new JumpPoint objects at this location
        JumpPoint front = new JumpPoint(startX,startY, 1, bestScore);
        JumpPoint next = new JumpPoint(startX,startY, 1, bestScore);
        JumpPoint next2 = new JumpPoint(startX,startY, 2, bestScore);
        bestScore = 1;
        // Mark the initial coordinates as visited
        int index = startY*256+startX;
        visited[1][index] = true;
        visited[3][index] = true;
        visited[5][index] = true;
        visited[7][index] = true;

        switch (omitDirection) {
            case 1:     // NE
                front.direction = 5;    // NE
                next.direction = 7;     // NW
                next2.direction = 3;    // SE
                break;
            case 2:     // E
                front.direction = 7;    // NW
                next.direction = 5;     // SW
                break;
            case 3:     // SE
                front.direction = 7;    // NW
                next.direction = 1;     // NE
                next2.direction = 5;    // SW
                break;
            case 4:     // S
                front.direction = 1;    // NE
                next.direction = 7;     // NW
                break;
            case 5:     // SW
                front.direction = 1;    // NE
                next.direction = 3;     // SE
                next2.direction = 7;    // NW
                break;
            case 6:    // W
                front.direction = 1;    // NE
                next.direction = 3;     // SE
                break;
            case 7:    // NW
                front.direction = 3;    // SW
                next.direction = 5;     // SE
                next2.direction = 1;    // NE
                break;
            default:    // N
                front.direction = 3;    // SW
                next.direction = 5;     // SE
                break;
        }



        // Start at index = 1 for efficient access to objects in the heap
        points[bestScore] = front;  // top of the heap
        front.hashNext = next;   // top's left node
        next.hashNext = next2;  // top's right node

        size = 3;
    }

    /**
     * Get the index of the fist element.
     * @return integer index of the first element in the multimap
     */
    public int peek() {
        int out;
        if (size == 0) {
            out = bestScore;
        } else {
            int i = bestScore-1;
            while (points[++i] == null) {}
            bestScore = i;
            out = bestScore;
        }
        return out;
    }

    /**
     * Get the element at the specified index
     * @param index
     * @return JumpPoint at specified index.
     */
    public JumpPoint peek(int index) {
        return points[index];
    }

    /**
     * Remove the jump point at the top of the hash queue.
     * @return the jump point with the lowest f(n) = h(n) + g(n)
     */
    public JumpPoint remove() {
        JumpPoint remove;
        if (size == 0) {
            remove = null;
        } else {
            int index = bestScore;
            while (points[index] == null) {++index;}
            remove = points[index];
            points[index] = remove.hashNext;
            bestScore = index;
            size--;
        }

        return remove; // Return the JumpPoint with the smallest heuristic score.
    }

    /**
     * Insert a JumpPoint into the hash table, and throw its key on the bottom
     * of the heap for later sorting only if it is a new key.
     * @param jp JumpPoint to insert
     */
    public void insert(JumpPoint jp) {
        int index = jp.yLoc*256+jp.xLoc;  // Linear index of newNode coordinates.

        if (!visited[jp.direction][index]) {     // If this node has not been visited...
            visited[jp.direction][index] = true; // Mark it as visited with the current direction of search.
            int myScore = jp.f;
            jp.hashNext = points[myScore];
            points[myScore] = jp;
            size++;
        }
    }

    /**
     * Retrace the path from any given jump point to the start.
     * Links nodes to create a bi-directional graph from the starting jump point
     * to the given jump point.
     * @param jp Jump point at the end of the path
     * @return Jump point at the start of the path
     */
    public JumpPoint retrace(JumpPoint jp) {
        JumpPoint next = jp.mapLast;// Previous JumpPoint on jp's path.
        while(next != null) {   // While jp has a previous JumpPoint on its path...
            next.mapNext = jp;  // Link jp and next in the forward direction.
            jp = next;          // Move back along the path by having jp reference its previous JumpPoint.
            next = next.mapLast;// next = the JumpPoint prior to itself.
        }
        return jp;
    }

    /**
     * Retrace the path from any given jump point to the start.
     * @param jp Jump point at the end of the path
     * @return Jump point at the start of the path
     */
    public JumpPoint retraceNoLink(JumpPoint jp) {
        JumpPoint next = jp.mapLast;// Previous JumpPoint on jp's path.
        JumpPoint temp = null;
        while(next != null) {    // While jp has a previous JumpPoint on its path...
            temp = jp;
            jp = next;           // jp references the previous JumpPoint.
            next = temp.mapLast; // next = the previous JumpPoint.
        }
        if (temp != null) {
            return temp;
        }
        return jp;   // Return a copy of the JumpPoint at the start of the path.
    }
}

