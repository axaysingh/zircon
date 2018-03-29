package org.codetome.zircon.api.animation

/**
 * Represents the states an [Animation] can be in.
 */
enum class AnimationState {

    /**
     * The `Animation` is in progress and will finish
     * some time in the future.
     */
    IN_PROGRESS,
    /**
     * The `Animation` is infinite, it will not
     * finish.
     */
    INFINITE,
    /**
     * The `Animation` is finished.
     */
    FINISHED
}
