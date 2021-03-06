package kercept.neuro.function

import kercept.math.DoubleVector
import org.junit.Assert.assertEquals
import org.junit.Test

class CostFunctionTest {

    @Test
    fun testQuadraticCost(){
        val expected = DoubleVector(1.0, 2.0, 3.0)
        val actual = DoubleVector(4.0, -3.0, 7.0)
        val cost = quadraticCost.invoke(expected, actual)
        assertEquals((3 * 3 + 5 * 5 + 4 * 4).toDouble(), cost, 0.01)
        val err = quadraticCost.getDerivative(expected, actual)
        assertEquals(DoubleVector(3.0, -5.0, 4.0) * 2.0, err)
    }

    @Test
    fun testQuadraticCost2(){
        val expected = DoubleVector(1.0, 0.2)
        val actual = DoubleVector(0.712257432295742, 0.533097573871501)
        val cost = quadraticCost.invoke(expected, actual)
        assertEquals(0.19374977898811957, cost, 0.0000001)
    }
}