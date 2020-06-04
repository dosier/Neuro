package neuro.example.mnist

import java.nio.file.Paths
import javax.swing.SwingUtilities

fun main() {
    val reader = MnistReader()
    val imagesPath = Paths.get("data/train-images-idx3-ubyte")

    val images = reader.readImages(imagesPath.toFile())

    SwingUtilities.invokeLater {
        MnistViewer(images[1])
    }
}