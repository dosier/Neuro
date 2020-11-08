package neuro.experiments.mdp

import org.jetbrains.numkt.LibraryLoader
import org.jetbrains.numkt.core.ExperimentalNumkt
import org.jetbrains.numkt.core.KtNDArray
import org.jetbrains.numkt.linalg.Linalg
import org.jetbrains.numkt.math.plus
import org.jetbrains.numkt.zeros
import kotlin.math.abs

@ExperimentalNumkt
fun main() {
    LibraryLoader.setPythonConfig("/Users/stanbend/opt/anaconda3/envs/Kotlin")

    val map = makeRNProblem()
//    map.valueIteration()
    map.policyIteration()
    map.printMaze(PrintType.VALUES)

//    map.calculateUtilitiesLinear()
}

/**
 * Creates the maze defined in Russell & Norvig.
 * Utilizes functions defined in the problem_utils module.
 */
fun makeRNProblem() : Map {

    val goals = mapOf(Coordinates(3, 0) to 1.0, Coordinates(3, 1) to -1.0)
    val walls = listOf(Coordinates(1, 1))

    val actions = listOf(Action.LEFT, Action.RIGHT, Action.UP, Action.DOWN)
    val cols = 4
    val rows = 3

    fun filter(old: Coordinates, new: Coordinates): Coordinates{
        return if(new.x < 0 || new.y < 0 || new.x > cols - 1 || new.y > rows - 1 || walls.contains(new))
            old
        else
            new
    }

    val m = Map(rows, cols)
    for(i in 0 until cols){
        for(j in 0 until rows){
            val id = j * cols + i
            val coordinates = Coordinates(i, j)
            m.states[coordinates] = State(id, -0.04, coordinates, actions)
        }
    }

    for(goal in goals){
        val state = m.states[goal.key]!!
        state.isGoal = true
        state.utility = goal.value
        state.reward = goal.value
    }

    for (wall in walls){
        val state = m.states[wall]!!
        state.isGoal = true // ?
        state.isWall = true
        state.utility = 0.0
        state.reward = 0.0
    }

    for (entry in m.states){
        val coordinates = entry.key
        val state = entry.value
        for(a in actions){
            state.transitions[a] = listOf(
                    0.8 to m.states[filter(coordinates, a.getSuccessor(coordinates))]!!,
                    0.1 to m.states[filter(coordinates, a.turnLeft().getSuccessor(coordinates))]!!,
                    0.1 to m.states[filter(coordinates, a.turnRight().getSuccessor(coordinates))]!!)
        }
    }
    return m
}

enum class Action(val representation: String) {
    LEFT("<<"),
    RIGHT(">>"),
    UP("/\\"),
    DOWN("\\/");

    fun getSuccessor(coordinates: Coordinates) : Coordinates {
        return when(this){
            LEFT -> Coordinates(coordinates.x-1, coordinates.y)
            RIGHT -> Coordinates(coordinates.x+1, coordinates.y)
            UP -> Coordinates(coordinates.x, coordinates.y-1)
            DOWN -> Coordinates(coordinates.x, coordinates.y+1)
        }
    }

    fun turnLeft() : Action {
        return when(this) {
            LEFT -> DOWN
            RIGHT -> UP
            UP -> LEFT
            DOWN -> RIGHT
        }
    }
    fun turnRight() : Action {
        return when(this) {
            RIGHT -> DOWN
            LEFT -> UP
            UP -> RIGHT
            DOWN -> LEFT
        }
    }
}

enum class PrintType {
    ACTIONS,
    VALUES
}

