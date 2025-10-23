package kelompok4.uasmobile2.pawscorner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kelompok4.uasmobile2.pawscorner.data.Product

// ==========================================================
// Custom Input Field — bisa dipakai untuk Search atau Form
// ==========================================================
@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isLoading: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None // ← TAMBAH INI
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 14.sp) },
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = {
            trailingIcon?.invoke()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
        singleLine = true,
        visualTransformation = visualTransformation, // ← GUNAKAN INI
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium
    )
}


// ==========================================================
// Primary Button — untuk tombol utama
// ==========================================================
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ==========================================================
// Product Card — untuk menampilkan produk di grid
// ==========================================================
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            GlideImage(
                model = product.imageUrl,
                contentDescription = product.title,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 2)
            Text(product.weight, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
            Text(
                product.price,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CustomPopup(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    confirmText: String = "OK",
    dismissText: String? = null,
    isError: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3, // ← LIMIT 3 BARIS
                overflow = TextOverflow.Ellipsis // ← ELLIPSIS KALAU KEGELEMBER
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm?.invoke()
                    onDismiss()
                }
            ) {
                Text(
                    confirmText,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold, // ← MORE BOLD
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            dismissText?.let {
                TextButton(onClick = onDismiss) {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onSurface, // ← LEBIH GELAP
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.large
    )
}