package com.atlasstudio.barcodescanner.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.atlasstudio.barcodescanner.R
import com.atlasstudio.barcodescanner.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        //removing the shadow from the action bar
        supportActionBar?.elevation = 0f

        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        /*binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return /*when (item.itemId) {
            R.id.action_one_back -> true
            else ->*/ super.onOptionsItemSelected(item)
        /*}*/
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val action = event!!.action
        val keyCode = event.keyCode
        return when (keyCode) {
            KeyEvent.KEYCODE_ESCAPE,
            KeyEvent.KEYCODE_CLEAR,
            KeyEvent.KEYCODE_DEL,
            KeyEvent.KEYCODE_FORWARD_DEL -> {
                if (action == KeyEvent.ACTION_UP) {
                    sendClearSumBroadcast()
                }
                true
            }
            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9,
            KeyEvent.KEYCODE_NUMPAD_0,
            KeyEvent.KEYCODE_NUMPAD_1,
            KeyEvent.KEYCODE_NUMPAD_2,
            KeyEvent.KEYCODE_NUMPAD_3,
            KeyEvent.KEYCODE_NUMPAD_4,
            KeyEvent.KEYCODE_NUMPAD_5,
            KeyEvent.KEYCODE_NUMPAD_6,
            KeyEvent.KEYCODE_NUMPAD_7,
            KeyEvent.KEYCODE_NUMPAD_8,
            KeyEvent.KEYCODE_NUMPAD_9,
            KeyEvent.KEYCODE_UNKNOWN -> {
                super.dispatchKeyEvent(event)
            }
            else -> true
        }
    }

    /*private fun sendAddBroadcast() {
        val intent = Intent("add-to-sum")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }*/

    private fun sendClearSumBroadcast() {
        val intent = Intent("clear-sum")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /*private fun sendClearCurrentBroadcast() {
        val intent = Intent("clear-number")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendNumberBroadcast(singleNumber: Int) {
        var intent = Intent("concat-to-number")
        intent.putExtra("singleNumber", singleNumber)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun sendDecimalsBroadcast() {
        val intent = Intent("decimal-separator")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }*/
}