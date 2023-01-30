package com.example.fundoonotes.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fundoonotes.R
import com.example.fundoonotes.database.DBHelper
import com.example.fundoonotes.listeners.DeleteListener
import com.example.fundoonotes.model.*
import com.example.fundoonotes.util.NoteAdapter
import com.example.fundoonotes.util.ViewState
import com.example.fundoonotes.viewmodel.HomeViewModel
import com.example.fundoonotes.viewmodel.HomeViewModelFactory
import com.example.fundoonotes.viewmodel.SharedViewModel
import com.example.fundoonotes.viewmodel.SharedViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomePage : Fragment() {
    private lateinit var viewType: ViewType
    private lateinit var addNoteFab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var progBar: ProgressBar
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var homeViewModel: HomeViewModel
//    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var list: ArrayList<Notes>
    private var page = 0
    private var limit = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_home_page, container, false)
        addNoteFab = view.findViewById(R.id.addNotesFab)
        recyclerView = view.findViewById(R.id.notesList)
//        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        progBar = view.findViewById(R.id.progressBar)

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(NoteService(DBHelper(requireContext())))
        ).get(HomeViewModel::class.java)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(UserAuthService())
        )[SharedViewModel::class.java]

        list = mutableListOf<Notes>() as ArrayList<Notes>
        viewType = ViewType.GRID
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(viewType.flag, StaggeredGridLayoutManager.VERTICAL)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.notesDisplayType.observe(viewLifecycleOwner, Observer {
            this.viewType = it
            displayNotes()
        })
        gotoAddNotePage()
        getNotes()
    }

    private fun displayNotes() {
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(viewType.flag, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = noteAdapter
    }

    private fun gotoAddNotePage() {
        addNoteFab.setOnClickListener {
            sharedViewModel.setNoteToWrite(NotesOperation.ADD)
        }
    }

    private fun getNotes() {
        homeViewModel.getNotesFromFireStore()
        homeViewModel.getNoteStatus.observe(viewLifecycleOwner, Observer {
            Log.d("HomePageFragment", "getNotes: ${it.size}")
            if (it.isNotEmpty()) {
                progBar.visibility = View.VISIBLE
                noteAdapter = NoteAdapter(it, deleteListener)
                recyclerView.adapter = noteAdapter
            }
            progBar.visibility = View.GONE
            searchNotes()
        })
    }

    private fun searchNotes() {
        sharedViewModel.queryText.observe(viewLifecycleOwner, Observer { text ->
            noteAdapter.filter.filter(text)
        })
    }

    private fun deleteNote(note: Notes) {
        homeViewModel.deleteNotes(note)
        homeViewModel.deleteNoteStatus.observe(viewLifecycleOwner, Observer {
            if (it is ViewState.Success) {
                Toast.makeText(requireContext(), "Note Deleted Successfully", Toast.LENGTH_SHORT)
                    .show()
                noteAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), " Failed To Delete The Note", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private val deleteListener = object : DeleteListener {
        override fun noteDeleted(deleteOnClick: Boolean, note: Notes) {
            activity?.let {
                if (deleteOnClick && note != null) {
                    deleteNote(note)
                }
            }
        }
    }

    private fun paging(){
     var handler = Handler()
    }

}

