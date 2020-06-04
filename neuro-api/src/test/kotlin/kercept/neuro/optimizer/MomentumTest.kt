package kercept.neuro.optimizer

import kercept.math.Matrix
import kercept.math.Vector
import org.junit.Assert.assertArrayEquals
import org.junit.Test

internal class MomentumTest {

    @Test
    fun testMomentumWeightUpdate(){
        val W = Matrix(2, 3, doubleArrayOf(2.0, 3.0, 4.0, 3.0, 4.0, 5.0))
        val dW = Matrix(2, 3, doubleArrayOf(.2, .3, .4, .3, .4, .5))
        val optimizer = Momentum(0.05)

        optimizer.updateWeights(W, dW)

        assertArrayEquals(doubleArrayOf(1.990, 2.985, 3.980), W.getRow(0), EPS)
        assertArrayEquals(doubleArrayOf(2.985, 3.980, 4.975), W.getRow(1), EPS)

        optimizer.updateWeights(W, dW)

        assertArrayEquals(doubleArrayOf(1.9710, 2.9565, 3.9420), W.getRow(0), EPS)
        assertArrayEquals(doubleArrayOf(2.9565, 3.9420, 4.9275), W.getRow(1), EPS)
    }

    @Test
    fun testMomentumBiasUpdate(){
        val bias = Vector(2.0, 3.0, 4.0)
        val db = Vector(0.2, 0.3, 0.4)
        val o = Momentum(0.05)
        o.updateBias(bias, db)
        assertArrayEquals(doubleArrayOf(1.990, 2.985, 3.980), bias.components, EPS)
        o.updateBias(bias, db)
        assertArrayEquals(doubleArrayOf(1.9710, 2.9565, 3.9420), bias.components, EPS)
    }
    companion object {
        const val EPS = 0.0000001
    }
}