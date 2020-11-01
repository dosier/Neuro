package kercept.neuro.layer

import kercept.math.FloatVector

class InputLayer(size: Int) : Layer(size) {

    override fun evaluate(signal: FloatVector) = signal.also { out.set(it) }
}