package kercept.neuro.function

import kercept.math.FloatVector

abstract class CostFunction : (FloatVector, FloatVector) -> Float {

    abstract fun getDerivative(expected: FloatVector, actual: FloatVector) : FloatVector
}