package kercept.neuro.layer

import kercept.math.FloatVector

abstract class Layer(val size: Int) {

    val out = ThreadLocal<FloatVector>()

    abstract fun evaluate(signal: FloatVector): FloatVector
}