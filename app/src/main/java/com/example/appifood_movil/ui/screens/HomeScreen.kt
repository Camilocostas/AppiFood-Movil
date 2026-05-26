package com.example.appifood_movil.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appifood_movil.ui.viewmodel.HomeViewModel
import com.example.appifood_movil.ui.components.AppiFoodFooter
import com.example.appifood_movil.ui.components.CategoryChip
import com.example.appifood_movil.ui.components.PromoBanner
import com.example.appifood_movil.ui.components.CarouselHeader
import com.example.appifood_movil.R
import com.example.appifood_movil.ui.viewmodel.SearchViewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import coil.compose.AsyncImage
import com.example.appifood_movil.navigation.Screen
import com.example.appifood_movil.ui.theme.FoodRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel
) {
    val filteredRestaurants by searchViewModel.filteredRestaurants.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            searchViewModel.fetchUserLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppiFoodFooter(
                navController = navController,
                currentRoute = Screen.Home.route,
                cartCount = 6,
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }
    ) { paddingValues ->

         LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                item {
                    CarouselHeader(height = 300.dp) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp, start = 20.dp),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logomau),
                                contentDescription = null,
                                modifier = Modifier.width(130.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.default_location),
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Text(
                                text = stringResource(id = R.string.welcome_user),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                item {
                    PromoBanner(onClick = {
                        navController.navigate(Screen.Home.route)
                    })
                }

                item {
                    SectionHeader(title = stringResource(id = R.string.section_categories))
                    val categories = listOf("Todos", "Bebidas", "Postres", "Rapida", "Oriental", "Mexicana", "Vegetariana")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected =
                                (cat == "Todos" && viewModel.selectedCategory == "Todas") || viewModel.selectedCategory == cat
                            CategoryChip(text = cat, isSelected = isSelected) {
                                viewModel.onCategorySelected(cat)
                            }
                        }
                    }
                }

             item {
                 SectionHeader(
                     title = stringResource(id = R.string.section_promotions),
                     showViewAll = true,
                     onViewAllClick = { /* TODO */ }
                 )
             }

             item {
                 LazyRow(
                     contentPadding = PaddingValues(horizontal = 20.dp),
                     horizontalArrangement = Arrangement.spacedBy(15.dp)
                 ) {
                     items(viewModel.filteredProducts) { product ->
                         PromoFoodCard(
                             name = product.name,
                             price = "$ ${String.format("%,.0f", product.price)}",
                             oldPrice = "$35.000",
                             imageRes = product.imageRes,
                             onNavigate = {
                                 navController.navigate("${Screen.ProductDetail.route}/${product.id}")
                             }
                         )
                     }
                 }
             }

             item { 
                 SectionHeader(
                     title = stringResource(id = R.string.section_popular_restaurants), 
                     showViewAll = true,
                     onViewAllClick = { /* TODO */ }
                 ) 
             }

             item {
                 LazyRow(
                     contentPadding = PaddingValues(horizontal = 20.dp),
                     horizontalArrangement = Arrangement.spacedBy(12.dp)
                 ) {
                     items(filteredRestaurants) { restaurant ->
                         MinimalRestaurantCard(
                             name = restaurant.name,
                             rating = restaurant.rating,
                             time = stringResource(id = R.string.delivery_time_range),
                             imageUrl = restaurant.imageUrl,
                             imageRes = restaurant.imageRes,
                             onClick = { navController.navigate("${Screen.RestaurantDetail.route}/${restaurant.id}") }
                         )
                     }
                 }
             }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
}


@Composable
fun PromoFoodCard(name: String, price: String, oldPrice: String, imageRes: Int, onNavigate: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onNavigate() }
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 12.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = stringResource(id = R.string.label_offer),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(price, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                oldPrice,
                color = Color.Gray,
                fontSize = 11.sp,
                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
            )
        }
    }
}

@Composable
fun MinimalRestaurantCard(
    name: String,
    rating: String,
    time: String,
    imageUrl: String? = null,
    imageRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUrl ?: imageRes,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.restaurantechino)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, null, tint = FoodRating, modifier = Modifier.size(12.dp))
            Text(" $rating", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(" • $time", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    showViewAll: Boolean = false,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        if (showViewAll) {
            Text(
                text = stringResource(id = R.string.label_view_all),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
    }
}
