package kercept.neuro.optimizer

import kercept.math.FloatMatrix
import kercept.math.FloatVector

class Nesterov(
        private val learningRate: Float,
        private val momentum: Float = 0.9F
) : Optimizer{

    private lateinit var lastDeltaWeights: FloatMatrix
    private lateinit var lastDeltaBias: FloatVector

    override fun updateWeights(weights: FloatMatrix, dCdW: FloatMatrix) {

        if(!this::lastDeltaWeights.isInitialized)
            lastDeltaWeights = FloatMatrix(dCdW.rows, dCdW.cols)

        val newLastDeltaWeight = (lastDeltaWeights * momentum) - (dCdW * learningRate)

        weights += (lastDeltaWeights * -momentum) + (newLastDeltaWeight * (1 + momentum))

        lastDeltaWeights = newLastDeltaWeight
    }

    override fun updateBias(bias: FloatVector, dCdB: FloatVector) {

        if(!this::lastDeltaBias.isInitialized)
            lastDeltaBias = FloatVector(dCdB.size)

        val newLastDeltaBias = (lastDeltaBias * momentum) - (dCdB * learningRate)

        bias += (lastDeltaBias * -momentum) + (newLastDeltaBias * (1 + momentum))

        lastDeltaBias = newLastDeltaBias
    }
}