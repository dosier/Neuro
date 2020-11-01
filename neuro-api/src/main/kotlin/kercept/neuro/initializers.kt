package kercept.neuro

import kercept.math.FloatMatrix
import java.util.*
import kotlin.math.sqrt

interface Initializer {
    fun initWeights(rows: Int, cols: Int, layer: Int) : FloatMatrix
}

val empty : Initializer = object : Initializer {
    override fun initWeights(rows: Int, cols: Int, layer: Int): FloatMatrix {
        return  FloatMatrix(rows, cols)
    }
}

val random : (Int, Int) -> Initializer = {
    min, max ->
    val delta = (max - min).toFloat()
    object : Initializer {
        override fun initWeights(rows: Int, cols: Int, layer: Int) :  FloatMatrix
                = FloatMatrix(rows, cols) { _, _ -> min + NeuralNet.random.nextFloat() * delta}
    }
}

val xavierNormal : Initializer = object : Initializer {
    override fun initWeights(rows: Int, cols: Int, layer: Int) : FloatMatrix {
        val factor = sqrt(2.0 / (cols + rows))
        return  FloatMatrix(rows, cols) { _, _ -> (NeuralNet.random.nextGaussian() * factor).toFloat() }
    }
}