class Map(private val nRows: Int,
          private val nCols: Int,
          private val stopCriteria: Double = 0.001,
          private val gamma: Double = 0.8) {

    val states = HashMap<Coordinates, State>()

    fun valueIteration(){

        // Initialize utilities to 0
        for(s in states.values)
            if(!s.isGoal)
                s.utility = 0.0

        var completed: Boolean
        do {
            completed = true

            for(s in states.values){

                if(s.isGoal)
                    continue

                var maxUtility = Double.MIN_VALUE
                for(a in s.actions){
                    val expectedUtility = s.reward + (gamma * s.computeExpectedUtility(a))
                    if(expectedUtility > maxUtility)
                        maxUtility = expectedUtility
                }

                if(abs(maxUtility - s.utility) > stopCriteria)
                    completed = false

                s.utility = maxUtility
            }
        }while (!completed)
    }

    @ExperimentalNumkt
    fun policyIteration() {
        // Pick initial policy randomly
        for(s in states.values)
            if(!s.isGoal)
                s.policy = s.actions.random()

        var policyChanged: Boolean
        do {
            policyChanged = false
            calculateUtilitiesLinear()
            for(s in states.values){
                if(s.isGoal)
                    continue
                var maxUtility = Double.MIN_VALUE
                var bestAction: Action? = null
                for(a in s.actions){
                    val successor = states[a.getSuccessor(s.coordinates)]?:continue
                    val expectedUtility = successor.utility
                    if(expectedUtility > maxUtility){
                        maxUtility = expectedUtility
                        bestAction = a
                    }
                }
                if(bestAction != s.policy){
                    s.policy = bestAction
                    policyChanged = true
                }
            }
        } while (policyChanged)
    }

    @ExperimentalNumkt
    fun calculateUtilitiesLinear(){
        val nStates = states.size
        val coefficients = zeros<Double>(nStates, nStates)
        val ordinate = zeros<Double>(nStates)

        for(s in states.values){
            val row = s.id
            ordinate[row] = s.reward
            coefficients[row][row] += (1.0)
            if(!s.isGoal){
                val probableStates = s.transitions[s.policy]?:continue
                for(pair in probableStates){
                    val probability = pair.first
                    val state = pair.second
                    val col = state.id
                    coefficients[row][col] += (-gamma * probability)
                }
            }
        }

        val solved = Linalg.lstsq(coefficients, ordinate)
        val solution = solved[0] as KtNDArray<Double>
        for(s in states.values){
            if(!s.isGoal){
                s.utility = solution[s.id].scalar?:0.0
            }
        }
    }

    fun printMaze(printType: PrintType){
        var toPrint = ":"
        for(c in 0 until nCols)
            toPrint += "--------:"
        toPrint += '\n'
        for(r in 0 until nRows){
            toPrint += "|"
            for(c in 0 until nCols){
                val coordinates = Coordinates(c, r)
                val state = states[coordinates]!!
                if(state.isWall){
                    toPrint += "        "
                } else {
                    toPrint += ' '
                    if(state.isGoal)
                        toPrint += "  "+(if(state.utility > 0.0) "+" else "")+ "${state.utility.toInt()}  "
                    else {
                        when(printType){
                            PrintType.VALUES -> toPrint += " %.${3}f".format(state.utility)
                            PrintType.ACTIONS -> {
                                toPrint += "  "
                                toPrint += state.findBestAction().representation
                                toPrint += "  "
                            }
                        }
                    }
                    toPrint += ' '
                }
                toPrint += "|"
            }
            toPrint += '\n'
            toPrint += ":"
            for(c in 0 until nCols)
                toPrint += "--------:"
            toPrint += '\n'
        }
        print(toPrint)
    }
}

class State(val id: Int, var reward: Double, val coordinates: Coordinates, val actions: List<Action>) {

    var utility = 0.0

    /**
     * [Actions][Action] mapped to a list of probability/[state][State] pairs.
     */
    val transitions = HashMap<Action, List<Pair<Double, State>>>()

    var policy : Action? = null
    var isGoal = false
    var isWall = false

    fun computeExpectedUtility(action: Action) : Double {
        return transitions[action]
                ?.sumByDouble { it.first * it.second.utility }
                ?:0.0
    }

    fun findBestAction() : Action {
        return Action.values().maxBy { computeExpectedUtility(it) }!!
    }
}

data class Coordinates(val x: Int, val y: Int)
