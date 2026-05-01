package dev.artisra.dailyappkt.exceptions

class DoneOrCanceledTaskException(message: String) : RuntimeException(message) {
    override fun toString(): String {
        return "DoneOrCanceledTaskException(message='$message')"
    }
}