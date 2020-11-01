package neuro.example.mnist

import kercept.math.FloatVector

class DigitEntry(
        val label: Int,
        val labelVector: FloatVector,
        val image: FloatVector
) {

}