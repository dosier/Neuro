//package kercept.math
//
//import kercept.neuro.Initializer
//import java.util.*
//
//open class DoubleMatrix(
//        val rows: Int,
//        val cols: Int,
//        val components : DoubleArray =  DoubleArray(cols * rows)
//) {
//
//    constructor(rows: Int, cols: Int, matrixMapper: (Int, Int) -> Double) : this(rows, cols) {
//        for(r in 0 until rows){
//            for(c in 0 until cols){
//                set(r, c, matrixMapper.invoke(r, c))
//            }
//        }
//    }
//
//    fun asInitializer() : Initializer = object : Initializer {
//        override fun initWeights(rows: Int, cols: Int, layer: Int): DoubleMatrix {
//            return DoubleMatrix(rows, cols, components.copyOf())
//        }
//    }
//
//    fun variance() : Double {
//        val avg = components.average()
//        return components.map { (it-avg) * (it-avg) }.average()
//    }
//
//    fun transposed() : DoubleMatrix {
//        val T = DoubleMatrix(cols, rows)
//        for(row in 0 until rows){
//            for(col in 0 until cols){
//                T.set(col, row, get(row, col))
//            }
//        }
//        return T
//    }
//
//    fun map(function: (Double) -> Double) {
//        for(i in components.indices)
//            components[i] = function.invoke(components[i])
//    }
//
//    fun fill(d: Double) { Arrays.fill(components, d) }
//
//    fun setRow(row: Int, vararg values: Double) {
//        var i = 0
//        for(col in 0 until cols)
//            set(row, col, values[i++])
//    }
//
//    fun setCol(col: Int, vararg values: Double) {
//        var i = 0
//        for(row in 0 until rows)
//            set(row, col, values[i++])
//    }
//
//    fun print(format: String = "%5.1f") {
//        for(row in 0 until rows){
//            for(col in 0 until cols){
//                System.out.printf(format, get(row, col))
//            }
//            println()
//        }
//    }
//
//    fun get(row: Int, col: Int) = components[(row * cols) + col]
//
//    fun set(row: Int, col: Int, value: Double) { components[(row * cols) + col] = value }
//
//    fun getRow(row: Int) = components.sliceArray((row * cols) until ((row * cols)+cols))
//
//    operator fun get(i: Int) = components[i]
//
//    operator fun set(i: Int, value: Double) { components[i] = value }
//
//    operator fun plus(other: DoubleMatrix) = DoubleMatrix(rows, cols) { r, c -> get(r, c) + other.get(r, c) }
//
//    operator fun minus(other: DoubleMatrix) = DoubleMatrix(rows, cols) { r, c -> get(r, c) - other.get(r, c) }
//
//    operator fun plusAssign(other: DoubleMatrix) {
//        for(r in 0 until rows){
//            for(c in 0 until cols){
//                set(r, c, get(r, c) + other.get(r, c))
//            }
//        }
//    }
//
//    operator fun minusAssign(other: DoubleMatrix) {
//        for(r in 0 until rows){
//            for(c in 0 until cols){
//                set(r, c, get(r, c) - other.get(r, c))
//            }
//        }
//    }
//
//    operator fun times(vector: DoubleVector): DoubleVector {
//
//        if(vector.size != cols)
//            throw IncorrectDimensions()
//
//        val result = DoubleVector(rows)
//
//        for(rowA in 0 until this.rows){
//
//            val rowOffsetA = (rowA * this.cols)
//            var valueC = 0.0
//
//            for(i in 0 until vector.size){
//
//                val valueB = vector[i]
//                val valueA = this[rowOffsetA + i]
//                valueC += (valueA * valueB)
//            }
//
//            result[rowA] = valueC
//        }
//
//        return result
//    }
//
//    operator fun times(other: DoubleMatrix): DoubleMatrix {
//        val A = this
//        val B = other
//        val C = DoubleMatrix(A.rows, B.cols)
//        for(rowA in 0 until A.rows){
//
//            val rowOffsetA = (rowA * A.cols)
//
//            for(colB in 0 until B.cols){
//
//                var valueC = 0.0
//
//                for(colA in 0 until A.cols){
//                    val rowOffsetB = (colA * B.cols)
//                    val valueB = B[rowOffsetB + colB]
//                    val valueA = A[rowOffsetA + colA]
//                    valueC += (valueA * valueB)
//                }
//
//                C.set(rowA, colB, valueC)
//            }
//        }
//        return C
//    }
//
//    operator fun times(scalar: Double) = DoubleMatrix(rows, cols) { r, c -> get(r, c) * scalar }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is DoubleMatrix) return false
//
//        if (rows != other.rows) return false
//        if (cols != other.cols) return false
//        if (!components.contentEquals(other.components)) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = rows
//        result = 31 * result + cols
//        result = 31 * result + components.contentHashCode()
//        return result
//    }
//}