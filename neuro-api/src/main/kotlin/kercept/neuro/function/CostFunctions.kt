package kercept.neuro.function

import kercept.math.FloatVector

val quadraticCost : CostFunction = object : CostFunction(){

    override fun getDerivative(expected: FloatVector, actual: FloatVector): FloatVector {
        val C = FloatVector(actual.size)
        for(i in 0 until C.size){
            C[i] = (actual[i] - expected[i]) * 2F
        }
        return C
    }

    override fun invoke(expected: FloatVector, actual: FloatVector): Float {
        return (actual - expected).let { it dot it }
    }

}