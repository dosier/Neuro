package neuro.example.mnist

import kercept.math.Vector
import java.io.File
import java.nio.ByteBuffer
import kotlin.experimental.and

/**
 * Based of https://github.com/jeffgriffith/mnist-reader/blob/master/src/main/java/mnist/MnistReader.java
 */
class MnistReader {

    fun readLabels(file: File) : IntArray {

        val buffer = ByteBuffer.wrap(file.readBytes())

        val magicNumber = buffer.int

        assertMagicNumber(LABEL_FILE_MAGIC_NUMBER, magicNumber)

        val count = buffer.int

        return IntArray(count) {
            (buffer.get() and 0xFF.toByte()).toInt()
        }
    }

    fun readImages(file: File) : Array<Array<IntArray>> {

        val buffer = ByteBuffer.wrap(file.readBytes())

        val magicNumber = buffer.int

        assertMagicNumber(IMAGE_FILE_MAGIC_NUMBER, magicNumber)

        val count = buffer.int
        val numRows = buffer.int
        val numCols = buffer.int

        return Array(count) {
            Array(numRows) {
                IntArray(numCols) {
                    (buffer.get() and 0xFF.toByte()).toInt()
                }
            }
        }
    }

    fun labelToVector(label: Int) = Vector(10) {
        if(it == label)
            1.0
        else
            0.0
    }

    fun imageToVector(image: Array<IntArray>): Vector {

        val vector = Vector(28*28)
        var i = 0
        for(pixelRow in image){
            for(pixel in pixelRow){
                vector[i++] = pixel.div(255.0)
            }
        }

        return vector
    }

    fun imageToString(image: Array<IntArray>): String {
        val sb = StringBuffer()

        image.forEach {
            sb.append('|')
            for(pixel in it){
                sb.append(when{
                    pixel == 0 -> ' '
                    pixel < 256 / 3 -> '.'
                    pixel < 2 * (256 / 3) -> 'x'
                    else -> 'X'
                })
            }
            sb.append("|\n")
        }

        return sb.toString()
    }

    companion object {

        private const val LABEL_FILE_MAGIC_NUMBER = 2049
        private const val IMAGE_FILE_MAGIC_NUMBER = 2051

        fun assertMagicNumber(expectedMagicNumber: Int, magicNumber: Int) {
            if (expectedMagicNumber != magicNumber) {
                when (expectedMagicNumber) {
                    LABEL_FILE_MAGIC_NUMBER -> throw RuntimeException("This is not a label file.")
                    IMAGE_FILE_MAGIC_NUMBER -> throw RuntimeException("This is not an image file.")
                    else -> throw RuntimeException(
                            String.format("Expected magic number %d, found %d", expectedMagicNumber, magicNumber))
                }
            }
        }
    }
}