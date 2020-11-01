package kercept.math

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class FloatVector(vararg val components: Float) {

    constructor(size: Int) : this(*FloatArray(size))

    constructor(size: Int, function: (Int) -> Float) : this(size) {
        for(i in 0 until size)
            components[i] = function.invoke(i)
    }

    constructor(ints: IntArray) : this(ints.size, {ints[it].toFloat()})

    val size = components.size

    fun print(format: String = "%5.1f") {
        components.forEach {
            System.out.printf(format, it)
        }
        println()
    }

    fun apply(function: (Float) -> Float): FloatVector {
        val C = FloatVector(size)
        for(i in 0 until size){
            C[i] = function.invoke(this[i])
        }
        return C
    }

    fun pow(n: Int) : FloatVector {
        val C = FloatVector(size)
        for(i in 0 until size){
            C[i] = this[i].pow(n)
        }
        return C
    }

    fun sum() = components.sum()

    fun elementProduct(other: FloatVector) : FloatVector {
        if(other.size != size)
            throw IncorrectDimensions()

        val result = FloatVector(other.size)

        for (i in components.indices)
            result[i] = this[i] * other[i]

        return result
    }

    fun outerProduct(u: FloatVector): FloatMatrix {
        val mat = FloatMatrix(u.size, size)

        for(i in 0 until size) {
            for(j in 0 until u.size){
                mat.set(j, i, this[i] * u[j])
            }
        }
        return mat
    }

    fun fill(d: Float) { Arrays.fill(components, d) }

    fun magnitude() = sqrt(this dot this)

    operator fun get(i: Int) = components[i]

    operator fun set(i: Int, value: Float) {
        components[i] = value
    }

    operator fun plus(exp: FloatVector): FloatVector {
        val C = FloatVector(size)
        for(i in 0 until size){
            C[i] = this[i] + exp[i]
        }
        return C
    }

    operator fun plusAssign(other: FloatVector) {
        if(other.size != size)
            throw IncorrectDimensions()
        for(i in 0 until  size)
            this[i] += other[i]
    }

    operator fun minus(exp: FloatVector): FloatVector {
        val C = FloatVector(size)
        for(i in 0 until size){
            C[i] = this[i] - exp[i]
        }
        return C
    }

    operator fun minusAssign(other: FloatVector){
        for(i in 0 until size){
            this[i] -= other[i]
        }
    }

    operator fun minus(x: Float): FloatVector {
        val C = FloatVector(size)
        for(i in 0 until size){
            C[i] = this[i] - x
        }
        return C
    }

    fun times(w: FloatMatrix, base: FloatVector): FloatVector {
        if (size != w.rows)
            throw IncorrectDimensions()

        val result = FloatVector(w.cols)

        for (col in 0 until w.cols) {
            var value = base[col]
            for (row in 0 until w.rows) {
                value += (w.get(row, col) * this[row])
            }
            result[col] = value
        }
        return result
    }

    operator fun times(w: FloatMatrix): FloatVector {
        if (size != w.rows)
            throw IncorrectDimensions()

        val result = FloatVector(w.cols)

        for (col in 0 until w.cols) {
            for (row in 0 until w.rows) {
                result[col] += (w.get(row, col) * this[row])
            }
        }
        return result
    }

    operator fun times(other: FloatVector): FloatVector {
        val C = FloatVector(size)
        for(i in 0 until size){
            C[i] = this[i] * other[i]
        }
        return C
    }

    operator fun times(scalar: Float): FloatVector {
        val C = FloatVector(size)
        for(i in 0 until size){
            C[i] = this[i] * scalar
        }
        return C
    }

    infix fun dot(other: FloatVector): Float {
        var product = 0F
        for(i in 0 until size){
            product += (this[i] * other[i])
        }
        return product
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FloatVector

        if (!components.contentEquals(other.components)) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = components.contentHashCode()
        result = 31 * result + size
        return result
    }

    override fun toString(): String {
        return "Vector(components=${components.contentToString()}, size=$size)"
    }

    fun indexOfMax(): Int {
        var largestIndex = 0
        var largest = this[largestIndex]
        for(i in 1 until components.size){
            val next = this[i]
            if(next > largest){
                largest = next
                largestIndex = i
            }
        }
        return largestIndex
    }

    fun copy() = FloatVector(*components.copyOf())
}