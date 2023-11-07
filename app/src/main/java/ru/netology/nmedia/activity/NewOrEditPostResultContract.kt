package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class NewOrEditPostResultContract(input: String?) : ActivityResultContract<String?, String?>() {

    override fun createIntent(context: Context, input: String?): Intent = Intent(context, NewOrEditPostActivity()::class.java).putExtra("input", input)

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }

}