package mnistexample;

import kercept.math.Vector;
import kercept.neuro.NeuralNet;
import kercept.neuro.layer.TrainableLayer;
import mnistexample.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.shuffle;
import static java.util.Collections.unmodifiableList;

@SuppressWarnings("Duplicates")
public class TrainNetwork {

    private static Logger log = Logger.getLogger(TrainNetwork.class.getSimpleName());

    private static int BATCH_SIZE = 32;


    public static void main(String[] args) throws IOException {
//        int seed = 942457;
//        setRnd(new Random(seed));

        List<DigitData> trainData = FileUtil.loadImageData("train");
        List<DigitData> testData = FileUtil.loadImageData("t10k");

//        for (DigitData d : trainData) {
//            d.setRandom(new Random(seed++));
//        }

        NeuralNet network = new MnistNetBuilder().create();

        network.initWeights();

        int epoch = 0;
        double errorRateOnTrainDS;
        double errorRateOnTestDS;

        StopEvaluator evaluator = new StopEvaluator(network, 40, null);
        boolean shouldStop = false;

        long t0 = currentTimeMillis();
        do {
            epoch++;
            shuffle(trainData);

            int correctTrainDS = applyDataToNet(trainData, network, true);
            errorRateOnTrainDS = 100 - (100.0 * correctTrainDS / trainData.size());

            if (epoch % 5 == 0) {
                int correctOnTestDS = applyDataToNet(testData, network, false);
                errorRateOnTestDS = 100 - (100.0 * correctOnTestDS / testData.size());
                shouldStop = evaluator.stop(errorRateOnTestDS);
                double epocsPerMinute = epoch * 60000.0 / (currentTimeMillis() - t0);
                log.info(format("Epoch: %3d    |   Train error rate: %6.3f %%    |   Test error rate: %5.2f %%   |   Epocs/min: %5.2f", epoch, errorRateOnTrainDS, errorRateOnTestDS, epocsPerMinute));
            } else {
                log.info(format("Epoch: %3d    |   Train error rate: %6.3f %%    |", epoch, errorRateOnTrainDS));
            }

//            trainData.parallelStream().forEach(DigitData::transformDigit);

        } while (!shouldStop);

        double lowestErrorRate = evaluator.getLowestErrorRate();
        log.info(format("No improvement, aborting. Reached a lowest error rate of %7.4f %%", lowestErrorRate));
        writeFile(evaluator, lowestErrorRate);
    }

    /**
     * Run the entire dataset <code>data</code> through the network.
     * If <code>learn</code> is true the network will learn from the data.
     */
    private static int applyDataToNet(List<DigitData> data, NeuralNet network, boolean learn) {
        final AtomicInteger correct = new AtomicInteger();

        for (int i = 0; i <= data.size() / BATCH_SIZE; i++) {

            getBatch(i, data).parallelStream().forEach(img -> {
                Vector input = new Vector(img.getData());
                Vector result = learn ?
                        network.evaluate(input, new Vector(img.getLabelAsArray())).getFirst() :
                        network.evaluate(input);

                if (result.indexOfMax() == img.getLabel())
                    correct.incrementAndGet();
            });

            if (learn)
                network.learn();
        }

        return correct.get();
    }

    /**
     * Cuts out batch i from dataset data.
     */
    private static List<DigitData> getBatch(int i, List<DigitData> data) {
        int fromIx = i * BATCH_SIZE;
        int toIx = Math.min(data.size(), (i + 1) * BATCH_SIZE);
        return unmodifiableList(data.subList(fromIx, toIx));
    }

    /**
     * Saves the weights and biases of the network in directory "./out"
     */
    private static void writeFile(StopEvaluator evaluator, double lowestErrorRate) throws IOException {
        File outDir = new File("./out");
        if (!outDir.exists())
            if (!outDir.mkdirs())
                throw new IOException("Could not create directory " + outDir.getAbsolutePath());

        File outFile = new File(outDir, format("%4.2f %tF %tT.json", lowestErrorRate, new Date(), new Date()));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
            bw.write(evaluator.getBestNetSoFar());
        }
    }


}
