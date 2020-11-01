package kercept.neuro.layer

import kercept.math.FloatMatrix
import kercept.math.FloatVector
import kercept.neuro.Initializer
import kercept.neuro.empty
import kercept.neuro.function.Activator
import kercept.neuro.function.sigmoid
import kercept.neuro.optimizer.GradientDescent
import kercept.neuro.optimizer.Optimizer

class TrainableLayer(
        size: Int,
        biasInitializer: (Int) -> Float = {0F},
        private val weightsInitializer: Initializer = empty,
        private val l2: Float = 0F,
        val activator: Activator = sigmoid,
        private val optimizer: Optimizer = GradientDescent(0.05F)
) : Layer(size) {

    lateinit var weights : FloatMatrix
    lateinit var precedingLayer: Layer

    var bias = FloatVector(size, biasInitializer)

    @Transient lateinit var deltaWeights : FloatMatrix
    @Transient var deltaWeightsAdded = 0

    @Transient var deltaBias = FloatVector(size)
    @Transient var deltaBiasAdded = 0

    fun createWeights(previousSize: Int, layerIndex: Int){
        weights = weightsInitializer.initWeights(previousSize, size, layerIndex)
        deltaWeights = FloatMatrix(previousSize, size)
    }

    override fun evaluate(signal: FloatVector): FloatVector {

        val newSignal = signal.times(weights, bias)

        activator.invokeY(newSignal)

        out.set(newSignal)
        return newSignal
    }

    @Synchronized
    fun addWeightsAndBias(dCdW: FloatMatrix, dCdI: FloatVector) {
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

            val averageDW = deltaWeights * (1F/deltaWeightsAdded)

            optimizer.updateWeights(weights, averageDW)

            deltaWeights.fill(0F)
            deltaWeightsAdded = 0
        }

        if(deltaBiasAdded > 0){

            val averageBias = deltaBias * (1F/deltaBiasAdded)

            optimizer.updateBias(bias, averageBias)

            deltaBias.fill(0F)
            deltaBiasAdded = 0
        }
    }

    fun hasPrecedingLayer() = this::precedingLayer.isInitialized

}