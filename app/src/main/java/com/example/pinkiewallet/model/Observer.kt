package com.example.pinkiewallet.model


interface Observer<T> {
    fun update(data: T)
}