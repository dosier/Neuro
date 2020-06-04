package kercept.neuro.function

import kercept.neuro.xavierNormal
import org.junit.Assert.assertEquals
import org.junit.Test

internal class InitializerTest {

    @Test
    fun testXavierNormal(){
        val cols = 700
        val rows = 300
        val m = xavierNormal.initWeights(rows, cols, 0)
        assertEquals(0.0, m.components.average(), 0.001)
        assertEquals(2.0/(cols+rows), m.variance(), 0.001)
    }
}