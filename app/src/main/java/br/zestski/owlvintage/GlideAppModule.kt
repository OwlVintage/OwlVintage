package br.zestski.owlvintage

import android.annotation.SuppressLint
import android.content.Context

import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Custom Glide module for configuring Glide options.
 *
 * @author Zestski
 */
@GlideModule
class GlideAppModule : AppGlideModule() {

    /**
     * Applies custom options to the Glide builder.
     *
     * @param context The application context.
     * @param builder The Glide builder to apply options to.
     */
    @SuppressLint("CheckResult")
    override fun applyOptions(
        context: Context,
        builder: GlideBuilder
    ) {
        super.applyOptions(context, builder)

        builder.apply {
            setDefaultRequestOptions(
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            )
        }
    }
}