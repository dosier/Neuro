package kercept.neuro.optimizer

import kercept.math.Matrix
import kercept.math.Vector

class Nesterov(
        private val learningRate: Double,
        private val momentum: Double = 0.9
) : Optimizer{

    private lateinit var lastDeltaWeights: Matrix
    private lateinit var lastDeltaBias: Vector

    override fun updateWeights(weights: Matrix, dCdW: Matrix) {

        if(!this::lastDeltaWeights.isInitialized)
            lastDeltaWeights = Matrix(dCdW.rows, dCdW.cols)

        val newLastDeltaWeight = (lastDeltaWeights * momentum) - (dCdW * learningRate)

        weights += (lastDeltaWeights * -momentum) + (newLastDeltaWeight * (1 + momentum))

        lastDeltaWeights = newLastDeltaWeight
    }

    override fun updateBias(bias: Vector, dCdB: Vector) {

        if(!this::lastDeltaBias.isInitialized)
            lastDeltaBias = Vector(dCdB.size)

        val newLastDeltaBias = (lastDeltaBias * momentum) - (dCdB * learningRate)

        bias += (lastDeltaBias * -momentum) + (newLastDeltaBias * (1 + momentum))

        lastDeltaBias = newLastDeltaBias
    }
}