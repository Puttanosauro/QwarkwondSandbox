package org.example.project.qdcore.pipeline.error

import org.example.project.qdcore.function.Function
import org.example.project.qdcore.log.Log

/**
 * Pipeline error handler that rethrows the incoming error and ignores the additional custom action.
 * In a regular pipeline, this will cause the program to exit (see `QuarkdownCli` from the `cli` module).
 */
class StrictPipelineErrorHandler : PipelineErrorHandler {
    override fun <T> handle(
        error: Throwable,
        sourceFunction: Function<*>?,
        action: () -> T,
    ): Nothing {
        val errorCode = (error as? PipelineException)?.code ?: "Unknown - ${error::class.simpleName}"
        Log.error("An error occurred while in strict mode (error code $errorCode)")
        if (sourceFunction != null) {
            Log.error("Originated from function: ${sourceFunction.name}")
        }
        throw error
    }
}
