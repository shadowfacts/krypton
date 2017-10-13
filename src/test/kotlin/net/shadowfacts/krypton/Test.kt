package net.shadowfacts.krypton

import net.shadowfacts.krypton.config.Configuration
import net.shadowfacts.krypton.pipeline.selector.PipelineSelectorExtension
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStageOutput
import java.io.File

/**
 * @author shadowfacts
 */
fun main(args: Array<String>) {
	val krypton = Krypton(Configuration {
		source = File("source")
		output = File("output")

		port = 3000
	})
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
//		addStage(StageRenderEKT(null, null) {
//			"title" to "Blah"
//		}, Dependencies {
//
//		})
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