package team037;

/**
 * JumpPoint objects hold data for specific Cartesian points on a 2D grid. These
 * points have the property of having no obstacles between two consecutive
 * points.
 *
 * @author David Bell
 */
public class JumpPoint {

    static int goalX, goalY, bestScore;

    JumpPoint mapNext;  // Link to the JumpPoint following this JumpPoint in the path
    JumpPoint mapLast;  // Link to the JumpPoint behind this JumpPoint in the path
    JumpPoint hashNext; // Link to the next JumpPoint in this JumpPoint's bin
    int xDistance, yDistance;    // Distances to voids
    int f, h;           // Result of the heuristic function for this JumpPoint
    int distance;       // Distance from this point to goal
    int direction;      // Direction of search from this JumpPoint
    int xLoc, yLoc;     // Coordinates of this point

    /**
     * Constructor to copy JumpPoint objects.
     *
     * @param jp JumpPoint to be copied.
     */
    public JumpPoint(JumpPoint jp) {
        mapNext = jp.mapNext;
        mapLast = jp.mapLast;
        xLoc = jp.xLoc;
        yLoc = jp.yLoc;
        direction = jp.direction;
        f = jp.f;
    }

    /**
     * Empty constructor.
     */
    public JumpPoint() {
    }

    /**
     * Constructor for reversing a path
     *
     * @param x coordinate
     * @param y coordinate
     */
    public JumpPoint(int x, int y) {
        xLoc = x;
        yLoc = y;
    }

    /**
     *
     * @param x Cartesian coordinates.
     * @param y Cartesian coordinates. General constructor
     * @param f f(n) = h(n) + g(n)
     * @param d heuristic h(n)
     */
    public JumpPoint(int x, int y, int f, int d) {
        xLoc = x;
        yLoc = y;
        distance = d;
        this.f = f;
    }

    /**
     *
     * @param x Cartesian coordinates.
     * @param y Cartesian coordinates.
     * @param last last JumpPoint
     * @param d directionality of search from this jump point
     * @param dist distance traveled to this node
     */
    public JumpPoint(int x, int y, int d, JumpPoint last, int dist) {
        xLoc = x;
        yLoc = y;
        direction = d;
        distance = Math.max(Math.abs(goalX - x), Math.abs(goalY - y));
        h = distance - last.distance + 1;
        f = last.f + h + dist;
        mapLast = last;
        mapLast.mapNext = this;
    }

    public boolean move(int direction) {
        boolean moved = true;
        switch (direction) {
            case 0:
                yLoc--;
                break;
            case 1:
                xLoc++;
                yLoc--;
                break;
            case 2:
                xLoc++;
                break;
            case 3:
                xLoc++;
                yLoc++;
                break;
            case 4:
                yLoc++;
                break;
            case 5:
                xLoc--;
                yLoc++;
                break;
            case 6:
                xLoc--;
                break;
            case 7:
                xLoc--;
                yLoc--;
                break;
            default:
                moved = false;
                break;
        }
        return moved;
    }

    public int directionTo(JumpPoint jp) {
        int nextDirection;

        if (jp == null) {
            nextDirection = -1;
        } else {
            int x = jp.xLoc;
            int y = jp.yLoc;

            int dx = xLoc - x;
            int dy = yLoc - y;

            if (dy == 0) {
                if (dx == 0) {
                    nextDirection = -1;
                } else if (dx < 0) {
                    nextDirection = 2;
                } else {
                    nextDirection = 6;
                }
            } else if (dx == 0) {
                if (dy < 0) {
                    nextDirection = 4;
                } else {
                    nextDirection = 0;
                }
            } else if (dx < 0) {
                if (dy < 0) {
                    nextDirection = 3;
                } else {
                    nextDirection = 1;
                }
            } else if (dy < 0) {
                nextDirection = 5;
            } else {
                nextDirection = 7;
            }
        }
        return nextDirection;
    }

    /**
     * Find the direction from this node to any given point when one direction
     * of search is needed.
     *
     * @param x coordinate of the given point
     * @param y coordinate of the given point
     * @return integer from 0-7, representing 8 possible directions where north
     * = 0, east = 2, south = 4, and west = 6. Return will always be odd, i.e.
     * the directionality is always one of the following: NE = 1, SE = 3, SW =
     * 5, NW = 7
     */
    public int directionTo(int x, int y) {
        int nextDirection;

        if (xLoc - x < 0) {
            if (yLoc - y < 0) {
                nextDirection = 3;
            } else {
                nextDirection = 1;
            }
        } else if (yLoc - y < 0) {
            nextDirection = 5;
        } else {
            nextDirection = 7;
        }
        return nextDirection;
    }

    public boolean equals(JumpPoint pt) {
        return pt.xLoc == xLoc && pt.yLoc == yLoc;
    }

    public boolean equals(int[] pt) {
        return pt[0] == xLoc && pt[1] == yLoc;
    }

    public boolean equals(int x, int y) {
        return x == xLoc && y == yLoc;
    }

    public boolean sameBranch(JumpPoint jp) {
        JumpPoint root = mapLast;
        boolean same = false;

        while (root != null && root.xLoc != jp.xLoc && root.yLoc != jp.yLoc) {
            root = root.mapLast;
        }

        while (root.equals(jp)) {
            if (equals(jp)) {
                same = true;
                break;
            }
            root = root.mapNext;
        }
        return same;
    }

