package kercept.neuro

import kercept.math.Matrix
import java.util.*
import kotlin.math.sqrt

interface Initializer {
    fun initWeights(rows: Int, cols: Int, layer: Int) : Matrix
}

var r = Random()

val empty : Initializer = object : Initializer {
    override fun initWeights(rows: Int, cols: Int, layer: Int): Matrix {
        return  Matrix(rows, cols)
    }
}

val random : (Int, Int) -> Initializer = {
    min, max ->
    val delta = (max - min).toDouble()
    object : Initializer {
        override fun initWeights(rows: Int, cols: Int, layer: Int) :  Matrix
                = Matrix(cols, rows) { _, _ -> min + r.nextDouble() * delta}
    }
}

val xavierNormal : Initializer = object : Initializer {
    override fun initWeights(rows: Int, cols: Int, layer: Int) : Matrix {
        val factor = sqrt(2.0 / (cols + rows))
        return  Matrix(rows, cols) { _, _ -> r.nextGaussian() * factor}
    }
}
