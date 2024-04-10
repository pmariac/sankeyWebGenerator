import io.data2viz.demo.sankeyGenerator
import io.data2viz.viz.bindRendererOnNewCanvas

// Visualization size
const val vizWidth = 1400.0
const val vizHeight = 700.0

fun main() {
	val viz = sankeyGenerator(vizWidth, vizHeight)
	viz.bindRendererOnNewCanvas()
}
