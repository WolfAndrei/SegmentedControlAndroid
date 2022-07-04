package com.example.newapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.children
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
/* ================================ NESTED CLASS ================================= */
/* ============================== PUBLIC PROPERTIES ============================== */
/* ============================== PRIVATE PROPERTIES ============================= */

  private var segmentedControlContainer: LinearLayout? = null
  private var productIdTextView: TextView? = null
  private var product: Product? = null
  private var productId: Long? = null

/* ================================== LIFECYCLE ================================== */

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    bindViews()

    // Get product
    product = Product()
    // Init segmented control container
    setupSegmentedControlContainer(segmentedControlContainer)
  }

/* =============================== PUBLIC METHODS ================================ */
/* =============================== PRIVATE METHODS =============================== */



  private fun bindViews() {
    segmentedControlContainer = findViewById(R.id.segmented_control_container)
    productIdTextView = findViewById(R.id.product_id_text_view)
  }

  private fun createSegment(context: Context, text: String) =
    MaterialButton(ContextThemeWrapper(context, R.style.Widget_App_Button_TextButton_Outlined), null, R.style.Widget_App_Button_TextButton_Outlined).apply {
      val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
      this.text = text
      setPadding(0,0 ,0,0)
      this.layoutParams = layoutParams
      gravity = Gravity.CENTER
    }

  private fun fillSegmentedControlWithButtons(segmentedControl: SegmentedControl?, params: List<String>) {
    segmentedControl ?: return
    for (index in 0.until(params.size)) {
      val child = createSegment(this, params[index])
      child.id = index
      child.setOnClickListener(::onChange)
      segmentedControl.addView(child)
    }
  }

  private fun setupSegmentedControlContainer(container: LinearLayout?) {
    container ?: return
    val product = product ?: return
    var rowId = 0
    for ((_,variant) in product.allVariants) {
      val segmentedControl = SegmentedControl(ContextThemeWrapper(this, R.style.SelectorTheme), null, 0).apply {
        val segmentLinearParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        isSingleSelection = true
        isSelectionRequired = true
        layoutParams = segmentLinearParams
        id = rowId
        fillSegmentedControlWithButtons(this, variant)
      }
      container.addView(segmentedControl)
      rowId += 1
    }
    // initial selection
    onChange(null)
  }

  private fun updateProduct() {
    val formatter = getString(R.string.product_id_placeholder)
    productIdTextView?.text = String.format(formatter, productId)
  }

  /* =================================== ACTIONS =================================== */

  private fun onChange(button: View?) {
    val tappedRowId = (button?.parent as? SegmentedControl)?.id
    val tappedButtonId = button?.id
    val state = product?.selectNewProduct(tappedRowId, tappedButtonId)

    segmentedControlContainer?.children?.forEach {
      val segmentedControl = it as? SegmentedControl ?: return@forEach
      val segmentedControlButtons = segmentedControl.children.mapNotNull { btn -> btn as? MaterialButton }.toList()
      segmentedControl.clearChecked() // important
      for (i in 0.until(segmentedControlButtons.size)) {
        val btn = segmentedControlButtons[i]
        when (state?.get(segmentedControl.id)?.get(i)) {
          Product.State.UNCHECKED -> {
            btn.isEnabled = true
            btn.isChecked = false
          }
          Product.State.DISABLED -> {
            btn.isEnabled = false
            btn.isChecked = false
          }
          Product.State.CHECKED -> {
            btn.isEnabled = true
            btn.isChecked = true
          }
          else -> Unit
        }
      }
    }
    productId = product?.selectedProductId
    updateProduct()
  }

/* =========================== INTERFACE CONFIRMATIONS =========================== */
/* ============================== COMPANION OBJECT =============================== */
}