package kercept.neuro.layer

import kercept.math.Vector

class InputLayer(size: Int) : Layer(size) {

    override fun evaluate(signal: Vector) = signal.also { out.set(it) }
}