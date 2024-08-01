package com.denys.shoppinglist.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.denys.shoppinglist.R
import com.denys.shoppinglist.databinding.ActivityNewNoteBinding
import com.denys.shoppinglist.entities.NoteItem
import com.denys.shoppinglist.fragments.NoteFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()
        getNote()
    }

    private fun getNote(){
        val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (sNote != null){
            note = sNote as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding){
        edTitle.setText(note?.title)
        edDescription.setText(note?.content)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_save -> {
                setMainResult()
            }
            android.R.id.home -> {
                finish()
            }
            R.id.id_bold -> {
                setBoldForSelectedText()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(binding) {
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd

        val styles = edDescription.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyleSpan: StyleSpan? = null

        val selectedText: String = edDescription.getText().substring(startPos, endPos)

        if(selectedText.isNotEmpty()) {
            if (styles.isNotEmpty()) {
                edDescription.text.removeSpan(styles[0])
            } else {
                boldStyleSpan = StyleSpan(Typeface.BOLD)
                edDescription.text.setSpan(boldStyleSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            edDescription.text.trim()
            edDescription.setSelection(startPos)
        } else {
            Toast.makeText(this@NewNoteActivity, "No text selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMainResult(){
        var editState = "new"
        val tempNote: NoteItem? = if(note == null){
            createNewNote()
        } else{
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = edTitle.text.toString(),
            content = edDescription.text.toString()
        )
    }

    private fun createNewNote(): NoteItem{
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            binding.edDescription.text.toString(),
            getCurrentTime(),
            ""
            )
    }

    private fun getCurrentTime(): String{
        val formatter = SimpleDateFormat("hh:mm:ss - yyyy/MM/dd", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    private fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }
}