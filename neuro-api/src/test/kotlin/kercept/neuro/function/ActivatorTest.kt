package kercept.neuro.function

import kercept.math.Vector
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ActivatorTest {

    @Test
    fun testSoftMax() {

        val out = Vector(-1.0, 0.0, 1.5, 2.0)
        softmax.invokeY(out)
        assertEquals(out, Vector(0.02778834297343303, 0.0755365477476706, 0.3385313204518047, 0.5581437888270917))
    }

    @Test
    fun testLReLU(){
        val out =  Vector(-1.0, 0.0, 1.5, 2.0)
        leakyRelu.invokeY(out)
        assertEquals(out, Vector(-0.01, 0.0, 1.5, 2.0))
    }
}