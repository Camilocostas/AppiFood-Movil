package com.example.appifood_movil.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.data.model.sampleRestaurants
import com.example.appifood_movil.data.model.searchRestaurants
import com.example.appifood_movil.data.model.sampleProducts
import com.example.appifood_movil.ui.theme.AppifoodMovilTheme
import kotlinx.coroutines.launch

data class HomeFilter(
    val category: String = "Todas",
    val maxPrice: Float = 100000f,
    val minRating: Float = 1f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf("Hamburguesas") }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(sampleRestaurants) }
    var cartCount by remember { mutableStateOf(0) }
    var filter by remember { mutableStateOf(HomeFilter()) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                FilterContent(
                    filter = filter,
                    onApply = {
                        filter = it
                        scope.launch { drawerState.close() }
                    },
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                item {
                    HeaderSection(
                        searchText = searchText,
                        onSearchChange = {
                            searchText = it
                            searchResults = searchRestaurants(it)
                        },
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }

                if (searchText.length > 2) {
                    items(searchResults) { restaurant ->
                        RestaurantSearchResultCard(restaurant) {
                            navController.navigate("restaurantDetail/${restaurant.name}")
                        }
                    }
                } else {
                    item {
                        CategoryProductsRow(
                            category = selectedCategory,
                            filter = filter,
                            navController = navController,
                            onAddToCart = { cartCount++ }
                        )
                    }
                }
            }
            BottomNavigationBar(cartCount = cartCount, navController = navController)
        }
    }
}

@Composable
fun CategoryProductsRow(
    category: String,
    filter: HomeFilter,
    navController: NavController,
    onAddToCart: () -> Unit
) {
    val filtered = sampleProducts.filter { product ->
        val matchesCategory = filter.category == "Todas" || product.category == category
        val priceValue = product.price.replace("$", "").replace(".", "").replace(",", "").replace(" ", "").filter { it.isDigit() }.toFloatOrNull() ?: 0f
        val matchesPrice = priceValue <= filter.maxPrice
        matchesCategory && matchesPrice
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(filtered, key = { it.id }) { product ->
            FoodItemCard(
                name = product.name,
                imageRes = product.imageRes,
                price = product.price,
                onNavigate = {
                    navController.navigate("productDetail/${product.name}/${product.price}/${product.imageRes}")
                },
                onAddToCart = onAddToCart
            )
        }
    }
}

@Composable
fun FilterContent(
    filter: HomeFilter,
    onApply: (HomeFilter) -> Unit,
    onClose: () -> Unit
) {
    var price by remember { mutableStateOf(filter.maxPrice) }
    var selectedCategory by remember { mutableStateOf(filter.category) }
    var rating by remember { mutableStateOf(filter.minRating) }

    val categories = listOf("Todas", "Hamburguesas", "Sushi", "Bebidas", "Sopas")

    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Filtros", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Cerrar", color = Color(0xFFFF4B3A), modifier = Modifier.clickable { onClose() })
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Precio máximo", fontWeight = FontWeight.Bold)
        Slider(
            value = price,
            onValueChange = { price = it },
            valueRange = 10000f..100000f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFFFF4B3A), activeTrackColor = Color(0xFFFF4B3A))
        )
        Text("$${price.toInt()}", color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))
        Text("Categoría", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFFFF4B3A) else Color(0xFFF1F1F1))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                ) {
                    Text(cat, color = if (isSelected) Color.White else Color.Black, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { onApply(HomeFilter(selectedCategory, price, rating)) },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B3A))
        ) {
            Text("APLICAR FILTROS", color = Color.White)
        }
    }
}

@Composable
fun FoodItemCard(name: String, imageRes: Int, price: String, onNavigate: () -> Unit, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.width(180.dp).padding(top = 10.dp, bottom = 10.dp).clickable { onNavigate() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            )
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text(name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF4B3A))
                    IconButton(onClick = onAddToCart, modifier = Modifier.size(35.dp).background(Color(0xFFFF4B3A), CircleShape)) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(searchText: String, onSearchChange: (String) -> Unit, selectedCategory: String, onCategorySelected: (String) -> Unit, onMenuClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(410.dp).clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))) {
        Image(painter = painterResource(id = R.drawable.burger_background_2), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Column(modifier = Modifier.fillMaxSize().padding(top = 60.dp, start = 20.dp, end = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, null, tint = Color.White) }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text("¿Qué deseas comer\nhoy?", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 35.sp)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Hamburguesas", "Sushi", "Bebidas", "Sopas").forEach { name ->
                    CategoryText(text = name, active = selectedCategory == name) { onCategorySelected(name) }
                }
            }
        }
    }
}

@Composable
fun CategoryText(text: String, active: Boolean, onSelect: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onSelect() }) {
        Text(text = text, color = if (active) Color.White else Color.White.copy(alpha = 0.6f), fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
        if (active) {
            Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color(0xFFFF4B3A), RoundedCornerShape(2.dp)))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(cartCount: Int, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Row(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFFF4B3A), RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)).padding(horizontal = 30.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.navigate("home") }) { Icon(Icons.Default.Home, null, tint = Color.White) }
            IconButton(onClick = { navController.navigate("profile") }) { Icon(Icons.Default.Person, null, tint = Color.White) }
            Spacer(modifier = Modifier.width(40.dp))
            IconButton(onClick = { navController.navigate("orderHistory") }) { Icon(Icons.Default.Message, null, tint = Color.White) }
            IconButton(onClick = { navController.navigate("favorites") }) { Icon(Icons.Default.Favorite, null, tint = Color.White) }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-42).dp).size(80.dp).border(4.dp, Color.White, CircleShape).background(Color(0xFFFF4B3A), CircleShape).clickable { navController.navigate("cart") }, contentAlignment = Alignment.Center) {
            BadgedBox(badge = { if (cartCount > 0) Badge(containerColor = Color.White, contentColor = Color(0xFFFF4B3A)) { Text("$cartCount") } }) {
                Icon(Icons.Default.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Composable
fun RestaurantSearchResultCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).clickable { onClick() }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = restaurant.imageRes), contentDescription = null, modifier = Modifier.size(85.dp).clip(RoundedCornerShape(15.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(restaurant.address, color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    AppifoodMovilTheme {
        HomeScreen(navController)
    }
}
