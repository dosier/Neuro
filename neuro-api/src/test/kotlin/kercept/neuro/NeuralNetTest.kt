//package kercept.neuro
//
//import kercept.math.DoubleMatrix
//import kercept.math.DoubleVector
//import kercept.neuro.function.quadraticCost
//import kercept.neuro.function.relu
//import kercept.neuro.function.sigmoid
//import kercept.neuro.function.softmax
//import kercept.neuro.layer.TrainableLayer
//import kercept.neuro.optimizer.GradientDescent
//import org.junit.Assert.assertEquals
//import org.junit.Test
//
//
//internal class NeuralNetTest {
//
//
//    companion object {
//        private const val EPS = 0.00001
//    }
//
//    @Test
//    fun testFFExampleFromBlog() {
//
//
//        val initWeights = arrayOf(
//                DoubleMatrix(2, 2, doubleArrayOf(0.3, 0.2, -.4, 0.6)),
//                DoubleMatrix(2, 2, doubleArrayOf(0.7, -.3, 0.5, -.1))
//        )
//        val network = NeuralNet.builder(2)
//                .add(
//                        TrainableLayer(2,
//                                optimizer = GradientDescent(0.1),
//                                activator = sigmoid,
//                                biasInitializer = { i -> if(i==0) 0.25 else 0.45 },
//                                weightsInitializer = initWeights[0].asInitializer()),
//                        TrainableLayer(2,
//                                optimizer = GradientDescent(0.1),
//                                activator = sigmoid,
//                                biasInitializer = { i -> if(i==0) 0.15 else 0.35 },
//                                weightsInitializer = initWeights[1].asInitializer()))
//                .setCostFunction(quadraticCost)
//                .build()
//
//
//        network.initWeights()
//
//        val out = network.evaluate(DoubleVector(2.0, 3.0), DoubleVector(1.0, 0.2)).first
//
//        assertEquals(0.712257432295742, out[0], EPS)
//        assertEquals(0.533097573871501, out[1], EPS)
//
//        network.learn()
//
//        val result = network.evaluate(DoubleVector(2.0, 3.0), DoubleVector(1.0, 0.2)).first
//
//        assertEquals(0.7187729999291985, result[0], EPS)
//        assertEquals(0.5238074518609882, result[1], EPS)
//    }
//
//    @Test
//    fun testEvaluate(){
//
//        val initWeights = arrayOf(
//                DoubleMatrix(3, 3, doubleArrayOf(0.1, 0.2, 0.3, 0.3, 0.2, 0.7, 0.4, 0.3, 0.9)),
//                DoubleMatrix(3, 3, doubleArrayOf(0.2, 0.3, 0.5, 0.3, 0.5, 0.7, 0.6, 0.4, 0.8)),
//                DoubleMatrix(3, 3, doubleArrayOf(0.1, 0.4, 0.8, 0.3, 0.7, 0.2, 0.5, 0.2, 0.9))
//        )
//        val network = NeuralNet.builder(3)
//                .add(
//                        TrainableLayer(3,
//                                activator = relu,
//                                biasInitializer = { 1.0 },
//                                weightsInitializer = initWeights[0].asInitializer()),
//                        TrainableLayer(3,
//                                biasInitializer = { 1.0 },
//                                weightsInitializer = initWeights[1].asInitializer()),
//                        TrainableLayer(3,
//                                activator = softmax,
//                                biasInitializer = { 1.0 },
//                                weightsInitializer = initWeights[2].asInitializer())
//                        )
//                .setCostFunction(quadraticCost)
//                .build()
//
//        network.initWeights()
//
//        val out = network.evaluate(DoubleVector(0.1, 0.2, 0.7))
//
//        assertEquals(0.1984468942, out[0], EPS)
//        assertEquals(0.2853555304, out[1], EPS)
//        assertEquals(0.5161975753, out[2], EPS)
//        assertEquals(1.0, out[0] + out[1] + out[2], EPS)
//    }
//
//    @Test
//    fun testEvaluateAndLearn2(){
//
//        val net = NeuralNet.builder(4)
//                .add(
//                        TrainableLayer(6,
//                                activator = sigmoid,
//                                optimizer = GradientDescent(1.0),
//                                weightsInitializer = xavierNormal,
//                                biasInitializer = {0.5}),
//                        TrainableLayer(14,
//                                activator = sigmoid,
//                                optimizer = GradientDescent(1.0),
//                                weightsInitializer = xavierNormal,
//                                biasInitializer = {0.5})
//                )
//                .setCostFunction(quadraticCost)
//                .build()
//
//        net.initWeights()
//
//        val trainInputs = arrayOf(intArrayOf(1, 1, 1, 0), intArrayOf(1, 1, 0, 0), intArrayOf(0, 1, 1, 0), intArrayOf(1, 0, 1, 0), intArrayOf(1, 0, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 0, 1, 0), intArrayOf(1, 1, 1, 1), intArrayOf(1, 1, 0, 1), intArrayOf(0, 1, 1, 1), intArrayOf(1, 0, 1, 1), intArrayOf(1, 0, 0, 1), intArrayOf(0, 1, 0, 1), intArrayOf(0, 0, 1, 1))
//        val trainOutput = arrayOf(intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0), intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0), intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0), intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1))
//
//        var cnt = 0
//        for (i in 0..1099) {
//            val input = DoubleVector(trainInputs[cnt])
//            val expected = DoubleVector(trainOutput[cnt])
//            net.evaluate(input, expected)
//            net.learn()
//            cnt = (cnt + 1) % trainInputs.size
//        }
//
//        for (i in trainInputs.indices) {
//            val result = net.evaluate(DoubleVector(trainInputs[i]))
//            val ix = result.indexOfMax()
//            assertEquals(DoubleVector(trainOutput[i]), DoubleVector(trainOutput[ix]))
//        }
//    }
//}