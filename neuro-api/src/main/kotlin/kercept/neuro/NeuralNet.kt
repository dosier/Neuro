package kercept.neuro

import com.google.gson.GsonBuilder
import kercept.math.FloatVector
import kercept.neuro.function.CostFunction
import kercept.neuro.layer.InputLayer
import kercept.neuro.layer.Layer
import kercept.neuro.layer.TrainableLayer
import java.util.*
import kotlin.collections.ArrayList

class NeuralNet(
    private val layers: List<Layer>,
    private val costFunction: CostFunction
) {

    fun initWeights(){
        val iterator = layers.listIterator()

        while (iterator.hasNext()) {

            val previous = if(iterator.hasPrevious())
                layers[iterator.previousIndex()]
            else
                null

            val layerIndex = iterator.nextIndex()
            val layer = iterator.next()

            if (layer is TrainableLayer) {
                layer.precedingLayer = previous!!
                layer.createWeights(previous.size, layerIndex)
            }
        }
    }

    fun evaluate(input: FloatVector): FloatVector {

        var signal = input

        for(layer in layers)
            signal = layer.evaluate(signal)

        return signal
    }

    fun evaluate(input: FloatVector, expected: FloatVector): Pair<FloatVector, Float> {

        val signal = evaluate(input)

        learnFrom(expected)

        val cost = costFunction.invoke(expected, signal)

        return Pair(signal, cost)
    }

    private fun learnFrom(expected: FloatVector) {

        var layer = layers.last()
        var dCd0 = costFunction.getDerivative(expected, layer.out.get())

        do {
            if(layer is TrainableLayer) {

                if(!layer.hasPrecedingLayer())
                    break

                val previousLayer = layer.precedingLayer

                val dCdI =  layer.activator.dCdI(layer.out.get(), dCd0)
                val dCdW = dCdI.outerProduct(previousLayer.out.get())

                layer.addWeightsAndBias(dCdW, dCdI)

                dCd0 = layer.weights * dCdI

                layer = previousLayer

            } else break
        } while (true)
    }

    @Synchronized
    fun learn() {
        for(layer in layers){
            if(layer is TrainableLayer && layer.hasPrecedingLayer())
                layer.update()
        }
    }

    fun toJson() = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(this)!!

    class Builder(private val inputSize: Int){

        private val addedLayers = ArrayList<Layer>()
        private lateinit var costFunction: CostFunction

        fun add(vararg layers: Layer) = also {
            this.addedLayers += layers
        }

        fun setCostFunction(costFunction: CostFunction) = also {
            this.costFunction = costFunction
        }

        fun build(): NeuralNet {
            val layers = List(addedLayers.size + 1) {
                if(it == 0)
                    InputLayer(inputSize)
                else
                    addedLayers[it-1]
            }
            return NeuralNet(
                layers,
                costFunction
            )
        }
    }

    companion object {

        lateinit var random : Random

        fun builder(size: Int) = Builder(size)
    }
}