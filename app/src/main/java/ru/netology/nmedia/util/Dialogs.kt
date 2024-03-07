package ru.netology.nmedia.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import ru.netology.nmedia.auth.AppAuth

fun SignOutDialog(context: Context = AppActivity()) {
    MaterialAlertDialogBuilder(context)
    .setTitle("Signing Out")
    .setMessage("Do you want to from your account?")
    .setIcon(R.drawable.baseline_logout_48)
    .setNegativeButton("Cancel", null)
    .setPositiveButton("Sign Out") { _,_ ->
        AppAuth.getInstance().removeAuth()
    }
    .show()
}

fun SignInDialog(context: Context = AppActivity()) {
    MaterialAlertDialogBuilder(context)
        .setTitle("Action not yet available")
        .setMessage("Do you want to sign in? ... for like/add posts etc.")
        .setIcon(R.drawable.baseline_logout_48)
        .setNegativeButton("Cancel", null)
        .setPositiveButton("Sign In") { _,_ ->
            AppAuth.getInstance().removeAuth()
        }
        .show()
}