package kercept.neuro.layer

import kercept.math.Matrix
import kercept.math.Vector
import kercept.neuro.Initializer
import kercept.neuro.empty
import kercept.neuro.function.Activator
import kercept.neuro.optimizer.Optimizer
import kercept.neuro.function.sigmoid
import kercept.neuro.optimizer.GradientDescent

class TrainableLayer(
        size: Int,
        biasInitializer: (Int) -> Double = {0.0},
        private val weightsInitializer: Initializer = empty,
        private val l2: Double = 0.0,
        val activator: Activator = sigmoid,
        private val optimizer: Optimizer = GradientDescent(0.05)
) : Layer(size) {

    lateinit var weights : Matrix
    lateinit var precedingLayer: Layer

    var bias = Vector(size, biasInitializer)

    @Transient lateinit var deltaWeights : Matrix
    @Transient var deltaWeightsAdded = 0

    @Transient var deltaBias = Vector(size)
    @Transient var deltaBiasAdded = 0

    fun createWeights(previousSize: Int, layerIndex: Int){
        weights = weightsInitializer.initWeights(previousSize, size, layerIndex)
        deltaWeights = Matrix(previousSize, size)
    }

    override fun evaluate(signal: Vector): Vector {

        val newSignal = signal * weights
        newSignal += (bias)

        activator.invokeY(newSignal)

        out.set(newSignal)
        return newSignal
    }

    @Synchronized
    fun addWeightsAndBias(dCdW: Matrix, dCdI: Vector) {
        deltaWeights.plusAssign(dCdW)
        deltaWeightsAdded++
        deltaBias.plusAssign(dCdI)
        deltaBiasAdded++
    }

    @Synchronized
    fun update(){
        if(deltaWeightsAdded > 0){

            if(l2 > 0)
                weights.map {it - (l2 * it)}

            val averageDW = deltaWeights * (1.0/deltaWeightsAdded)

            optimizer.updateWeights(weights, averageDW)

            deltaWeights.fill(0.0)
            deltaWeightsAdded = 0
        }

        if(deltaBiasAdded > 0){

            val averageBias = deltaBias * (1.0/deltaBiasAdded)

            optimizer.updateBias(bias, averageBias)

            deltaBias.fill(0.0)
            deltaBiasAdded = 0
        }
    }

    fun hasPrecedingLayer() = this::precedingLayer.isInitialized

}