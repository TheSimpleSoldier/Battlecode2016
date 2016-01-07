
package team037.DataStructures;

import team037.Enums.CommunicationType;



public class CommTypeToSpacing {

    public static int opcodeSize = 4;
    public static int valSize = 15;
    public static int typeSize = 4;
    public static int botSize = 8;
    public static int locationSize = 15;
    /*
    Here lies the spacing definitions. When you know what ints you are sending in the
    Message.ints list you also need to specify how many bits each int needs to take up.

    These have a couple conditions:
       1. sum must be less than or equal to 56
       2. must be able to fit into two buckets, 26, 30 using greedy methods
     */
    public static final int[] MAP_EDGE_COORDS_SPACING = {
            2, 15, 7, 2, 15, 7
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

    public static int[] getSpacingArrayFromCommType(CommunicationType t) {

        switch(t) {
            case MAP_BOUNDS:
                return MAP_EDGE_COORDS_SPACING;
        }

        System.out.println("Looking for a spacing that doesn't exist!");
        return null;
    }
}