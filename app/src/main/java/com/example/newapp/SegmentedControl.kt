package com.example.newapp

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup


class SegmentedControl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : MaterialButtonToggleGroup(context, attrs, defStyleAttr) {

  override fun addView(child: View?) {
    super.addView(child)
    if (child is MaterialButton) {
      child.maxLines = 2
    }
  }
}