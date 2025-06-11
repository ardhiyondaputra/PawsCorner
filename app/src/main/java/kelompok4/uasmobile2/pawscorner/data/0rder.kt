package kelompok4.uasmobile2.pawscorner.data

data class Order(
    val id: String = "",
    val title: String = "",
    val quantity: Int = 1,
    val price: Int = 0,
    val status: String="Diproses"
)