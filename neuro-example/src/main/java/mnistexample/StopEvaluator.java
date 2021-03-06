package mnistexample;

import kercept.neuro.NeuralNet;

import java.util.LinkedList;

/**
 * The StopEvaluator keeps track of whether it is meaningful to continue
 * to train the network or if the error rate of the test data seems to
 * be on the raise (i.e. when we might be on our way to overfit the network).
 *
 * It also keeps a copy of the best neural network seen.
 */
public class StopEvaluator {

    private final int windowSize;
    private final NeuralNet network;
    private final Float acceptableErrorRate;
    private final LinkedList<Double> errorRates;

    private String bestNetSoFar;
    private float lowestErrorRate = Float.MAX_VALUE;
    private float lastErrorAverage = Float.MAX_VALUE;

    public StopEvaluator(NeuralNet network, int windowSize, Float acceptableErrorRate) {
        this.windowSize = windowSize;
        this.network = network;
        this.acceptableErrorRate = acceptableErrorRate;
        this.errorRates = new LinkedList<>();
    }

    // See if there is any point in continuing ...
    public boolean stop(double errorRate) {
        // Save config of neural network if error rate is lowest we seen
        if (errorRate < lowestErrorRate) {
            lowestErrorRate = (float) errorRate;
            bestNetSoFar = network.toJson();
        }

        if (acceptableErrorRate != null && lowestErrorRate <= acceptableErrorRate)
            return true;

        // update moving average
        errorRates.addLast(errorRate);

        if (errorRates.size() < windowSize) {
            return false;   // never stop if we have not filled moving average
        }

        if (errorRates.size() > windowSize)
            errorRates.removeFirst();

        double avg = getAverage(errorRates);

        // see if we should stop
        if (avg > lastErrorAverage) {
            return true;
        } else {
            lastErrorAverage = (float) avg;
            return false;
        }
    }

    public String getBestNetSoFar() {
        return bestNetSoFar;
    }

    public double getLowestErrorRate() {
        return lowestErrorRate;
    }

    private double getAverage(LinkedList<Double> list) {
        return list.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}
