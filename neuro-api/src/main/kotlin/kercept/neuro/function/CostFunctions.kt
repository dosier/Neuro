package kercept.neuro.function

import kercept.math.Vector

val quadraticCost : CostFunction = object : CostFunction(){

    override fun getDerivative(expected: Vector, actual: Vector): Vector {
        return (actual - expected) * 2.0
    }

    override fun invoke(expected: Vector, actual: Vector): Double {
        return (actual - expected).let { it dot it }
    }

}