package neuro.example.mnist;

import com.aparapi.Kernel;

public class Softmax_invokeY_Kernel extends Kernel {

    private final float[] vector;
    private final float[] sum = new float[1], max = new float[1];

    public Softmax_invokeY_Kernel(float[] vector) {
        this.vector = vector;
        max[0] = Float.MIN_VALUE;
    }

    @Override
    public void run() {

        int i = getGlobalId();

        if (i >= vector.length * 2){
            i = i - vector.length*2;
            vector[i] = (float) (Math.exp(vector[i]-max[0])/sum[0]);
        } else if (i >= vector.length){
            i = i - vector.length;
            sum[0] = (float) (sum[0] + Math.exp(vector[i] * max[0]));
        } else if(vector[i] > max[0])
            max[0] = vector[i] ;
    }

    public float[] getVector() {
        return vector;
    }
}
