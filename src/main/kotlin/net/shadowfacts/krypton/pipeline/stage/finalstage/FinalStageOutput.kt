package net.shadowfacts.krypton.pipeline.stage.finalstage

import net.shadowfacts.krypton.Krypton
import net.shadowfacts.krypton.Metadata

/**
 * @author shadowfacts
 */
class FinalStageOutput(val krypton: Krypton): FinalStage {

	override fun apply(metadata: Metadata, input: String) {
		metadata.output.apply {
			parentFile.mkdirs()
			writeText(input, Charsets.UTF_8)
		}
	}

}