package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewOrEditPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    //private val viewModel: PostViewModel by viewModels()
    private val authModel : AuthViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth

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

        checkGoogleApiAvailability()

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
                appAuth.authState.collect {
                    invalidateOptionsMenu()
                }
            }
        }

        //TODO: REWRITE WITH DI
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println("Token from FCM: $token")
        }

        //TODO: MENU DUPLICATE IN FEEDFRAGMENT, HERE NOT SHOWS
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                //menu.clear();
                menuInflater.inflate(R.menu.main_menu, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !authModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, authModel.authenticated)
                }
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.setGroupVisible(
                    R.id.authenticated,
                    authModel.authenticated
                )
                menu.setGroupVisible(
                    R.id.unauthenticated,
                    !authModel.authenticated
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signIn -> {
                        findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_feedFragment_to_signInFragment)
                        true
                    }

                    R.id.signUp -> {
                        findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_feedFragment_to_signUpFragment)
                        true
                    }

                    R.id.signOut -> {
                        MaterialAlertDialogBuilder(this@AppActivity)
                            .setTitle("Signing Out")
                            .setMessage("Are you want to from your account?")
                            .setIcon(R.drawable.baseline_logout_48)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Sign Out", ) { _,_ ->
                                appAuth.removeAuth()
                            }
                            .show()
                        true
                    }

                    else -> {false}
                }
        })//addMenuProvider

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

    private fun checkGoogleApiAvailability() {
        //TODO: REWRITE WITH DI
        with(GoogleApiAvailability()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }
}
