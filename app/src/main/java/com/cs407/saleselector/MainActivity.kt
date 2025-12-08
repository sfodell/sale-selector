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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.saleselector.data.FriendsRepository
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
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            //If foreground, active
                            FriendsRepository.setMyStatus(true)
                        } else if (event == Lifecycle.Event.ON_PAUSE) {
                            //If background, inactive
                            FriendsRepository.setMyStatus(false)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                        //Ensure we go offline if the component is destroyed
                        FriendsRepository.setMyStatus(false)
                    }
                }
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
                onToCreate = { nav.navigate("create") },
            )
        }
        composable("create") {
            CreateAccountScreen(
                onCreate = { nav.navigate("home") },
                onToLogin = { nav.popBackStack() }
            )
        }
        composable("home") {
            SalesHomeScreen(
                onOpenMyRoute = { nav.navigate("my_route") },
                onOpenFriends = { nav.navigate("friends") },
                onOpenMySales = { nav.navigate("my_sales") },
                onOpenAccount = { nav.navigate("account") }
            )
        }
        composable("my_route") {
            MyRouteScreen(
                onBack = { nav.popBackStack() }
            )
        }
        composable("friends") {
            FriendsScreen(
                onBack = { nav.popBackStack() }
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

