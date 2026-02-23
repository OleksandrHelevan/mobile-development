package notespace.manager

import notespace.model.noteitem.NoteItem

object NoteManager {
    private val notes = mutableListOf<NoteItem>()

    fun addNote(note: NoteItem) {
        notes.add(note)
    }

    fun showAllNotes() {
        notes.forEach { it.show() }
    }

    fun totalNotes(): Int = notes.size
}