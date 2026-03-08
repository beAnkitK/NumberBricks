package io.github.beankitk.numberbricks.core.geometry

class Meta<P : GeometryProvider<*>, M> internal constructor(
    internal val defaultFactory: () -> M
) {
    val default: M by lazy { defaultFactory() }
}

abstract class MetaGroup<P : GeometryProvider<*>> {

    protected fun <M> defineMeta(
        defaultFactory: () -> M
    ): Meta<P, M> = Meta(defaultFactory)
}