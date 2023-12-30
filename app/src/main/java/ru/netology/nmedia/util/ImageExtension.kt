package ru.netology.nmedia.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.R

fun ImageView.load(
    url: String,
    timeOut: Int = 30_000,
    placeholderIndex: Int = R.drawable.baseline_downloading_48,
    errorIndex: Int = R.drawable.baseline_error_outline_48,
    options: RequestOptions = RequestOptions()
) {
    Glide.with(this)
        .load(url)
        .timeout(timeOut)
        .placeholder(placeholderIndex)
        .error(errorIndex)
        .apply(options)
        .into(this)
}