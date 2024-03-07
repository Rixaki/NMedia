package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel


class AppActivity : AppCompatActivity() {

    val viewModel by viewModels<AuthViewModel>()

    //TODO: THIS ADDING OF UNDO BUTTON (IN START_SIDE) HIDE MENU IN END-SIDE
    override fun onStart() {
        super.onStart()
        //TODO: add SET click listener
        findNavController(R.id.my_nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            supportActionBar?.setDisplayHomeAsUpEnabled(destination.id == R.id.newOrEditPostFragment)//undo action
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationsPermission()

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (text.isNullOrBlank()) {
            Snackbar.make(
                binding.root,
                getString(R.string.empty_text_error), Snackbar.LENGTH_SHORT
            ).show()
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
                supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(
                R.id.action_feedFragment_to_newOrEditPostFragment,
                Bundle().apply {
                    textArg = text
                })
        }//intent

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.data.collect {
                    invalidateOptionsMenu()
                }
            }
        }

        /*
        //TODO: MENU DUPLICATE IN FEEDFRAGMENT, HERE NOT SHOWS
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.main_menu, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                }
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.setGroupVisible(
                    R.id.authenticated,
                    viewModel.authenticated
                )
                menu.setGroupVisible(
                    R.id.unauthenticated,
                    !viewModel.authenticated
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signIn -> {
                        AppAuth.getInstance().setAuth(5, "x-token")
                        true
                    }

                    R.id.signUp -> {
                        AppAuth.getInstance().setAuth(5, "x-token")
                        true
                    }

                    R.id.signOut -> {
                        MaterialAlertDialogBuilder(this@AppActivity)
                            .setTitle("Signing Out")
                            .setMessage("Are you want to from your account?")
                            .setIcon(R.drawable.baseline_logout_48)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Sign Out", ) { _,_ ->
                                AppAuth.getInstance().removeAuth()
                            }
                            .show()
                        true
                    }

                    else -> {false}
                }
        })//addMenuProvider

         */
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
}
