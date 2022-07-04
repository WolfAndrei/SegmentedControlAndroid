package com.example.newapp
data class ProductComponentV2(
  var productId: Long,
  var params: List<Param>?
): Cloneable {

  data class Param(
    var name: String,
    var value: String
  ) : Cloneable {
    public override fun clone(): Param {
      return Param(name, value)
    }
  }
}

fun <K, V> HashMap<K, V>.getOrNull(key: K): V? {
  return this.getOrElse(key) { null }
}

fun <K, V> Map<K, V>.getOrNull(key: K): V? {
  return this.getOrElse(key) { null }
}