package neuro.example.mnist

import java.awt.Graphics
import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

class MnistViewer(rawImageData: Array<IntArray>) : JFrame("Image Viewer") {

    private var image: Image

    init {

        val bufferedImage = BufferedImage(28, 28, BufferedImage.TYPE_INT_RGB)
        val raster = bufferedImage.raster

        for((r, pixelsRow) in rawImageData.withIndex()){
            for((c, pixel) in pixelsRow.withIndex()){
                raster.setPixel(r, c, intArrayOf(pixel, pixel, pixel))
            }
        }

        image = bufferedImage

        val pane = JPanel()
        val label = JLabel(ImageIcon(bufferedImage))
        pane.add(label)

        add(pane)
        pack()

        isVisible = true
    }

}