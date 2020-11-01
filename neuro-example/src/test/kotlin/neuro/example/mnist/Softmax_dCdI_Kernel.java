package neuro.example.mnist;

import com.aparapi.Kernel;

public class Softmax_dCdI_Kernel extends Kernel {

    private final float[] result, out, dCd0;
    private final float[] sum = new float[1];

    public Softmax_dCdI_Kernel(float[] result, float[] out, float[] dCd0) {
        this.result = result;
        this.out = out;
        this.dCd0 = dCd0;
    }

    @Override
    public void run() {

        int i = getGlobalId();

        if (i >= result.length){
            i = i - result.length;
            float sub = dCd0[i] - sum[0];
            result[i] = out[i] * sub;
        } else {
            sum[0] = sum[0] + (out[i] * dCd0[i]);
        }
    }

    public float[] getResult() {
        return result;
    }
}
