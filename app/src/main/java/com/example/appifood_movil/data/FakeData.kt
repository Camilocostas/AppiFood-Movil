package com.example.appifood_movil.data

import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.Dish
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.data.model.FoodProduct



val restaurants = listOf(
    Restaurant(
        name = "China Express",
        address = "Calle 10 #5-20",
        imageRes = R.drawable.restaurantechino,
        schedule = "11:00 AM - 9:00 PM",
        hasDelivery = true,
        rating = "4.8",
        category = "Comida China",
        dishes = listOf(
            Dish("Arroz Chaufa Especial", "$ 25.000", R.drawable.arrozchaufa),
            Dish("Lomo Saltado Chino", "$ 28.500", R.drawable.lomosaltado),
            Dish("Tallarín Saltarín", "$ 22.000", R.drawable.tallarinsaltarin)
        )
    ),
    Restaurant(
        name = "La verdura",
        address = "Calle 17 #7-28",
        imageRes = R.drawable.restaurantevegano,
        schedule = "9:00 AM - 10:00 PM",
        hasDelivery = false,
        rating = "4.6",
        category = "Comida Vegana",
        dishes = listOf(
            Dish("Ensalada César Premium", "$ 18.000", R.drawable.ensaladacesar),
            Dish("Bowl Vegano Mixto", "$ 22.000", R.drawable.bowlvegano),
            Dish("Hamburguesa de Lenteja", "$ 15.500", R.drawable.hamburguesalenteja)
        )
    ), // <--- La coma que faltaba estaba aquí
    Restaurant(
        name = "Burguer House",
        address = "Carrera 9 #12-45",
        imageRes = R.drawable.burguer_house,
        schedule = "12:00 PM - 10:00 PM",
        hasDelivery = true,
        rating = "4.9",
        category = "Hamburguesas",
        dishes = listOf(
            Dish("Monster Bacon", "$ 32.000", R.drawable.monster_bacon),
            Dish("Clásica de la Casa", "$ 24.500", R.drawable.clasica_casa),
            Dish("Papas Supremas", "$ 12.000", R.drawable.papas_supremas)
        )
    ),
    Restaurant(
        name = "Pizza Nostra",
        address = "Calle 5 #15-30",
        imageRes = R.drawable.pizza_nostra,
        schedule = "3:00 PM - 11:00 PM",
        hasDelivery = true,
        rating = "4.7",
        category = "Pizzería",
        dishes = listOf(
            Dish("Pepperoni King", "$ 38.000", R.drawable.pepperoni_king),
            Dish("Hawaiana Especial", "$ 35.000", R.drawable.hawaiana),
            Dish("Chicago Deep Pizza", "$ 42.000", R.drawable.chicago_pizza)
        )
    ),
    Restaurant(
        name = "El Rancho de la Parrilla",
        address = "Variante Norte #4-80",
        imageRes = R.drawable.parrilla_rancho,
        schedule = "11:30 AM - 8:00 PM",
        hasDelivery = true,
        rating = "4.5",
        category = "Carnes y Parrilla",
        dishes = listOf(
            Dish("Baby Beef 300g", "$ 45.000", R.drawable.baby_beef),
            Dish("Churrasco Especial", "$ 48.000", R.drawable.churrasco),
            Dish("Costillas BBQ", "$ 39.900", R.drawable.costillas_bbq)
        )
    ),
    Restaurant(
        name = "Sushi Zen",
        address = "Calle 18 #9-12",
        imageRes = R.drawable.sushi_zen,
        schedule = "12:00 PM - 9:30 PM",
        hasDelivery = true,
        rating = "4.9",
        category = "Comida Japonesa",
        dishes = listOf(
            Dish("Roll Philadelphia", "$ 28.000", R.drawable.roll_phila),
            Dish("Ramen Tradicional", "$ 32.500", R.drawable.ramen),
            Dish("Gyoza Mixtas", "$ 18.000", R.drawable.gyozas)
        )
    ),
    Restaurant(
        name = "Tacos del Sol",
        address = "Carrera 7 #20-10",
        imageRes = R.drawable.tacos_sol,
        schedule = "5:00 PM - 12:00 AM",
        hasDelivery = true,
        rating = "4.4",
        category = "Comida Mexicana",
        dishes = listOf(
            Dish("Tacos al Pastor x3", "$ 21.000", R.drawable.tacos_pastor),
            Dish("Burrito Supremo", "$ 26.000", R.drawable.burrito),
            Dish("Quesadilla de Birria", "$ 24.500", R.drawable.quesabirria)
        )
    ),
    Restaurant(
        name = "Pasta & Vino",
        address = "Cl. 15 Nte. #8-22",
        imageRes = R.drawable.pasta_vino,
        schedule = "12:00 PM - 10:00 PM",
        hasDelivery = false,
        rating = "4.8",
        category = "Italiana",
        dishes = listOf(
            Dish("Lasaña Boloñesa", "$ 29.000", R.drawable.lasana),
            Dish("Fettuccine Alfredo", "$ 27.500", R.drawable.fettuccine),
            Dish("Raviolis de Espinaca", "$ 31.000", R.drawable.raviolis)
        )
    ),
    Restaurant(
        name = "Delicias del Mar",
        address = "Calle 2 #4-50",
        imageRes = R.drawable.delicias_mar,
        schedule = "11:00 AM - 5:00 PM",
        hasDelivery = true,
        rating = "4.6",
        category = "Pescadería",
        dishes = listOf(
            Dish("Ceviche de Camarón", "$ 26.000", R.drawable.ceviche),
            Dish("Mojarra Frita", "$ 32.000", R.drawable.mojarra),
            Dish("Cazuela de Mariscos", "$ 45.000", R.drawable.cazuela)
        )
    ),
    Restaurant(
        name = "Chicken Crispy",
        address = "Carrera 6 #10-15",
        imageRes = R.drawable.chicken_crispy,
        schedule = "11:00 AM - 10:30 PM",
        hasDelivery = true,
        rating = "4.3",
        category = "Pollo Frito",
        dishes = listOf(
            Dish("Combo 8 Presas", "$ 48.000", R.drawable.combo_pollo),
            Dish("Alitas Picantes x12", "$ 29.900", R.drawable.alitas),
            Dish("Sándwich de Pollo", "$ 19.500", R.drawable.sand_pollo)
        )
    )
)

val allProducts = listOf(
    FoodProduct(1, "Cheeseburger", "$25.000", R.drawable.cheese, "Hamburguesas"),
    FoodProduct(2, "Big Mac", "$32.000", R.drawable.bicmac, "Hamburguesas"),
    FoodProduct(7, "Hamburguesa", "$15.000", R.drawable.clasica, "Hamburguesas"),
    FoodProduct(3, "Philadelphia", "$28.000", R.drawable.philadelphia, "Sushi"),
    FoodProduct(4, "Ojo de Tigre", "$35.000", R.drawable.ojotigre, "Sushi"),
    FoodProduct(5, "Coca Cola 1L", "$6.000", R.drawable.cocacola, "Bebidas"),
    FoodProduct(6, "Ramen Tonkotsu", "$22.000", R.drawable.ramen, "Sopas")
)

fun searchRestaurants(query: String): List<Restaurant> {
    return restaurants.filter { restaurant ->
        restaurant.name.contains(query, ignoreCase = true) ||
                restaurant.address.contains(query, ignoreCase = true)
    }
}