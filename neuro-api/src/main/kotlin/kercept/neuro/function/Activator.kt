package kercept.neuro.function

import kercept.math.Vector

open class Activator(
    val y : (Double) -> Double = {0.0},
    val yd : (Double) -> Double = {0.0}
) {


    open fun dCdI(out: Vector, dCd0: Vector) = dCd0 * invokeYd(out)

    open fun invokeY(vector: Vector) {
        for(i in 0 until vector.size){
            vector[i] = y.invoke(vector[i])
        }
    }
    open fun invokeYd(vector: Vector): Vector {
        val newVec = Vector(vector.size)
        for(i in 0 until newVec.size){
            newVec[i] = yd.invoke(vector[i])
        }
        return newVec
    }
}
