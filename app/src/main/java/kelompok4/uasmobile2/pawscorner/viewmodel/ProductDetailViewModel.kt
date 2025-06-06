package kelompok4.uasmobile2.pawscorner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kelompok4.uasmobile2.pawscorner.data.Product
import kotlinx.coroutines.tasks.await

class ProductDetailViewModel : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val firestore = FirebaseFirestore.getInstance()

    fun loadProductById(documentId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val doc = firestore.collection("products")
                    .document(documentId)
                    .get()
                    .await()

                if (doc.exists()) {
                    val product = Product(
                        title = doc.getString("title") ?: "",
                        weight = doc.getString("weight") ?: "",
                        category = doc.getString("category") ?: "",
                        price = (doc.getLong("price")?.toInt() ?: 0).toString(),
                        quantity = 1,
                        description = doc.getString("description"),
                        imageRes = 0
                    )
                    _product.value = product
                } else {
                    _errorMessage.value = "Produk tidak ditemukan"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat produk: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                val cartItem = hashMapOf(
                    "title" to product.title,
                    "price" to (product.price.toIntOrNull() ?: 0),
                    "quantity" to product.quantity,
                    "category" to product.category
                )
                firestore.collection("cart").add(cartItem)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal tambah ke keranjang: ${e.message}"
            }
        }
    }
}
