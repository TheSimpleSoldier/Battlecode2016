package team037.DataStructures;

import team037.Enums.Bots;

/**
 * Created by joshua on 1/5/16.
 */
public class BuildOrder
{
    //all bots where each row one sequence
    Bots[][] orders;
    //for each sequence, how many times to do it
    int[] times;
    //largest sequence size
    int maxSize;
    //current spot is 3 values: sequence, spot in sequence, and time through sequence
    int[] current;

    public BuildOrder(Bots[][] orders, int[] times)
    {
        this.orders = orders;
        this.times = times;

        maxSize = orders[0].length;
        current = new int[]{0,0,0};
    }

    public Bots nextBot()
    {
        int sequence = current[0];
        int location = current[1];
        int time = current[2];

        Bots toReturn = orders[sequence][location];

        location++;
        if(location >= maxSize || orders[sequence][location] == null)
        {
            location = 0;
            time++;
            if(time >= times[sequence])
            {
                sequence++;
                time = 0;
                if(sequence >= orders.length)
                {
                    sequence--;
                    time = -9999;
                }
            }
        }

        current[0] = sequence;
        current[1] = location;
        current[2] = time;

        return toReturn;
    }
}
