## Sankey Generator using Data2viz

1. Edit the [incomes.kt](src/commonMain/kotlin/incomes.kt) file to add your data in the format
	- "Source node name, target node name, flow value\n"
2. Edit the values in [sankeyGenerator.kt](src/commonMain/kotlin/sankeyGenerator.kt) to customize your chart:
    - vizWidth / vizHeight
    - labelSpace / margins
    - formatter
    - totalColorAngle / startColorAngle / chroma / luminance
3. Run `./gradlew jsBrowserDevelopmentRun --continuous`
