package com.example.notes

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        prefs = this.getSharedPreferences("stored_notes", MODE_PRIVATE)

        // sets up nav controller
        val navHostElement = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostElement.navController

        setupActionBarWithNavController(navController)

        // sets up FAB
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            makeNote()
        }

    }

    private fun makeNote() {
        lateinit var notesList: MutableList<Note>
        val pos: Int
        val prefsEditor = prefs.edit()

        // gets notes list
        val type = object: TypeToken<List<Note>>(){}.type
        var json = prefs.getString("notes", null)

        // checks if notes list is empty
        if (json != null) {
            notesList = Gson().fromJson(json,type)
            notesList.add(Note("", ""))
            pos = notesList.size - 1
        } else {
            notesList = mutableListOf(Note("", ""))
            pos = 0
        }

        // stores notes list in shared prefs
        json = Gson().toJson(notesList)
        prefsEditor.putString("notes", json)
        prefsEditor.apply()

        // navigates to edit note screen
        val action = NotesListDirections.actionFirstFragmentToSecondFragment(position = pos)
        navController.navigate(action)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}