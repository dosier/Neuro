package mnistexample

import kercept.neuro.NeuralNet
import kercept.neuro.function.leakyRelu
import kercept.neuro.function.quadraticCost
import kercept.neuro.function.softmax
import kercept.neuro.layer.TrainableLayer
import kercept.neuro.xavierNormal

class MnistNetBuilder() {

    fun create() = NeuralNet
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
}