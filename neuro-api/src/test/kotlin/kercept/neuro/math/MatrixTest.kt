package kercept.neuro.math

import kercept.math.Matrix
import kercept.math.Vector
import org.junit.Assert.assertEquals
import org.junit.Test


internal class MatrixTest {

    @Test
    fun testVectorMultiplication(){

        val vec = Vector(2.0, 1.0, 0.0)

        val mat = Matrix(2, 3)
        mat.setRow(0, 1.0, -1.0, 2.0)
        mat.setRow(1, 0.0, -3.0, 1.0)

        val result = mat * vec
        val expected = Vector(1.0, -3.0)
        assertEquals(expected, result)
    }

    @Test
    fun testMultiply() {
        val v = Vector(1.0, 2.0, 3.0)
        val W = Matrix(2, 3)
        W.setRow(0, 2.0, 3.0, 4.0)
        W.setRow(1, 3.0, 4.0, 5.0)
        val result = W * v
        assertEquals(Vector(20.0, 26.0), result)
    }


    @Test
    fun testMultiply2() {
        val v = Vector(1.0, 2.0)
        val W = Matrix(2, 3)
        W.setRow(0, 2.0, 1.0, 3.0)
        W.setRow(1, 3.0, 4.0, -1.0)
        val result = v * W
        assertEquals(Vector(8.0, 9.0, 1.0), result)
    }

    @Test
    fun unaryOps(){
        val W = Matrix(2, 3)
        W.setRow(0, 2.0, 1.0, 3.0)
        W.setRow(1, 3.0, 4.0, -1.0)
        val V = Matrix(2, 3)
        V.setRow(0, 2.0, 1.0, 3.0)
        V.setRow(1, 3.0, 4.0, -1.0)

        val D = W + V
        W += V

        assertEquals(D, W)
    }
}