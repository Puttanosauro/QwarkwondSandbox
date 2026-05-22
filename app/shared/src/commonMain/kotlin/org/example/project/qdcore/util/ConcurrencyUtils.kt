package org.example.project.qdcore.util

//actually use for compatibility, as of right now it doesn't do shit:
//we do not have any form of parallel mapping
//but if i dont add this line the code screams at my face
//so given that i dont want to remap half of the functions, i just keep a useless variable
private const val DEFAULT_MIN_ITEMS_FOR_PARALLELISM = 4

/**
 * WASM/KMP Port: True multithreading via Java streams is not supported in the browser.
 * This now falls back to a standard sequential map for all lists.
 */
fun <T, R> List<T>.mapParallel(
    minItems: Int = DEFAULT_MIN_ITEMS_FOR_PARALLELISM,
    transform: (T) -> R,
): List<R> {
    // Just ignore the parallel optimization and run a normal map
    return this.map(transform)
}

/* /TODO !IMPORTANT
    ideally the following would be the func to use: because it allows us to support parallelism server-side by setting up promises

    NOTE TO SELF: cause im stupid and I wont remember if I dont write it here:
    this works close enough to like, an abstract class so if i use this I NEED TO THEN IMPLEMENT THE 2 DIFFERENT VERSION
*/

//package org.example.project.qdcore.util
//
//expect fun <T, R> List<T>.mapParallel(
//    minItems: Int = 4,
//    transform: (T) -> R
//): List<R>