package kelompok4.uasmobile2.pawscorner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun PaymentSuccessScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pembayaran Berhasil!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Terima kasih telah melakukan pembayaran.\nPesanan Anda sedang diproses.",
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate("status_order_screen") {
                    popUpTo("payment_success_screen") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Status Pesanan", color = Color.White)
        }
    }
}