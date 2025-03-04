package kweb.docs

import kweb.plugins.css.CSSPlugin

// ANCHOR: fomanticUIPlugin
import kweb.*
import kweb.plugins.fomanticUI.*

fun main() {
    Kweb(port = 16097, plugins = listOf(fomanticUIPlugin)) {
        // ...
    }
}
// ANCHOR_END: fomanticUIPlugin

fun main2() {
    // ANCHOR: search
Kweb(port = 16097, plugins = listOf(fomanticUIPlugin)) {
    doc.body {
        div(fomantic.ui.icon.input) {
            input(type = InputType.text, placeholder = "Search...")
            i(fomantic.search.icon)
        }
    }
}
    // ANCHOR_END: search
}


fun main3() {
    // ANCHOR: CSSPlugin
Kweb(port = 16097, plugins = listOf(CSSPlugin("css", "test.css"))) {
    // ...
}
    // ANCHOR_END: CSSPlugin
}
