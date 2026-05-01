package dev.artisra.dailyappkt.exceptions

class OpenSubTasksException(message: String) : RuntimeException(message) {
    override fun toString(): String {
        return "OpenSubTasksException(message='$message')"
    }
}