package notespace.extensions

import notespace.model.noteitem.impl.ListNoteItem
import notespace.model.noteitem.impl.TextNoteItem

fun String.firstChar(): Char? = if (this.isNotEmpty()) this[0] else null

fun TextNoteItem.printTitle() = println("TextNote title: $title")

fun ListNoteItem.itemCount() = run { println("Total items: ${this.items.size}") }