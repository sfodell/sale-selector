package com.cs407.saleselector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.saleselector.ui.model.FriendsStore
import com.cs407.saleselector.ui.screen.AccountScreen
import com.cs407.saleselector.ui.screen.AddSaleScreen
import com.cs407.saleselector.ui.screen.CreateAccountScreen
import com.cs407.saleselector.ui.screen.FriendsScreen
import com.cs407.saleselector.ui.screen.LoginScreen
import com.cs407.saleselector.ui.screen.MyRouteScreen
import com.cs407.saleselector.ui.screen.MySalesScreen
import com.cs407.saleselector.ui.screen.SalesHomeScreen
import com.cs407.saleselector.ui.theme.SaleSelectorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FriendsStore.loadFriends(applicationContext)

        enableEdgeToEdge()
        setContent {
            SaleSelectorTheme {
                Surface(
                    color = Color.Transparent
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLogin = {
                    nav.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                //onToCreate = { nav.navigate("create") },
            )
        }
        composable("create") {
            CreateAccountScreen(
                onCreate = { nav.navigate("home") },
                onToLogin = { nav.popBackStack() }
            )
        }

        // Updated home route to accept optional lat/long arguments
        composable(
            "home?lat={lat}&lng={lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType; defaultValue = 0f },
                navArgument("lng") { type = NavType.FloatType; defaultValue = 0f }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat") ?: 0f
            val lng = backStackEntry.arguments?.getFloat("lng") ?: 0f

            SalesHomeScreen(
                onOpenMyRoute = { nav.navigate("my_route") },
                onOpenFriends = { nav.navigate("friends") },
                onOpenMySales = { nav.navigate("my_sales") },
                onOpenAccount = { nav.navigate("account") },
                focusLat = lat.toDouble(),
                focusLng = lng.toDouble()
            )
        }

        composable("my_route") {
            MyRouteScreen(
                onBack = { nav.popBackStack() }
            )
        }
        composable("friends") {            FriendsScreen(
            onBack = { nav.popBackStack() },
            onGoToSale = { sale ->
                nav.navigate("home?lat=${sale.lat}&lng=${sale.lng}") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
        }

        composable("my_sales") {
            MySalesScreen(
                onBack = { nav.popBackStack() },
                onAddSale = { nav.navigate("add_sale") }
            )
        }
        composable("add_sale") {
            AddSaleScreen(
                onBack = { nav.popBackStack() },
                onSave = { nav.popBackStack() }
            )
        }
        composable("account") {
            AccountScreen(
                onBack = { nav.popBackStack() },
                onLogout = {
                    nav.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
