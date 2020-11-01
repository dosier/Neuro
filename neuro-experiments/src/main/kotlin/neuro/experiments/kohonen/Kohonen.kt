package neuro.experiments.kohonen

import kercept.math.FloatVector
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.lang.Integer.max
import java.lang.Integer.min
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.*
import javax.swing.BoxLayout.*
import javax.swing.text.NumberFormatter
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


val trainDataPath: Path = Paths.get("/Users/stanbend/PycharmProjects/AI2-Assignment-3/train.dat")
val testDataPath: Path = Paths.get("/Users/stanbend/PycharmProjects/AI2-Assignment-3/test.dat")
val requestsPath: Path = Paths.get("/Users/stanbend/PycharmProjects/AI2-Assignment-3/requests.dat")
val clientsPath: Path = Paths.get("/Users/stanbend/PycharmProjects/AI2-Assignment-3/clients.dat")

fun main() {

    val panel = TrainPanel()

    val trainData = Files.readAllLines(trainDataPath).parseAsFloatArray()
    val testData = Files.readAllLines(testDataPath).parseAsFloatArray()
    val requests = Files.readAllLines(requestsPath)
    val clients = Files.readAllLines(clientsPath)
    val dimensions = trainData[0].size
    var kohonen : KohonenSOM? = null

    panel.trainButton.addActionListener {
        panel.evaluateButton.isEnabled = false
        panel.trainButton.isEnabled = false
        val n = panel.nField.value as Number
        val epochs = panel.epochsField.value as Number
        kohonen = KohonenSOM(panel, n.toInt(), epochs.toInt(), trainData, testData, dimensions)
        Thread {
            println("Training ...")
            kohonen!!.train()
            println("Training finished!")
            SwingUtilities.invokeLater {
                panel.trainButton.isEnabled = true
                panel.evaluateButton.isEnabled = true
                panel.resetTestMetricLabels()
            }
        }.start()
    }

    panel.evaluateButton.isEnabled = false
    panel.evaluateButton.addActionListener {
        panel.evaluateButton.isEnabled = false
        panel.trainButton.isEnabled = false
        Thread {
            println("Testing ...")
            kohonen!!.test()
            println("Testing finished!")
            kohonen!!.setMetricLabels()
            SwingUtilities.invokeLater {
                panel.trainButton.isEnabled = true
            }
        }.start()
    }
}

/**
 * This class represents the clusters, it contains the prototype
 * and a set with the ID's (integers) of the data points that
 * are a member of that cluster.
 */
class Cluster {

    lateinit var prototype : FloatVector
    val currentMembers = HashSet<FloatVector>()
}

/**
 * @param n             the span of each dimension
 * @param epochs        the number of training epochs
 * @param trainData     the data to train the network with
 * @param testData      the data to test the network with
 * @param dimensions    the dimensions of each cluster
 */
class KohonenSOM(private val panel: TrainPanel,
                 private val n: Int,
                 private val epochs: Int,
                 private val trainData: Array<FloatVector>,
                 private val testData: Array<FloatVector>,
                 private val dimensions: Int
){

    val clusterGroups = Array(n) { Array(n) { Cluster() } }.also {
        panel.drawPanel.clusterGroups = it
    }

    // Threshold above which the corresponding url is prefetched
    val prefetchThreshold = 0.5
    val initialLearningRate = 0.8

    // Performance metrics
    var accuracy = 0.0
    var hitRate = 0.0

    /**
     * Updates the partitions in the clusters.
     *
     * @param data the data to be clustered
     */
    private fun assignClusters(data: Array<FloatVector>){

        for(member in data){
            var bestCluster: Cluster? = null
            var minDistance = Float.MAX_VALUE
            for(clusterGroup in clusterGroups){
                for(cluster in clusterGroup){
                    // Calculate the distance from the cluster prototype
                    val distance = euclidDistance.invoke(member, cluster.prototype)
                    if(distance < minDistance){
                        minDistance = distance
                        bestCluster = cluster
                    }
                }
            }
            // Add member if best cluster is not null (elvis operator)
            bestCluster?.currentMembers?.add(member)
        }
    }

    fun train(){


        // Initialize map with random vectors
        for (row in clusterGroups)
            for (cluster in row)
                cluster.prototype = FloatVector(dimensions) { Random.nextFloat() }

        panel.progressBar.value = 0

        // Repeat epoch times
        for (t in 0 until epochs){
            /*
            Calculate the square size and the learning rate,
            these decrease linearly with the number of  epochs.
             */
            val eta = initialLearningRate * (1.0 - (t/epochs.toDouble()))
            val radius = ((n/2.0) * (1.0 - (t/epochs.toDouble()))).toInt()

            // Every input vector is presented to the map
            for (vector in trainData){
                var minDistance = Float.MAX_VALUE
                var bmu = intArrayOf(0, 0)
                for((i, clusterGroup) in clusterGroups.withIndex()){
                    for((j, cluster) in clusterGroup.withIndex()){
                        val distance = euclidDistance.invoke(vector, cluster.prototype)
                        if(distance < minDistance){
                            minDistance = distance
                            bmu = intArrayOf(i, j)
                        }
                    }
                }
                // Iterate over clusters in neighbourhood of the BMU
                for(i in max(bmu[0] - radius, 0) until min(bmu[0] + radius, n)){
                    for(j in max(bmu[1] - radius, 0) until min(bmu[1] + radius, n)){
                        val neighbour = clusterGroups[i][j]
                        // Compute new prototype for the current neighbour
                        neighbour.prototype = FloatVector(dimensions) {
                            (((1.0 - eta) * neighbour.prototype[it]) + (eta * vector[it])).toFloat()
                        }
                    }
                }
            }
            panel.progressBar.value = (floor((t / epochs.toDouble()) * 100)).toInt()
            panel.drawPanel.repaint()
        }
        panel.progressBar.value = 100
        assignClusters(trainData)
    }

    fun test(){
        assignClusters(testData)

        var prefetchUrlCount = 0
        var requestsCount = 0
        var hitCount = 0

        for(clusterGroup in clusterGroups){
            for(cluster in clusterGroup){
                for(member in cluster.currentMembers){
                    for(i in 0 until dimensions){
                        val request = member[i].toInt()
                        val prefetch = if(cluster.prototype[i] > prefetchThreshold) 1 else 0
                        if(prefetch == 1)
                            prefetchUrlCount++
                        if(request == 1)
                            requestsCount++
                        if(request == 1 && prefetch == 1)
                            hitCount++
                    }
                }
            }
        }

        hitRate = hitCount / requestsCount.toDouble()
        accuracy = hitCount / prefetchUrlCount.toDouble()
    }

    fun setMetricLabels(){
        panel.accuracyLabel.text = ("Accuracy: $accuracy")
        panel.hitRateLabel.text = ("Hit-Rate: $hitRate")
    }
}

