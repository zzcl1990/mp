buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
    }
}
allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}
tasks.register<Delete>("clean") {
    delete = setOf(rootProject.buildDir)
}