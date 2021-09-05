package net.veldor.oblepiha_kotlin.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import net.veldor.oblepiha_kotlin.R

class HeaderCardView(context: Context, attrs: AttributeSet?) : CardView(context, attrs) {
    init {
        background = ResourcesCompat.getDrawable(context.resources, R.color.primary, null)
    }
}