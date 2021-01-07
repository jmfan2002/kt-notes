package com.example.notes

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.notes.databinding.EditNoteBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class EditNote : Fragment() {
    private var _binding: EditNoteBinding? = null
    private val binding get() = _binding!!
    private var pos = 0
    private lateinit var prefs: SharedPreferences
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            pos = it.getInt("position")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = EditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // load note
        prefs = requireContext().getSharedPreferences("stored_notes", MODE_PRIVATE)
        val type = object: TypeToken<List<Note>>(){}.type
        val json = prefs.getString("notes", null)
        val notesList: MutableList<Note> = Gson().fromJson(json, type)
        note = notesList[pos]

        val title: EditText = binding.editTitle
        val body: EditText = binding.editBody

        // set title and body text
        title.setText(note.title)
        body.setText(note.body)

        // add text watcher
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                // replace note
                val newNote = Note(binding.editTitle.text.toString(), binding.editBody.text.toString())
                notesList[pos] = newNote

                // store notes
                val prefsEditor = prefs.edit()
                val newJson = Gson().toJson(notesList)
                prefsEditor.putString("notes", newJson)
                prefsEditor.apply()
            }
        }
        title.addTextChangedListener(textWatcher)
        body.addTextChangedListener(textWatcher)
    }
}