package com.gionni2d.mviapp.utils.debug

open class SerializableObject(
    private val serialized: String
) {
    override fun toString(): String = serialized
}