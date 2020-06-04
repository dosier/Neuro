package neuro.example.mnist

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.file.Paths

internal class MnistReaderTest {

    @Test
    fun test(){
        val reader = MnistReader()

        val imagesPath = Paths.get("data/train-images-idx3-ubyte")
        val images = reader.readImages(imagesPath.toFile())

        val labelsPath = Paths.get("data/train-labels-idx1-ubyte")
        val labels = reader.readLabels(labelsPath.toFile())

        assertEquals(labels.size, images.size)
        assertEquals(28, images[0].size)
        assertEquals(28, images[0][0].size)

        for (i in 0 until labels.size.coerceAtMost(10)) {
            System.out.printf("================= LABEL %d\n", labels[i])
            System.out.printf("%s", reader.imageToString(images[i]))
        }
    }
}