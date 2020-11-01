package mnistexample

import kercept.neuro.NeuralNet
import kercept.neuro.empty
import kercept.neuro.function.*
import kercept.neuro.layer.TrainableLayer
import kercept.neuro.optimizer.Nesterov
import kercept.neuro.random
import kercept.neuro.xavierNormal

class MnistNetBuilder() {

    fun create() = NeuralNet
                .builder(28 * 28)
                .add(
//                        TrainableLayer(256,
////                            l2 = 0.00010,
//                                weightsInitializer = xavierNormal,
//                                optimizer = Nesterov(0.02F, 0.90F),
//                                activator = leakyRelu),
                        TrainableLayer(128,
//                                l2 = 0.001F,
                                weightsInitializer = xavierNormal,
                                optimizer = Nesterov(0.02F, 0.87F),
                                activator = leakyRelu),
//                        TrainableLayer(64,
////                                l2 = 0.001F,
//                                weightsInitializer = xavierNormal,
////                            optimizer = Nesterov(0.02, 0.87),
//                                activator = leakyRelu),
                        TrainableLayer(28,
//                                l2 = 0.001F,
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
}