package neuro.example.mnist

import com.aparapi.Kernel
import com.aparapi.Range
import kercept.math.FloatVector
import kercept.neuro.function.softmax
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random
import kotlin.system.measureTimeMillis


class AparapiTest {
    @Test
    fun test() {
        val times = 100
        val n = 100_000_000

        val a = FloatVector(n) {Random.nextFloat()} // get a float array of data from somewhere
        val aArray = a.components.copyOf()
        val b = FloatVector(n) {Random.nextFloat()}// get a float array of data from somewhere
        val bArray = b.components.copyOf()

        Kernel.invalidateCaches()

        var results1 : FloatVector? = null
        var results2 : FloatVector? = null
        val dur1 = measureTimeMillis {
            val out = FloatVector(n)
            for(j in 0 until times) {
                softmax.dCdI(out, b)
                softmax.invokeY(a)
                results1 = out
                results2 = a
            }
        }

        var results3 : FloatArray? = null
        var results4 : FloatArray? = null
        val dur2 = measureTimeMillis {
            val out = FloatArray(n)
            val cdiRange = Range.create(n+n)
            val inyRange = Range.create(n+n+n)
            val result = FloatArray(n)
            val cdiKernel = Softmax_dCdI_Kernel(out, result, bArray)
            val inyKernel = Softmax_invokeY_Kernel(aArray)
            for(i in 0 until times) {
                cdiKernel.execute(cdiRange)
                inyKernel.execute(inyRange)
                results3 = cdiKernel.result
                results4 = inyKernel.vector
            }
        }
        println("dur1 = $dur1")
        println("dur2 = $dur2")

        Assert.assertArrayEquals(
                results1!!.components.map { it.toInt() }.toIntArray(),
                results3!!.map { it.toInt() }.toIntArray())

        Assert.assertArrayEquals(
                results2!!.components.map { it.toInt() }.toIntArray(),
                results4!!.map { it.toInt() }.toIntArray())
    }
}