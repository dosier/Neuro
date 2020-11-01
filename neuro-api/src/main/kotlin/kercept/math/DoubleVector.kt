//package kercept.math
//
//import java.util.*
//import kotlin.math.pow
//import kotlin.math.sqrt
//
//class DoubleVector(vararg val components: Double) {
//
//    constructor(size: Int) : this(*DoubleArray(size))
//
//    constructor(size: Int, function: (Int) -> Double) : this(size) {
//        for(i in 0 until size)
//            components[i] = function.invoke(i)
//    }
//
//    constructor(ints: IntArray) : this(ints.size, {ints[it].toDouble()})
//
//    val size = components.size
//
//    fun print(format: String = "%5.1f") {
//        components.forEach {
//            System.out.printf(format, it)
//        }
//        println()
//    }
//
//    fun apply(function: (Double) -> Double): DoubleVector {
//        val C = DoubleVector(size)
//        for(i in 0 until size){
//            C[i] = function.invoke(this[i])
//        }
//        return C
//    }
//
//    fun pow(n: Int) : DoubleVector {
//        val C = DoubleVector(size)
//        for(i in 0 until size){
//            C[i] = this[i].pow(n)
//        }
//        return C
//    }
//
//    fun sum() = components.sum()
//
//    fun elementProduct(other: DoubleVector) : DoubleVector {
//        if(other.size != size)
//            throw IncorrectDimensions()
//
//        val result = DoubleVector(other.size)
//
//        for (i in components.indices)
//            result[i] = this[i] * other[i]
//
//        return result
//    }
//
//    fun outerProduct(u: DoubleVector): DoubleMatrix {
//        val mat = DoubleMatrix(u.size, size)
//
//        for(i in 0 until size) {
//            for(j in 0 until u.size){
//                mat.set(j, i, this[i] * u[j])
//            }
//        }
//        return mat
//    }
//
//    fun fill(d: Double) { Arrays.fill(components, d) }
//
//    fun magnitude() = sqrt(this dot this)
//
//    operator fun get(i: Int) = components[i]
//
//    operator fun set(i: Int, value: Double) {
//        components[i] = value
//    }
//
//    operator fun plus(exp: DoubleVector): DoubleVector {
//        val C = DoubleVector(size)
//        for(i in 0 until size){
//            C[i] = this[i] + exp[i]
//        }
//        return C
//    }
//
//    operator fun plusAssign(other: DoubleVector) {
//        if(other.size != size)
//            throw IncorrectDimensions()
//        for(i in 0 until  size)
//            this[i] += other[i]
//    }
//
//    operator fun minus(exp: DoubleVector): DoubleVector {
//        val C = DoubleVector(size)
//        for(i in 0 until size){
//            C[i] = this[i] - exp[i]
//        }
//        return C
//    }
//
//    operator fun minusAssign(other: DoubleVector){
//        for(i in 0 until size){
//            this[i] -= other[i]
//        }
//    }
//
//    operator fun minus(x: Double): DoubleVector {
//        val C = DoubleVector(size)
//        for(i in 0 until size){
//            C[i] = this[i] - x
//        }
//        return C
//    }
//
//    operator fun times(w: DoubleMatrix): DoubleVector {
//        if (size != w.rows)
//            throw IncorrectDimensions()
//
//        val result = DoubleVector(w.cols)
//
//        for (col in 0 until w.cols) {
//            for (row in 0 until w.rows) {
//                result[col] += (w.get(row, col) * this[row])
//            }
//        }
//        return result
//    }
//
//    operator fun times(other: DoubleVector): DoubleVector {
//        val C = DoubleVector(size)
//        for(i in 0 until size){
//            C[i] = this[i] * other[i]
//        }
//        return C
//    }
//
//    operator fun times(scalar: Double): DoubleVector {
//        val C = DoubleVector(size)
//        for(i in 0 until size){
//            C[i] = this[i] * scalar
//        }
//        return C
//    }
//
//    infix fun dot(other: DoubleVector): Double {
//        var product = 0.0
//        for(i in 0 until size){
//            product += (this[i] * other[i])
//        }
//        return product
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as DoubleVector
//
//        if (!components.contentEquals(other.components)) return false
//        if (size != other.size) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = components.contentHashCode()
//        result = 31 * result + size
//        return result
//    }
//
//    override fun toString(): String {
//        return "Vector(components=${components.contentToString()}, size=$size)"
//    }
//
//    fun indexOfMax(): Int {
//        var largestIndex = 0
//        var largest = this[largestIndex]
//        for(i in 1 until components.size){
//            val next = this[i]
//            if(next > largest){
//                largest = next
//                largestIndex = i
//            }
//        }
//        return largestIndex
//    }
//
//    fun copy() = DoubleVector(*components.copyOf())
//}