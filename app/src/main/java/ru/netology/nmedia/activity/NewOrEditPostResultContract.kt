package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class PostInfo (
    val id: Long?,
    val content: String?
)

class NewOrEditPostResultContract(input: PostInfo?) : ActivityResultContract<PostInfo?, String?>() {

    override fun createIntent(context: Context, input: PostInfo?): Intent = Intent(context, NewOrEditPostActivity()::class.java).apply {
        putExtra("content", input?.content)
        putExtra("id", input?.id)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }

}