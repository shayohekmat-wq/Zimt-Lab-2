package de.zimt.eimerliste.ui

internal data class PaperRollPose(val scaleY: Float, val translationY: Float)

/** Top of the paper travels upwards while its lower edge remains near the composer.
 * Reversing progress reproduces precisely the same path, without a directional jump.
 */
internal fun paperRollPose(progress: Float, paperHeight: Float, paperTop: Float,
                           containerHeight: Float, collapsedHeight: Float, bottomInset: Float): PaperRollPose {
    val t = progress.coerceIn(0f, 1f)
    val scale = (collapsedHeight / paperHeight.coerceAtLeast(1f)).coerceIn(.08f, 1f)
    val displacement = (containerHeight - paperTop - collapsedHeight - bottomInset).coerceAtLeast(0f)
    return PaperRollPose(scale + (1f - scale) * t, displacement * (1f - t))
}
