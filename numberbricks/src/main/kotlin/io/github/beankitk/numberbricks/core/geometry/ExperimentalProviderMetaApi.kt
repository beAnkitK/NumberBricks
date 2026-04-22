package io.github.beankitk.numberbricks.core.geometry

/**
 * Marks experimental Provider Meta APIs.
 *
 * These APIs may change or be removed at any time. Opt in with
 * `@OptIn(ExperimentalProviderMetaApi::class)`.
 */
@RequiresOptIn("The Provider Meta API is experimental and may change in future.")
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class ExperimentalProviderMetaApi
