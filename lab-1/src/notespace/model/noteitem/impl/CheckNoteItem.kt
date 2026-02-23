package notespace.model.noteitem.impl

import notespace.model.noteitem.NoteItem

class CheckNoteItem(
    title: String,
    private var isChecked: Boolean = false
) : NoteItem(title) {

    override fun render(): String {
        return "Check: $title -> ${if (isChecked) "x" else "o"}"
    }

    fun toggle() {
        isChecked = !isChecked
    }
}