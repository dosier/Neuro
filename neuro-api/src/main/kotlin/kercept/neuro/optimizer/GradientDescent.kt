package kercept.neuro.optimizer

import kercept.math.FloatMatrix
import kercept.math.FloatVector

class GradientDescent(private val learningRate : Float = 0F) : Optimizer {

    override fun updateWeights(weights: FloatMatrix, dCdW: FloatMatrix) {
        weights -= (dCdW * learningRate)
    }

    override fun updateBias(bias: FloatVector, dCdB: FloatVector) {
        bias -= (dCdB * learningRate)
    }
}