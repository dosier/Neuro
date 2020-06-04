package kercept.neuro.optimizer

import kercept.math.Matrix
import kercept.math.Vector

interface Optimizer {

    fun updateWeights(weights: Matrix, dCdW: Matrix)

    fun updateBias(bias: Vector, dCdB: Vector)
}