    /**
     * Find the direction from this node to any given point when four directions
     * of search are needed.
     *
     * @param b the given point.
     * @return integer from 0-15, representing 16 possible directions where
     * north = 0, east = 4, south = 8, and west = 12. Return will always be odd,
     * i.e. the directionality is always one of the following: NNE = 1, ENE = 3,
     * ESE = 5, SSE = 7, SSW = 9, WSW = 11, WNW = 13, NNW = 15
     */
    public int directionTo(int[] b) {

        int nextDirection;
        int dx = xLoc - b[0];
        int dy = yLoc - b[1];

        if (dx != 0) {
            switch (dy / dx) {
                case -256:
                case -255:
                case -254:
                case -253:
                case -252:
                case -251:
                case -250:
                case -249:
                case -248:
                case -247:
                case -246:
                case -245:
                case -244:
                case -243:
                case -242:
                case -241:
                case -240:
                case -239:
                case -238:
                case -237:
                case -236:
                case -235:
                case -234:
                case -233:
                case -232:
                case -231:
                case -230:
                case -229:
                case -228:
                case -227:
                case -226:
                case -225:
                case -224:
                case -223:
                case -222:
                case -221:
                case -220:
                case -219:
                case -218:
                case -217:
                case -216:
                case -215:
                case -214:
                case -213:
                case -212:
                case -211:
                case -210:
                case -209:
                case -208:
                case -207:
                case -206:
                case -205:
                case -204:
                case -203:
                case -202:
                case -201:
                case -200:
                case -199:
                case -198:
                case -197:
                case -196:
                case -195:
                case -194:
                case -193:
                case -192:
                case -191:
                case -190:
                case -189:
                case -188:
                case -187:
                case -186:
                case -185:
                case -184:
                case -183:
                case -182:
                case -181:
                case -180:
                case -179:
                case -178:
                case -177:
                case -176:
                case -175:
                case -174:
                case -173:
                case -172:
                case -171:
                case -170:
                case -169:
                case -168:
                case -167:
                case -166:
                case -165:
                case -164:
                case -163:
                case -162:
                case -161:
                case -160:
                case -159:
                case -158:
                case -157:
                case -156:
                case -155:
                case -154:
                case -153:
                case -152:
                case -151:
                case -150:
                case -149:
                case -148:
                case -147:
                case -146:
                case -145:
                case -144:
                case -143:
                case -142:
                case -141:
                case -140:
                case -139:
                case -138:
                case -137:
                case -136:
                case -135:
                case -134:
                case -133:
                case -132:
                case -131:
                case -130:
                case -129:
                case -128:
                case -127:
                case -126:
                case -125:
                case -124:
                case -123:
                case -122:
                case -121:
                case -120:
                case -119:
                case -118:
                case -117:
                case -116:
                case -115:
                case -114:
                case -113:
                case -112:
                case -111:
                case -110:
                case -109:
                case -108:
                case -107:
                case -106:
                case -105:
                case -104:
                case -103:
                case -102:
                case -101:
                case -100:
                case -99:
                case -98:
                case -97:
                case -96:
                case -95:
                case -94:
                case -93:
                case -92:
                case -91:
                case -90:
                case -89:
                case -88:
                case -87:
                case -86:
                case -85:
                case -84:
                case -83:
                case -82:
                case -81:
                case -80:
                case -79:
                case -78:
                case -77:
                case -76:
                case -75:
                case -74:
                case -73:
                case -72:
                case -71:
                case -70:
                case -69:
                case -68:
                case -67:
                case -66:
                case -65:
                case -64:
                case -63:
                case -62:
                case -61:
                case -60:
                case -59:
                case -58:
                case -57:
                case -56:
                case -55:
                case -54:
                case -53:
                case -52:
                case -51:
                case -50:
                case -49:
                case -48:
                case -47:
                case -46:
                case -45:
                case -44:
                case -43:
                case -42:
                case -41:
                case -40:
                case -39:
                case -38:
                case -37:
                case -36:
                case -35:
                case -34:
                case -33:
                case -32:
                case -31:
                case -30:
                case -29:
                case -28:
                case -27:
                case -26:
                case -25:
                case -24:
                case -23:
                case -22:
                case -21:
                case -20:
                case -19:
                case -18:
                case -17:
                case -16:
                case -15:
                case -14:
                case -13:
                case -12:
                case -11:
                case -10:
                case -9:
                case -8:
                case -7:
                case -6:
                case -5:
                case -4:
                case -3:
                case -2:
                case -1:
                    if (dy < 0) {
                        nextDirection = 9;
                    } else {
                        nextDirection = 1;
                    }
                    break;

                case 0:
                    if (dx < 0) {
                        if (dy < 0) {
                            nextDirection = 5;
                        } else {
                            nextDirection = 3;
                        }
                    } else if (dy < 0) {
                        nextDirection = 11;
                    } else {
                        nextDirection = 13;
                    }
                    break;

                default:
                    if (dx < 0) {
                        nextDirection = 7;
                    } else {
                        nextDirection = 15;
                    }
                    break;

            }

        } else if (dy < 0) {
            nextDirection = 9;
        } else {
            nextDirection = 1;
        }

        return nextDirection;
    }
}
