package kercept.neuro.optimizer

import kercept.math.Matrix
import kercept.math.Vector

class GradientDescent(private val learningRate: Double = 0.1) : Optimizer {

    override fun updateWeights(weights: Matrix, dCdW: Matrix) {
        weights -= (dCdW * learningRate)
    }

    override fun updateBias(bias: Vector, dCdB: Vector) {
        bias -= (dCdB * learningRate)
    }
}