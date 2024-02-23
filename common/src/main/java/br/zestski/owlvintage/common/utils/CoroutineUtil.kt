package br.zestski.owlvintage.common.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * @author Zestski <zestski@proton.me>
 */
@Suppress("SpellCheckingInspection", "DeferredResultUnused")
object CoroutineUtil {
    @OptIn(DelicateCoroutinesApi::class)
    @JvmStatic
    fun execute(runnable: Runnable) {
        GlobalScope.async { runnable.run() }
    }
}