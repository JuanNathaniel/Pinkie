package com.example.pinkiewallet.model

interface Subject<T> {
    fun addObserver(observer: Observer<T>)
    fun removeObserver(observer: Observer<T>)
    fun notifyObservers(data: T)
}