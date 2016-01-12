package team037.NeuralNet;

/**
 * inputs: 6
 * middle: 10
 * output: 5
 */
public class FeedForwardNeuralNet
{
    private double[] weights;

    public FeedForwardNeuralNet(double[] weights)
    {
        this.weights = weights;
    }

    public void setWeights(double[] weights)
    {
        this.weights = weights;
    }

    public double[] compute(double[] input)
    {
        double[] weights = this.weights;
        double[] middle = {0,0,0,0,0,0,0,0,0,0};
        double[] output = {0,0,0,0,0};

        if(input[0] * weights[0] + input[1] * weights[10] + input[2] * weights[20] + input[3] * weights[30] + input[4] * weights[40] + input[5] * weights[50] > .5)
        {
            middle[0] = 1;
        }

        if(input[0] * weights[1] + input[1] * weights[11] + input[2] * weights[21] + input[3] * weights[31] + input[4] * weights[41] + input[5] * weights[51] > .5)
        {
            middle[1] = 1;
        }

        if(input[0] * weights[2] + input[1] * weights[12] + input[2] * weights[22] + input[3] * weights[32] + input[4] * weights[42] + input[5] * weights[52] > .5)
        {
            middle[2] = 1;
        }

        if(input[0] * weights[3] + input[1] * weights[13] + input[2] * weights[23] + input[3] * weights[33] + input[4] * weights[43] + input[5] * weights[53] > .5)
        {
            middle[3] = 1;
        }

        if(input[0] * weights[4] + input[1] * weights[14] + input[2] * weights[24] + input[3] * weights[34] + input[4] * weights[44] + input[5] * weights[54] > .5)
        {
            middle[4] = 1;
        }

        if(input[0] * weights[5] + input[1] * weights[15] + input[2] * weights[25] + input[3] * weights[35] + input[4] * weights[45] + input[5] * weights[55] > .5)
        {
            middle[5] = 1;
        }

        if(input[0] * weights[6] + input[1] * weights[16] + input[2] * weights[26] + input[3] * weights[36] + input[4] * weights[46] + input[5] * weights[56] > .5)
        {
            middle[6] = 1;
        }

        if(input[0] * weights[7] + input[1] * weights[17] + input[2] * weights[27] + input[3] * weights[37] + input[4] * weights[47] + input[5] * weights[57] > .5)
        {
            middle[7] = 1;
        }

        if(input[0] * weights[8] + input[1] * weights[18] + input[2] * weights[28] + input[3] * weights[38] + input[4] * weights[48] + input[5] * weights[58] > .5)
        {
            middle[8] = 1;
        }

        if(input[0] * weights[9] + input[1] * weights[19] + input[2] * weights[29] + input[3] * weights[39] + input[4] * weights[49] + input[5] * weights[59] > .5)
        {
            middle[9] = 1;
        }

        if(middle[0] * weights[60] + middle[1] * weights[65] + middle[2] * weights[70] + middle[3] * weights[75] + middle[4] * weights[80] + middle[5] * weights[85] + middle[6] * weights[90] + middle[7] * weights[95] + middle[8] * weights[100] + middle[9] * weights[105] > .5)
        {
            output[0] = 1;
        }

        if(middle[0] * weights[61] + middle[1] * weights[66] + middle[2] * weights[71] + middle[3] * weights[76] + middle[4] * weights[81] + middle[5] * weights[86] + middle[6] * weights[91] + middle[7] * weights[96] + middle[8] * weights[101] + middle[9] * weights[106] > .5)
        {
            output[1] = 1;
        }

        if(middle[0] * weights[62] + middle[1] * weights[67] + middle[2] * weights[72] + middle[3] * weights[77] + middle[4] * weights[82] + middle[5] * weights[87] + middle[6] * weights[92] + middle[7] * weights[97] + middle[8] * weights[102] + middle[9] * weights[107] > .5)
        {
            output[2] = 1;
        }

        if(middle[0] * weights[63] + middle[1] * weights[68] + middle[2] * weights[73] + middle[3] * weights[78] + middle[4] * weights[83] + middle[5] * weights[88] + middle[6] * weights[93] + middle[7] * weights[98] + middle[8] * weights[103] + middle[9] * weights[108] > .5)
        {
            output[3] = 1;
        }

        if(middle[0] * weights[64] + middle[1] * weights[69] + middle[2] * weights[74] + middle[3] * weights[79] + middle[4] * weights[84] + middle[5] * weights[89] + middle[6] * weights[94] + middle[7] * weights[99] + middle[8] * weights[104] + middle[9] * weights[109] > .5)
        {
            output[4] = 1;
        }

        return output;
    }
}