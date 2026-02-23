package notespace.model.noteitem.impl

import notespace.model.noteitem.NoteItem
import notespace.enums.TextStyle

class TextNoteItem : NoteItem {

    private val text: String
    private val style: TextStyle

    constructor(title: String, text: String) : super(title) {
        this.text = text
        this.style = TextStyle.REGULAR
    }

    constructor(title: String, text: String, style: TextStyle) : super(title) {
        this.text = text
        this.style = style
    }

    fun render(withStyle: Boolean): String {
        return if (withStyle) "$title [$style] -> $text" else render()
    }

    override fun render(): String = "Text: $title -> $text"
}