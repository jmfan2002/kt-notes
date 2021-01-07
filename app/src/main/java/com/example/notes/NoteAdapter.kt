package com.example.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class NoteAdapter(
        private val fragment: Fragment,
        private val notes: List<Note>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), Filterable {

    val searchableNotes = notes.toMutableList()
    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteCard: CardView = view.findViewById(R.id.note_card)
        val title: TextView = view.findViewById(R.id.note_item_title)
        val body: TextView = view.findViewById(R.id.note_item_body)

        fun bind(value: Int, isActivated: Boolean = false) {

        }

        // for MyDetailsLookup
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object: ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): Long? = itemId
            }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_list_item, parent, false)

        return NoteViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = searchableNotes[position]
        holder.title.text = note.title
        holder.body.text = note.body

        // swaps fragment upon click
        holder.noteCard.setOnClickListener {
            val action = NotesListDirections.actionFirstFragmentToSecondFragment(position = position)
            findNavController(fragment).navigate(action)
        }

        holder.noteCard.isActivated = tracker!!.isSelected(position.toLong())
    }

    override fun getItemCount() = searchableNotes.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(queryCS: CharSequence?): FilterResults {
                val filteredList: MutableList<Note> = mutableListOf()

                // filter logic
                if (queryCS.isNullOrBlank()) {
                    filteredList.addAll(notes)
                } else {
                    val query = queryCS.toString().toLowerCase(Locale.getDefault()).trim()
                    for (note in notes) {
                        if (note.title.toLowerCase(Locale.getDefault()).contains(query) ||
                            note.body.toLowerCase(Locale.getDefault()).contains(query)) {
                            filteredList.add(note)
                        }
                    }
                }

                // return results
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                searchableNotes.clear()
                searchableNotes.addAll(results.values as List<Note>)
                notifyDataSetChanged()
            }

        }
    }
}