package kercept.neuro.layer

import kercept.math.Vector

abstract class Layer(val size: Int) {

    val out = ThreadLocal<Vector>()

    abstract fun evaluate(signal: Vector): Vector
}