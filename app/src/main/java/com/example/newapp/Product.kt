package com.example.newapp



class Product {

  private var productComponents: List<ProductComponentV2>? = listOf(
//    ProductComponentV2(
//      635,
//      listOf(
//        ProductComponentV2.Param("volume", "30 г")
//      )
//    ),
//    ProductComponentV2(
//      2078,
//      listOf(
//        ProductComponentV2.Param("volume", "100 г")
//      )
//    ),
//  )


//    ProductComponentV2(
//      10459,
//      listOf(
//        ProductComponentV2.Param("size", "25 см"),
//        ProductComponentV2.Param("dough", "пышное"),
//      )
//    ),
//    ProductComponentV2(
//      10478,
//      listOf(
//        ProductComponentV2.Param("size", "30 см"),
//        ProductComponentV2.Param("dough", "пышное"),
//      )
//    ),
//    ProductComponentV2(
//      10469,
//      listOf(
//        ProductComponentV2.Param("size", "25 см"),
//        ProductComponentV2.Param("dough", "тонкое"),
//      )
//    ),
//    ProductComponentV2(
//      10459,
//      listOf(
//        ProductComponentV2.Param("size", "30 см"),
//        ProductComponentV2.Param("dough", "тонкое"),
//      )
//    )
//  )


    // alt
    ProductComponentV2(
      1,
      listOf(
        ProductComponentV2.Param("size", "20"),
        ProductComponentV2.Param("dough", "A"),
        ProductComponentV2.Param("wew", "1"),
      )
    ),
    ProductComponentV2(
      5,
      listOf(
        ProductComponentV2.Param("size", "20"),
        ProductComponentV2.Param("dough", "A"),
        ProductComponentV2.Param("wew", "2"),
      )
    ),
    ProductComponentV2(
      6,
      listOf(
        ProductComponentV2.Param("size", "20"),
        ProductComponentV2.Param("dough", "A"),
        ProductComponentV2.Param("wew", "3"),
      )
    ),
    ProductComponentV2(
      2,
      listOf(
        ProductComponentV2.Param("size", "30"),
        ProductComponentV2.Param("dough", "B"),
        ProductComponentV2.Param("wew", "2"),
      )
    ),
    ProductComponentV2(
      3,
      listOf(
        ProductComponentV2.Param("size", "30"),
        ProductComponentV2.Param("dough", "C"),
        ProductComponentV2.Param("wew", "3"),
      )
    ),
    ProductComponentV2(
      4,
      listOf(
        ProductComponentV2.Param("size", "30"),
        ProductComponentV2.Param("dough", "A"),
        ProductComponentV2.Param("wew", "2"),
      )
    )
  )

  /**
   * All variants
   * [
   *  ["A", "B", "C"],
   *  ["1", "2"]
   * ]
   */
  val allVariants by lazy {
    val allAvailableVars = mutableMapOf<String, MutableSet<String>>()
    productComponents?.forEach {
      it.params?.forEach { param ->
        if (allAvailableVars.contains(param.name)) {
          allAvailableVars.getOrNull(param.name)?.add(param.value)
        } else {
          allAvailableVars[param.name] = mutableSetOf(param.value)
        }
      }
    }
    allAvailableVars.map { it.key to it.value.toList().sorted() }.sortedBy { it.first }
  }

  /**
   * All products
   *
   *  Map is illegible for new price format with equal ids!!!
   *
   * [
   *  {
   *    "product_id": [
   *      "size" : "20 sm",
   *      "dough" : "thin"
   *    ]"
   *   },
   *   ...
   * ]
   */
  private val allProducts by lazy {
    val pr = productComponents ?: listOf()
    pr.mapNotNull {
      val value = it.params?.map { it.name to it.value } ?: return@mapNotNull null
      it.productId to value.sortedBy { it.first }
    }.filter { it.second.size == allVariants.size }
  }

  private fun updateCurrentState(): List<List<State>> {
    val list: MutableList<MutableList<State>> = mutableListOf()
    var availableProducts = allProducts
    for (index in allVariants.indices) {
      val selectedIndexForCurrentRow = selectedIndexes[index]
      val allVariantsForRow = allVariants[index].second
      list.add(allVariantsForRow.map { State.UNCHECKED }.toMutableList())
      list[index][selectedIndexForCurrentRow] = State.CHECKED
      val newAvailableProducts =
        availableProducts.filter { it.second[index].second == allVariantsForRow[selectedIndexForCurrentRow] }
      if (index != 0) {
        for (i in allVariantsForRow.indices) {
          if (availableProducts.none { it.second[index].second == allVariantsForRow[i] }) {
            list[index][i] = State.DISABLED
          }
        }
      }
      if (newAvailableProducts.isEmpty()) {
        val newValidIndex = list[index].indexOf(State.UNCHECKED)
        selectedIndexes[index] = newValidIndex
        list[index][newValidIndex] = State.CHECKED
        availableProducts =
          availableProducts.filter { it.second[index].second == allVariantsForRow[newValidIndex] }
      } else {
        availableProducts = newAvailableProducts
      }
    }
    println(list)
    return list
  }

  fun selectNewProduct(row: Int?, item: Int?): List<List<State>> {
    if (row != null && item != null) {
      selectedIndexes[row] = item
    }
    return updateCurrentState()
  }

  val selectedProductId: Long?
    get() {
      val listOfSelectedProps = mutableListOf<Pair<String, String>>()

      for (i in allVariants.indices) {
        val currentSelectedIndex = selectedIndexes[i]
        val propName = allVariants[i].first
        val prop = allVariants[i].second[currentSelectedIndex]
        listOfSelectedProps.add(Pair(propName, prop))
      }
      return allProducts.firstOrNull { it.second == listOfSelectedProps }?.first
    }

  private val selectedIndexes = MutableList(allVariants.size) { 0 }

  enum class State {
    DISABLED,
    CHECKED,
    UNCHECKED
  }
}