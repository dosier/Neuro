package kercept.neuro.optimizer

import kercept.math.FloatMatrix
import kercept.math.FloatVector

class Momentum(
        private val learningRate: Float,
        private val momentum: Float = 0.9F
) : Optimizer {

    private lateinit var lastDeltaWeights: FloatMatrix
    private lateinit var lastDeltaBias: FloatVector

    override fun updateWeights(weights: FloatMatrix, dCdW: FloatMatrix) {
        if(!this::lastDeltaWeights.isInitialized)
            lastDeltaWeights = dCdW * learningRate
        else {
            lastDeltaWeights *= momentum
            lastDeltaWeights.plusAssign(dCdW * learningRate)
        }
        weights -= lastDeltaWeights
    }

    override fun updateBias(bias: FloatVector, dCdB: FloatVector) {
        if(!this::lastDeltaBias.isInitialized)
            lastDeltaBias = dCdB * learningRate
        else {
            lastDeltaBias *= momentum
            lastDeltaBias.plusAssign(dCdB * learningRate)
        }
        bias -= lastDeltaBias
    }
}