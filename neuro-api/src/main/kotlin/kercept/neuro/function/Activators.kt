package kercept.neuro.function

import kercept.math.FloatVector
import kotlin.math.exp
import kotlin.math.pow


val sigmoid = Activator(
        y = {x -> 1F / (1F + exp(-x))},
        yd = {x -> x * (1F - x)}
)
val relu = Activator(
        y = {x -> if(x <= 0) 0F else x },
        yd = {x -> if(x <= 0) 0F else 1F}
)

val leakyRelu = Activator(
        y = {x -> if(x <= 0) 0.01F * x else x },
        yd = {x -> if(x <= 0) 0.01F else 1F}
)

var a = 1F

val elu = object : Activator() {
    override fun invokeY(vector: FloatVector) {
        for((i, x) in vector.components.withIndex()){
            if(x <= 0)
                vector[i] = fl(x)
        }
    }

    private fun fl(x: Float) = a * (Math.E.pow(x.toDouble()).toFloat() - 1F)

    override fun invokeYd(vector: FloatVector): FloatVector {
        val result = FloatVector(vector.size)

        for((i, x) in vector.components.withIndex()){
            if(x <= 0)
                result[i] = fl(x) + a
            else
                result[i] = 1F
        }

        return result
    }
}

val softmax = object : Activator() {
    override fun invokeY(vector: FloatVector) {
        var sum = 0F
        val max = vector.components.max()!!
        for(comp in vector.components){
            sum += exp(comp - max)
        }
        for(i in 0 until vector.size){
            vector[i] = exp(vector[i]-max)/sum
        }
    }

    override fun dCdI(out: FloatVector, dCd0: FloatVector): FloatVector {
        val x: Float = (out * dCd0).sum()
        val sub = dCd0 - x
        return out * sub
    }
}