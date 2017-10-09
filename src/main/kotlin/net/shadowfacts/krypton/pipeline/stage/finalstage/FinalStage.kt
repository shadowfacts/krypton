package net.shadowfacts.krypton.pipeline.stage.finalstage

import net.shadowfacts.krypton.Metadata

/**
 * @author shadowfacts
 */
interface FinalStage {

	fun apply(metadata: Metadata, input: String)

}