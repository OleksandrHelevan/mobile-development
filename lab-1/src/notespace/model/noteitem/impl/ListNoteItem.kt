package notespace.model.noteitem.impl

import notespace.model.noteitem.NoteItem

class ListNoteItem(
    title: String,
    val items: MutableList<String> = mutableListOf()
) : NoteItem(title) {

    fun addItem(item: String) {
        items.add(item)
    }

    override fun render(): String {
        return buildString {
            appendLine("List Note: $title")
            items.forEachIndexed { index, item -> appendLine("${index + 1}. $item") }
        }
    }
}