buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}

plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.android.library") version "7.4.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}