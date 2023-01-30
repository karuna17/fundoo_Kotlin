package com.example.fundoonotes.util

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoonotes.R
import com.example.fundoonotes.listeners.DeleteListener
import com.example.fundoonotes.model.Notes
import com.example.fundoonotes.model.NotesOperation
import com.example.fundoonotes.view.AddNotes

class NoteAdapter(private val notesList: List<Notes>, private val deleteListener: DeleteListener) :
    RecyclerView.Adapter<NoteViewHolder>(),
    Filterable {

    companion object {
        private const val TAG = "NoteAdapter"
    }

    var allNotes = mutableListOf<Notes>().apply {
        Log.d("NoteAdapter", "setNotes: ${notesList.size}")
        addAll(notesList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val displayView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_view, parent, false)
        return NoteViewHolder(displayView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val list = allNotes[position]
        holder.bindNotes(list)
        holder.view.setOnClickListener {
            val updateNote = AddNotes(NotesOperation.UPDATE)
            val args = Bundle()
            Log.d(TAG, "onBindViewHolder: ${list.noteId}")
            args.putString("title", list.tittle)
            args.putString("content", list.content)
            args.putString("noteId", list.noteId)
            args.putString("userId", list.userId)
            args.putString("lastModified", list.lastModified)
            updateNote.arguments = args
            val activity = it!!.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, updateNote).addToBackStack(null).commit()
        }

        holder.view.findViewById<ImageView>(R.id.menuIcon).setOnClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.menu.add("Delete").setOnMenuItemClickListener {
                deleteListener.noteDeleted(true, list)
                return@setOnMenuItemClickListener false
            }
            popupMenu.menu.add("Archive").setOnMenuItemClickListener {
                allNotes.removeAt(position)
                return@setOnMenuItemClickListener false
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Notes>()
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(notesList)
            } else {
                for (notes in notesList) {
                    if (notes.tittle.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(notes)
                    }
                }
            }
            val filterResult = FilterResults()
            filterResult.values = filteredList
            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            allNotes.clear()
            allNotes.addAll(results?.values as Collection<Notes>)
            notifyDataSetChanged()
        }
    }
}

class NoteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val tittle: TextView = view.findViewById(R.id.note_title)
    private val content: TextView = view.findViewById(R.id.note_content)

    fun bindNotes(notes: Notes) {
        tittle.text = notes.tittle
        content.text = notes.content
    }
}

