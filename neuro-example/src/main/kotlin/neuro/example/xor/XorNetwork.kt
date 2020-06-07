package neuro.example.xor

import kercept.math.Vector
import kercept.neuro.NeuralNet
import kercept.neuro.function.quadraticCost
import kercept.neuro.function.relu
import kercept.neuro.function.sigmoid
import kercept.neuro.layer.TrainableLayer
import kercept.neuro.random
import mnistexample.StopEvaluator
import mnistexample.TrainNetwork
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend
 * @since   07/06/2020
 * @version 1.0
 */

val log = Logger.getLogger(TrainNetwork::class.java.simpleName)

const val EPSILON = 0.000001

fun main() {

    val inputOutputPairs = mutableListOf(
            Vector(0.0, 0.0) to Vector(0.0),
            Vector(0.0, 1.0) to Vector(1.0),
            Vector(1.0, 1.0) to Vector(0.0),
            Vector(1.0, 0.0) to Vector(1.0)
    )

    val hiddenLayer = TrainableLayer(2,
            biasInitializer = { Random.nextDouble() },
            weightsInitializer = random.invoke(0, 1),
            activator = sigmoid)
    val outputLayer = TrainableLayer(1,
            biasInitializer = { Random.nextDouble() },
            weightsInitializer = random.invoke(0, 1),
            activator = relu)

    val net = NeuralNet
            .builder(2)
            .add(hiddenLayer, outputLayer)
            .setCostFunction(quadraticCost)
            .build()

    net.initWeights()

    var epoch = 0
    var errorRateOnTrainDS: Double
    var errorRateOnTestDS: Double

    val evaluator = StopEvaluator(net, 40, 0.0)
    var shouldStop = false

    val t0 = System.currentTimeMillis()
    do {
        epoch++
        inputOutputPairs.shuffle()

        val correctTrainDS = applyDataToNet(inputOutputPairs, net, true)
        errorRateOnTrainDS = 100 - 100.0 * correctTrainDS / inputOutputPairs.size
        if (epoch % 5 == 0) {
            val correctOnTestDS = applyDataToNet(inputOutputPairs, net, false)
            errorRateOnTestDS = 100 - 100.0 * correctOnTestDS / inputOutputPairs.size
            shouldStop = evaluator.stop(errorRateOnTestDS)
            val epocsPerMinute = epoch * 60000.0 / (System.currentTimeMillis() - t0)
            log.info(String.format("Epoch: %3d    |   Train error rate: %6.3f %%    |   Test error rate: %5.2f %%   |   Epocs/min: %5.2f", epoch, errorRateOnTrainDS, errorRateOnTestDS, epocsPerMinute))
        } else {
            log.info(String.format("Epoch: %3d    |   Train error rate: %6.3f %%    |", epoch, errorRateOnTrainDS))
        }

    } while (!shouldStop)

    val lowestErrorRate = evaluator.lowestErrorRate
    log.info(String.format("No improvement, aborting. Reached a lowest error rate of %7.4f %%", lowestErrorRate))
}

/**
 * Run the entire dataset `data` through the network.
 * If `learn` is true the network will learn from the data.
 */
private fun applyDataToNet(data: List<Pair<Vector, Vector>>, network: NeuralNet, learn: Boolean): Int {

    val correct = AtomicInteger()

    for (pair in data) {

        val input = pair.first.copy()

        val result = if (learn)
            network.evaluate(input, pair.second.copy()).first
        else
            network.evaluate(input)

        val diff0 = (result[0] - pair.second[0]).absoluteValue

        if (diff0 < EPSILON)
            correct.incrementAndGet()
    }

    if (learn)
        network.learn()

    return correct.get()
}