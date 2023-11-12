package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.ActivityAppBinding


class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.action != Intent.ACTION_SEND) return

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        
        if (text.isNullOrBlank()) {
            Snackbar.make(binding.root,
                getString(R.string.empty_text_error), Snackbar.LENGTH_SHORT).show()
            return
        }

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.empty_text_error,
                    LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            }

            it.removeExtra(Intent.EXTRA_TEXT)

            //support manually add from https://developer.android.com/guide/navigation/get-started
            //to having a NavController in activity (frag.men-r of avt-ty)
            val navHostFragment =
                supportFragmentManager.findFragmentById(androidx.navigation.fragment.R.id.nav_host_fragment_container) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(R.id.action_feedFragment_to_newOrEditPostFragment, Bundle().apply{
                    textArg = text
                })
        }

    }
}
