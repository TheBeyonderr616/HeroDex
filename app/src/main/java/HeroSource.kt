package com.tugas.herodex.model

import android.content.Context

object HeroSource {
    fun getResourceId(context: Context, imageName: String): Int {
        return context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        ) // [cite: 160, 161, 162]
    }
}