package kercept.neuro.function

import kercept.math.FloatVector

open class Activator(
    val y : (Float) -> Float = {0F},
    val yd : (Float) -> Float = {0F}
) {


    open fun dCdI(out: FloatVector, dCd0: FloatVector) = dCd0 * invokeYd(out)

    open fun invokeY(vector: FloatVector) {
        for(i in 0 until vector.size){
            vector[i] = y.invoke(vector[i])
        }
    }
    open fun invokeYd(vector: FloatVector): FloatVector {
        val newVec = FloatVector(vector.size)
        for(i in 0 until newVec.size){
            newVec[i] = yd.invoke(vector[i])
        }
        return newVec
    }
}
