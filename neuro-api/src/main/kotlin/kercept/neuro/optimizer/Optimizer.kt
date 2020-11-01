package kercept.neuro.optimizer

import kercept.math.FloatMatrix
import kercept.math.FloatVector

interface Optimizer {

    fun updateWeights(weights: FloatMatrix, dCdW: FloatMatrix)

    fun updateBias(bias: FloatVector, dCdB: FloatVector)
}