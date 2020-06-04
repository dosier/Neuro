package kercept.neuro.function

import kercept.math.Vector
import kotlin.math.exp


val sigmoid = Activator(
        y = {x -> 1.0 / (1.0 + exp(-x))},
        yd = {x -> x * (1.0 - x)}
)
val relu = Activator(
        y = {x -> if(x <= 0) 0.0 else x },
        yd = {x -> if(x <= 0) 0.0 else 1.0}
)

val leakyRelu = Activator(
        y = {x -> if(x <= 0) 0.01 * x else x },
        yd = {x -> if(x <= 0) 0.01 else 1.0}
)

val softmax = object : Activator() {
    override fun invokeY(vector: Vector) {
        var sum = 0.0
        val max = vector.components.max()!!
        for(comp in vector.components){
            sum += exp(comp - max)
        }
        for(i in 0 until vector.size){
            vector[i] = exp(vector[i]-max)/sum
        }
    }

    override fun dCdI(out: Vector, dCd0: Vector): Vector {
        val x: Double = (out * dCd0).sum()
        val sub = dCd0 - x
        return out * sub
    }
}