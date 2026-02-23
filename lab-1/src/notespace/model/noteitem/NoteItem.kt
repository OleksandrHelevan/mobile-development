package notespace.model.noteitem

abstract class NoteItem(
    val title: String
) {
    abstract fun render(): String
    open fun show() {
        println(render())
    }
}