package team037.DataStructures;

import battlecode.common.*;

public class AppendOnlyMapLocationSet extends AppendOnlyMapLocationArray{

    public AppendOnlyMapLocationSet() {
        super();
    }

    @Override
    public void add(MapLocation m) {
        if (!contains(m)) {
            array[length] = m;
            length += 1;
        }
    }
}

