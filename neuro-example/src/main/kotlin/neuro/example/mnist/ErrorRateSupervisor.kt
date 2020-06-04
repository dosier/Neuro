package neuro.example.mnist

import kercept.neuro.NeuralNet
import java.util.*

class ErrorRateSupervisor(
        private val net: NeuralNet,
        private val windowSize: Int,
        private var acceptableErrorRate: Double? = null
) {



    private val errorRates = LinkedList<Double>()

    private var lowestErrorRate = Double.MAX_VALUE
    private var lastErrorAverage = Double.MAX_VALUE

    fun stop(errorRate: Double): Boolean{

        if(errorRate < lowestErrorRate)
            lowestErrorRate = errorRate

        acceptableErrorRate?.also {
            if (lowestErrorRate < it)
                return true
        }

        errorRates.addLast(errorRate)

        if(errorRates.size < windowSize)
            return false

        if(errorRates.size > windowSize)
            errorRates.removeFirst()

        val average = errorRates.average()

        return if(average > lastErrorAverage)
            true
        else {
            lastErrorAverage = average
            false
        }
    }
}