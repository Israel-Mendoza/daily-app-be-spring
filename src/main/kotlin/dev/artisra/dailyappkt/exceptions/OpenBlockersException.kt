package dev.artisra.dailyappkt.exceptions

class OpenBlockersException(message: String) : RuntimeException(message) {
    override fun toString(): String {
        return "OpenBlockersException(message='$message')"
    }
}