package kercept.neuro.optimizer

import kercept.math.Matrix
import kercept.math.Vector

class Momentum(
        private val learningRate: Double,
        private val momentum: Double = 0.9
) : Optimizer {

    private lateinit var lastDeltaWeights: Matrix
    private lateinit var lastDeltaBias: Vector

    override fun updateWeights(weights: Matrix, dCdW: Matrix) {
        if(!this::lastDeltaWeights.isInitialized)
            lastDeltaWeights = dCdW * learningRate
        else {
            lastDeltaWeights *= momentum
            lastDeltaWeights.plusAssign(dCdW * learningRate)
        }
        weights -= lastDeltaWeights
    }

    override fun updateBias(bias: Vector, dCdB: Vector) {
        if(!this::lastDeltaBias.isInitialized)
            lastDeltaBias = dCdB * learningRate
        else {
            lastDeltaBias *= momentum
            lastDeltaBias.plusAssign(dCdB * learningRate)
        }
        bias -= lastDeltaBias
    }
}