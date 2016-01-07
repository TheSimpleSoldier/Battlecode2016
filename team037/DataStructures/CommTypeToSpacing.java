
package team037.DataStructures;

public class CommTypeToSpacing {

    public static int opcodeSize = 4;
    public static int valSize = 15;
    public static int typeSize = 4;
    public static int botSize = 8;
    public static int locationSize = 15;
    public static int locationSize2 = 7;
    public static int indicatorSize = 2;
    /*
    Here lies the spacing definitions. When you know what ints you are sending in the
    Message.ints list you also need to specify how many bits each int needs to take up.

    These have a couple conditions:
       1. sum must be less than or equal to 56
       2. must be able to fit into two buckets, 26, 30 using greedy methods
     */
    public static final int[] MK_FORMAT_SPACING = {
            indicatorSize, locationSize, locationSize2, indicatorSize, locationSize, locationSize2
    };
    public static final int[] I_FORMAT_SPACING = {
            valSize, locationSize, locationSize
    };
    public static final int[] BI_FORMAT_SPACING = {
            valSize, typeSize, locationSize, locationSize
    };
    public static final int[] CM_FORMAT_SPACING = {
            valSize, botSize, botSize
    };
    public static final int[] IM_FORMAT_SPACING = {
            valSize, botSize
    };
}