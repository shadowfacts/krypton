package net.shadowfacts.krypton

import net.shadowfacts.krypton.pipeline.selector.PipelineSelectorExtension
import net.shadowfacts.krypton.pipeline.stage.StageRenderEKT
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStageOutput
import net.shadowfacts.krypton.util.dependencies.Dependencies
import java.io.File

/**
 * @author shadowfacts
 */
fun main(args: Array<String>) {
	val krypton = Krypton(DefaultConfiguration(File("source"), File("output")))
	krypton.createPipeline {
		selector = PipelineSelectorExtension("html")
//		addStage(object: Stage() {
//			override val id = "stripWhitespace"
//
//			override fun generate(metadata: Page, input: String) = input.filter { !it.isWhitespace() }
//
//		}, Dependencies {
//			after += "ekt"
//		})
		addStage(StageRenderEKT(null, null) {
			"title" to "Blah"
		}, Dependencies {

		})
//		addStage(object: Stage() {
//			override val id = "test"
//			override fun generate(metadata: Page, input: String) = input
//		}, Dependencies {
//			after += arrayOf("ekt", "stripWhitespace")
//		})
		final = FinalStageOutput()
	}
	krypton.serve()
}