val euclidDistance = { v1: FloatVector, v2: FloatVector ->
    var sum = 0f
    for(i in 0 until v1.size){
        sum += (v1[i]-v2[i]).pow(2)
    }
    sqrt(sum)
}

fun List<String>.parseAsFloatArray(): Array<FloatVector> {
    return filter { it.isNotBlank() }
            .map { FloatVector(*it.trim().split(" ").map { entry -> entry.toFloat() }.toFloatArray()) }
            .toTypedArray()
}


class TrainPanel : JFrame(){

    val progressBar = JProgressBar()
    val nField = JFormattedTextField(NumberFormatter())
    val epochsField = JFormattedTextField(NumberFormatter())
    val trainButton = JButton("Train")
    val evaluateButton = JButton("Test")
    val accuracyLabel = JLabel()
    val hitRateLabel = JLabel()
    val drawPanel = DrawPanel()

    init {
//        size = Dimension(240, 240)
        layout = BorderLayout()

        add(BorderLayout.SOUTH, progressBar)
        add(BorderLayout.CENTER, JPanel().also {
            it.layout = BoxLayout(it, Y_AXIS)
            it.alignmentX = LEFT_ALIGNMENT
            it.add(addLabeledField("N:", nField))
            it.add(addLabeledField("Epochs:", epochsField))
            it.add(Box(X_AXIS).also { box ->
                box.add(trainButton)
                box.add(evaluateButton)
            })
            it.add(addLabelWithSpace(accuracyLabel))
            it.add(addLabelWithSpace(hitRateLabel))
        })
        add(BorderLayout.EAST, drawPanel)
        resetTestMetricLabels()
        pack()
        isResizable = false
        isVisible = true
    }

    fun resetTestMetricLabels(){
        accuracyLabel.text = "Accuracy: "
        hitRateLabel.text = "Hit-Rate: "
    }

    private fun addLabeledField(labelText: String, field: JFormattedTextField): JComponent {
        return JPanel().also { comp ->
            comp.layout = BoxLayout(comp, LINE_AXIS)
            comp.add(JLabel(labelText).also { it.labelFor = field })
            comp.add(Box.createHorizontalStrut(10))
            comp.add(field)
        }
    }
    private fun addLabelWithSpace(label: JLabel): JComponent {
        return JPanel().also { comp ->
            comp.layout = BoxLayout(comp, LINE_AXIS)
            comp.add(label)
            comp.add(Box.createHorizontalGlue())
        }
    }
}

class DrawPanel : JPanel() {

    lateinit var clusterGroups: Array<Array<Cluster>>

    init {
        preferredSize = Dimension(300, 100)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(this::clusterGroups.isInitialized) {
            var offset = 0
            for (clusterGroup in clusterGroups) {
                g.color = Color.RED
                val values = clusterGroup.map { it.prototype.components.average() };
                val radius = (values.max()!! - values.min()!!).times(100).toInt()
                g.fillOval(offset, 0, radius, radius)
                offset += radius
            }
        }
    }

}