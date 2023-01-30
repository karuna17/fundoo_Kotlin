package com.example.fundoonotes.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fundoonotes.R
import com.example.fundoonotes.database.DBHelper
import com.example.fundoonotes.model.NoteService
import com.example.fundoonotes.model.Notes
import com.example.fundoonotes.model.NotesOperation
import com.example.fundoonotes.model.UserAuthService
import com.example.fundoonotes.util.ViewState
import com.example.fundoonotes.viewmodel.AddNoteViewModel
import com.example.fundoonotes.viewmodel.AddNoteViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddNotes(private val operation: NotesOperation) : Fragment() {

    companion object {
        private const val TAG = "AddNotes"
    }

    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var saveButton: FloatingActionButton
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var addNoteViewModel: AddNoteViewModel
    private lateinit var noteId: String
    private lateinit var userId: String
    private lateinit var lastModifiedTime: String
    private lateinit var toolbar: MaterialToolbar
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_add_notes, container, false)
        noteTitle = view.findViewById(R.id.note_title)
        noteContent = view.findViewById(R.id.note_content)
        saveButton = view.findViewById(R.id.saveNotesFab)
        toolbar = view.findViewById(R.id.addNoteToolbar)
        setHasOptionsMenu(true)

        addNoteViewModel = ViewModelProvider(
            this, AddNoteViewModelFactory(NoteService(DBHelper(requireContext())))
        ).get(AddNoteViewModel::class.java)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNoteViewModel.addNoteStatus.observe(viewLifecycleOwner, {
            if (it is ViewState.Success) {
                sharedViewModel.setAddNoteStatus(it)
                activity?.supportFragmentManager?.popBackStack()
            }
        })

        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            noteTitle.setText(bundle.getString("title"))
            noteContent.setText(bundle.getString("content"))
            noteId = bundle.getString("noteId").toString()
            Log.d(TAG, "onViewCreated: noteId: $noteId")
            userId = bundle.getString("userId").toString()
            lastModifiedTime = bundle.getString("lastModified").toString()
        }

        addNoteViewModel.updateNoteStatus.observe(viewLifecycleOwner, Observer {
            if (it is ViewState.Success) {
                sharedViewModel.setUpdateNoteStatus(it)
                activity?.supportFragmentManager?.popBackStack()
            }
        })
        saveNotes()
    }

    @SuppressLint("NewApi")
    private fun saveNotes() {
        saveButton.setOnClickListener {
            var mtitle: String = noteTitle.text.toString()
            var mcontent: String = noteContent.text.toString()
            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss")
            val formatted = now.format(formatter)
            var note = Notes(tittle = mtitle, content = mcontent, lastModified = formatted)
            when (operation) {
                NotesOperation.ADD -> {
                    addNoteViewModel.saveNotesToFirestore(note, requireContext())
                }
                NotesOperation.UPDATE -> {
                    var note = Notes(
                        tittle = mtitle,
                        content = mcontent,
                        lastModified = formatted,
                        noteId = noteId,
                        userId = userId
                    )
                    addNoteViewModel.updateNotesInFirestore(note)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.add_note_toolbar_menu, menu)
        addNoteToolbar()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addReminder) {
            Toast.makeText(requireContext(), "Reminder select", Toast.LENGTH_SHORT).show()
//            SetReminderFragment(note).show((requireActivity() as AppCompatActivity).supportFragmentManager, "Reminder")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNoteToolbar() {
//        toolbar.title = "Add Note"
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
//        toolbar.setNavigationOnClickListener {
//            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
//        }

    }

}


