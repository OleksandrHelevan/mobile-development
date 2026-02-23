
package notespace

import notespace.model.noteitem.impl.*
import notespace.manager.NoteManager
import notespace.enums.TextStyle
import notespace.extensions.firstChar
import notespace.extensions.itemCount
import notespace.extensions.printTitle
import notespace.model.noteitem.NoteItem

fun main() {

    val printNotes: (List<NoteItem>) -> Unit = { notes ->
        notes.forEach { println(it.render()) }
    }

    val text1 = TextNoteItem("Kotlin", "Learn basics")
    text1.printTitle()
    val text2 = TextNoteItem("Project", "Complete lab", TextStyle.BOLD)

    val check1 = CheckNoteItem("Homework")
    check1.toggle()

    val list1 = ListNoteItem("Shopping")
    list1.itemCount()
    list1.addItem("Milk")
    list1.addItem("Bread")

    NoteManager.addNote(text1)
    NoteManager.addNote(text2)
    NoteManager.addNote(check1)
    NoteManager.addNote(list1)

    printNotes(listOf(text1, text2, check1, list1))
    var optionalText: String? = null
    println("Optional text length: ${optionalText?.length}")
    optionalText = "Hello"
    println("Optional text length: ${optionalText.length}")

    println("First char of title: ${text1.render().firstChar()}")
    println("-------------------")
    NoteManager.showAllNotes()

}