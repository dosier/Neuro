package neuro.example.mnist

import kercept.neuro.NeuralNet
import kercept.neuro.function.leakyRelu
import kercept.neuro.function.quadraticCost
import kercept.neuro.function.softmax
import kercept.neuro.layer.TrainableLayer
import kercept.neuro.xavierNormal
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private const val BATCH_SIZE = 32

fun main() {

    val net = NeuralNet
            .builder(28 * 28)
            .add(
                    TrainableLayer(38,
//                            l2 = 0.00010,
                            weightsInitializer = xavierNormal,
//                            optimizer = Nesterov(0.02, 0.87),
                            activator = leakyRelu),
                    TrainableLayer(12,
//                            l2 = 0.00010,
                            weightsInitializer = xavierNormal,
//                            optimizer = Nesterov(0.02, 0.87),
                            activator = leakyRelu),
                    TrainableLayer(10,
//                            l2 = 0.00010,
                            weightsInitializer = xavierNormal,
//                            optimizer = Nesterov(0.02, 0.87),
                            activator = softmax)
            )
            .setCostFunction(quadraticCost)
            .build()

    net.initWeights()


    val trainData = getData("train")
    val testData = getData("t10k")

    var epoch = 0
    var trainErrorRate: Double
    var testErrorRate: Double

    val supervisor = ErrorRateSupervisor(net, windowSize = 40)
    var stopTraining = false

    val t0 = System.nanoTime()

    while (!stopTraining){

        epoch++
        trainData.shuffle()

        val correctTrainEvaluations = evaluateData(trainData, net, true)
        trainErrorRate = getErrorRate(correctTrainEvaluations, trainData)

        if(epoch % 5 == 0){

            val correctTestEvaluations = evaluateData(testData, net, false)
            testErrorRate = getErrorRate(correctTestEvaluations, testData)

            stopTraining = supervisor.stop(testErrorRate)

            val epochsPerMinute = epoch * 60_000 / (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-t0)).toFloat()

            System.out.printf("Epoch: %3d    " +
                    "|   Train error rate: %6.3f %%    " +
                    "|   Test error rate: %5.2f %%   " +
                    "|   Epocs/min: %5.2f", epoch, trainErrorRate, testErrorRate, epochsPerMinute)
        } else {
            System.out.printf("Epoch: %3d    " +
                    "|   Train error rate: %6.3f %%    ", epoch, trainErrorRate)
        }
        println()
    }
}

private fun getErrorRate(correctEvaluations: Int, data: MutableList<DigitEntry>) =
        100 - (100.0 * correctEvaluations / data.size)

fun evaluateData(data: List<DigitEntry>, net: NeuralNet, learn: Boolean) : Int {

    val correctCount = AtomicInteger()
    for (i in 0.. data.size / BATCH_SIZE) {
        getBatch(i, data).parallelStream().forEach {
            val result = if(learn)
                net.evaluate(it.image.copy(), it.labelVector.copy()).first
            else
                net.evaluate(it.image.copy())

            if(result.indexOfMax() == it.label){
                correctCount.incrementAndGet()
            }
        }
        if(learn)
            net.learn()
    }
    return correctCount.get()
}

/**
 * Cuts out batch i from dataset data.
 */
private fun getBatch(i: Int, data: List<DigitEntry>): List<DigitEntry> {
    val fromIx: Int = i * BATCH_SIZE
    val toIx = Math.min(data.size, (i + 1) * BATCH_SIZE)
    return Collections.unmodifiableList(data.subList(fromIx, toIx))
}


fun getData(type: String): MutableList<DigitEntry> {

    val reader = MnistReader()

    val imagesPath = Paths.get("data/$type-images-idx3-ubyte")
    val images = reader.readImages(imagesPath.toFile())

    val labelsPath = Paths.get("data/$type-labels-idx1-ubyte")
    val labels = reader.readLabels(labelsPath.toFile())

    return MutableList(images.size) {
        DigitEntry(
                label = labels[it],
                labelVector = reader.labelToVector(labels[it]),
                image = reader.imageToVector(images[it]))
    }
}