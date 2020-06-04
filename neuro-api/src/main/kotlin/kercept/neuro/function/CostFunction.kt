package kercept.neuro.function

import kercept.math.Vector

abstract class CostFunction : (Vector, Vector) -> Double {

    abstract fun getDerivative(expected: Vector, actual: Vector) : Vector
}