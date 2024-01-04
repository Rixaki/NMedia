package ru.netology.nmedia.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ru.netology.nmedia.R

fun ImageView.load(
    url: String,
    timeOut: Int = 30_000,
    placeholderIndex: Int = R.drawable.baseline_downloading_48,
    errorIndex: Int = R.drawable.baseline_error_outline_48,
    options: RequestOptions = RequestOptions(),
    toFullWidth: Boolean = false
) {
    Glide.with(this)
        .load(url)
        .timeout(timeOut)
        .placeholder(placeholderIndex)
        .error(errorIndex)
        .apply(options)
        .apply {
            if (toFullWidth)
                this.into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        this@load.setImageDrawable(resource)
                        val layoutParams = this@load.layoutParams
                        val widthOriginal = resource.intrinsicWidth
                        val heightOriginal = resource.intrinsicHeight

                        val displayMetrics = context.resources.displayMetrics
                        val screenWidth = displayMetrics.widthPixels
                        layoutParams.width = screenWidth

                        val calculatedHeight =
                            (screenWidth.toFloat() / widthOriginal.toFloat() * heightOriginal).toInt()
                        layoutParams.height = calculatedHeight
                        this@load.layoutParams = layoutParams
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        this@load.setImageDrawable(placeholder)
                    }
                })
        }
        .into(this)
}