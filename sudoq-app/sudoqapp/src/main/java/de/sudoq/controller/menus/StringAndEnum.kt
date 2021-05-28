package de.sudoq.controller.menus

internal class StringAndEnum<E>(val string: String, val enum: E) {
    override fun toString(): String {
        return string
    }
}