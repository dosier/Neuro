package kercept.neuro.math

import kercept.math.DoubleMatrix
import kercept.math.DoubleVector
import org.junit.Assert.assertEquals
import org.junit.Test


internal class MatrixTest {

    @Test
    fun testVectorMultiplication(){

        val vec = DoubleVector(2.0, 1.0, 0.0)

        val mat = DoubleMatrix(2, 3)
        mat.setRow(0, 1.0, -1.0, 2.0)
        mat.setRow(1, 0.0, -3.0, 1.0)

        val result = mat * vec
        val expected = DoubleVector(1.0, -3.0)
        assertEquals(expected, result)
    }

    @Test
    fun testMultiply() {
        val v = DoubleVector(1.0, 2.0, 3.0)
        val W = DoubleMatrix(2, 3)
        W.setRow(0, 2.0, 3.0, 4.0)
        W.setRow(1, 3.0, 4.0, 5.0)
        val result = W * v
        assertEquals(DoubleVector(20.0, 26.0), result)
    }


    @Test
    fun testMultiply2() {
        val v = DoubleVector(1.0, 2.0)
        val W = DoubleMatrix(2, 3)
        W.setRow(0, 2.0, 1.0, 3.0)
        W.setRow(1, 3.0, 4.0, -1.0)
        val result = v * W
        assertEquals(DoubleVector(8.0, 9.0, 1.0), result)
    }

    @Test
    fun unaryOps(){
        val W = DoubleMatrix(2, 3)
        W.setRow(0, 2.0, 1.0, 3.0)
        W.setRow(1, 3.0, 4.0, -1.0)
        val V = DoubleMatrix(2, 3)
        V.setRow(0, 2.0, 1.0, 3.0)
        V.setRow(1, 3.0, 4.0, -1.0)

        val D = W + V
        W += V

        assertEquals(D, W)
    }
}