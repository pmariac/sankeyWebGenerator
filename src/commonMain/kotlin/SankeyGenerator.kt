package io.data2viz.demo

import io.data2viz.color.Colors
import io.data2viz.format.*
import io.data2viz.geom.Point
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.sankey.SankeyAlignment
import io.data2viz.sankey.SankeyLayout
import io.data2viz.sankey.sankeyLinkHorizontal
import io.data2viz.viz.*

// Let a small space at the left and right of the chart to display labels
const val labelSpace = 180.0

// extent margins
const val margins = 20.0

// Formatter for the amount values (decimal, GB currency prefix...) use another formatter to add "Bn" for billions
val numberFormatter = Locales.Companion.en_GB.formatter(Type.DECIMAL, precision = 3, symbol = Symbol.CURRENCY)
val amountFormatter = { amount: Double -> "${numberFormatter(amount)}Bn" }

// Color parameters, change this to adjust display
const val totalColorAngle = 250.0						// 0 - 360, the more, the more colors
const val startColorAngle = 0.0							// 0 - 360, use this to choose the "starting" color
const val chroma = 70.0									// The more, the flashier (0 - 230)
val luminance = 80.pct									// The more, the lighter

fun sankeyGenerator(vizWidth: Double, vizHeight: Double): Viz {

	// Read and build the incomes list
	val allIncomes = mutableSetOf<String>()
	val incomeStrings = incomeString.split('\n').filterNot { it.isBlank() }.map {
		val p = it.split(',')
		val from = p[0].clean()
		val to = p[1].clean()
		val value = p[2].clean().toDouble()
		allIncomes.add(from)
		allIncomes.add(to)
		StringIncome(from, to, value)
	}
	val incomes = allIncomes.mapIndexed { index, name ->
		val flows = incomeStrings.filter { it.from == name }.map {
			IncomeFlow(index, allIncomes.indexOf(it.to), it.amount)
		}
		Income(index, name, flows)
	}

	// Compute the Sankey layout
	val sankeyLayout = SankeyLayout<Income>()
	sankeyLayout.extent(labelSpace, vizWidth - labelSpace, margins, vizHeight - margins)
	sankeyLayout.nodePadding = 45.0
	sankeyLayout.iterations = 10
	sankeyLayout.align = SankeyAlignment.CENTER
	sankeyLayout.sankey(incomes) { from, to ->
		from.flows.firstOrNull { it.toIndex == to.index }?.amount
	}

	// build the color palette
	val layerCount = sankeyLayout.nodes.maxOf { it.layer } + 1
	val layerAngleIncrement = totalColorAngle / layerCount
	val layerColors = (0 until layerCount).map { layer ->
		val layerAngle = (layer * layerAngleIncrement) + startColorAngle
		Colors.hcl(layerAngle.deg, chroma, luminance)
	}

	// Build the visualization
	val viz = viz {
		width = vizWidth
		height = vizHeight

		// just a white background
		rect {
			x = .0
			y = .0
			width = vizWidth
			height = vizHeight
			fill = Colors.Web.white
		}

		// Draw links
		sankeyLayout.links.forEach { link ->
			// build gradient using the color from the source node and the color from the target node
			val fromSource = Point(link.source.x1, .0)
			val toTarget = Point(link.target.x0, .0)
			val sourceColor = layerColors[link.source.layer]
			val targetColor = layerColors[link.target.layer]
			val gradient = Colors.Gradient.linear(fromSource, toTarget)
				.withColor(sourceColor)
				.andColor(targetColor)
				.withAlpha(70.pct)

			val linkPath = path {
				strokeColor = gradient
				strokeWidth = link.width
			}
			sankeyLinkHorizontal.link(link, linkPath)
		}

		// Draw nodes and labels
		sankeyLayout.nodes.forEach { node ->
			val nodeWidth = node.x1 - node.x0
			val nodeHeight = node.y1 - node.y0
			val nodeColor = layerColors[node.layer]
			rect {
				x = node.x0
				y = node.y0
				width = nodeWidth
				height = nodeHeight
				fill = nodeColor
				strokeColor = null
			}
			text {
				textContent = node.data.name
				fontWeight = FontWeight.BOLD
				x = if (node.depth == 0) node.x0 - 5.0 else node.x1 + 5.0
				y = node.y0 + (nodeHeight / 2.0) - 6.0
				vAlign = TextVAlign.MIDDLE
				hAlign = if (node.depth == 0) TextHAlign.RIGHT else TextHAlign.LEFT
				fontSize = 12.0
				textColor = nodeColor.darken(3.0)
			}
			text {
				textContent = amountFormatter(node.value)
				fontWeight = FontWeight.BOLD
				x = if (node.depth == 0) node.x0 - 5.0 else node.x1 + 5.0
				y = node.y0 + (nodeHeight / 2.0) + 6.0
				vAlign = TextVAlign.MIDDLE
				hAlign = if (node.depth == 0) TextHAlign.RIGHT else TextHAlign.LEFT
				fontSize = 12.0
				textColor = nodeColor.darken(3.0)
			}
		}

	}
	return viz
}
