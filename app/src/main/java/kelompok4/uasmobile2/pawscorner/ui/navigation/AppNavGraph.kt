package kelompok4.uasmobile2.pawscorner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kelompok4.uasmobile2.pawscorner.ui.screens.CartScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.DetailProductScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.DetailStatusScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.EditProfileScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.EmailVerificationScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ForgotPasswordScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.LoginScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.RegisterScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.HomeScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.NotificationScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.OrderStatusScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.PaymentScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.PaymentSuccessScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileDetailScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.ProfileScreen
import kelompok4.uasmobile2.pawscorner.viewmodel.AuthViewModel
import kelompok4.uasmobile2.pawscorner.viewmodel.AddressViewModel
import kelompok4.uasmobile2.pawscorner.ui.screens.alamat.AddressListScreen
import kelompok4.uasmobile2.pawscorner.ui.screens.alamat.AddAddressScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    addressViewModel: AddressViewModel,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("email_verification/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                navController = navController,
                authViewModel = authViewModel,
                email = email
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("profile_detail") {
            ProfileDetailScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("notification") {
            NotificationScreen(navController)
        }

        composable("address") {
            AddressListScreen(
                navController = navController,
                addressViewModel = addressViewModel  // Pass the addressViewModel here
            )
        }
        composable("add_address") {
            AddAddressScreen(
                navController = navController,
                addressViewModel = addressViewModel // Pass the AddressViewModel here
            )
        }

        composable(
            route = "payment/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            PaymentScreen(
                navController = navController,
                addressViewModel = addressViewModel
            )
        }

        composable("edit_address/{addressId}") { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId") ?: ""
            AddAddressScreen(
                navController = navController,
                addressViewModel = addressViewModel,
                addressId = addressId // Pass the addressId for editing
            )
        }

        composable(
            route = "detail/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            DetailProductScreen(documentId = documentId, navController = navController)
        }

        composable("cart") {
            CartScreen(
                navController = navController
            )
        }

        composable("payment_success_screen") {
            PaymentSuccessScreen(navController)
        }

        composable("status_order_screen") {
            OrderStatusScreen(navController)
        }

        composable("forgot_password") {
            ForgotPasswordScreen(navController, authViewModel)
        }

        composable("detail_status/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            DetailStatusScreen(navController, orderId, addressViewModel)
        }

    }
}