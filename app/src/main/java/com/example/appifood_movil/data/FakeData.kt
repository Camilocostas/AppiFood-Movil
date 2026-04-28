package com.example.appifood_movil.data

import com.example.appifood_movil.R
import com.example.appifood_movil.data.model.Dish
import com.example.appifood_movil.data.model.Restaurant
import com.example.appifood_movil.data.model.FoodProduct
import com.example.appifood_movil.ui.screens.ForgotPasswordForm
import com.example.appifood_movil.data.model.Review


val restaurants = listOf(
    Restaurant(
        name = "China Express",
        address = "Calle 10 #5-20",
        imageRes = R.drawable.restaurantechino,
        schedule = "11:00 AM - 9:00 PM",
        hasDelivery = true,
        rating = "4.8",
        category = "Comida China",
        latitude = 2.4435, longitude = -76.6063,
        dishes = listOf(
            Dish("Arroz Chaufa Especial", 25000.0, R.drawable.arrozchaufa),
            Dish("Lomo Saltado Chino", 28500.0, R.drawable.lomosaltado),
            Dish("Tallarín Saltarín", 22000.0, R.drawable.tallarinsaltarin)
        ),
        reviews = listOf(
            Review("Juan Pérez", "El arroz estaba increíble, muy auténtico.", 5),
            Review("María García", "Buen servicio, pero tardaron un poco.", 4),
            Review("Carlos Ruiz", "Excelente relación calidad-precio.", 5),
            Review("Ana López", "El tallarín estaba un poco salado.", 3),
            Review("Luis Torres", "Me encanta este lugar, siempre vuelvo.", 5),
            Review("Sofía Castro", "La mejor comida china de la ciudad.", 5),
            Review("Diego Vega", "Rapidez en la entrega, muy recomendado.", 4),
            Review("Elena Paz", "El ambiente es muy tranquilo.", 4),
            Review("Pedro Gomez", "Esperaba más cantidad, pero rico.", 3),
            Review("Lucia Velez", "Atención espectacular.", 5)
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
        latitude = 2.4412, longitude = -76.6085,
        dishes = listOf(
            Dish("Ensalada César Premium", 18000.0, R.drawable.ensaladacesar),
            Dish("Bowl Vegano Mixto", 22000.0, R.drawable.bowlvegano),
            Dish("Hamburguesa de Lenteja", 15500.0, R.drawable.hamburguesalenteja)
        ),
        reviews = listOf(
            Review("Camila R.", "¡Las opciones veganas son frescas y llenas de sabor!", 5),
        Review("Jorge M.", "El bowl vegano me sorprendió gratamente.", 5),
        Review("Beatriz O.", "Muy saludable, aunque las porciones son pequeñas.", 4),
        Review("Felipe S.", "La hamburguesa de lenteja es una delicia.", 5),
        Review("Valentina H.", "Excelente atención y ambiente verde.", 4),
        Review("Andrés P.", "La ensalada César es de otro nivel.", 5),
        Review("Clara D.", "Me encanta que tengan ingredientes locales.", 5),
        Review("Hugo L.", "Un poco caro para ser comida vegetariana.", 3),
        Review("Sara G.", "El sabor es increíble, muy creativo.", 5),
        Review("Daniel C.", "Un lugar tranquilo para almorzar.", 4)
    )
    ),
    Restaurant(
        name = "Burguer House",
        address = "Carrera 9 #12-45",
        imageRes = R.drawable.burguer_house,
        schedule = "12:00 PM - 10:00 PM",
        hasDelivery = true,
        rating = "4.9",
        category = "Hamburguesas",
        latitude = 2.4455, longitude = -76.6042,
        dishes = listOf(
            Dish("Monster Bacon", 32000.0, R.drawable.monster_bacon),
            Dish("Clásica de la Casa", 24500.0, R.drawable.clasica_casa),
            Dish("Papas Supremas", 12000.0, R.drawable.papas_supremas)
        ),
        reviews = listOf(
            Review("Marta S.", "La Monster Bacon es un pecado necesario.", 5),
            Review("Roberto B.", "La mejor carne de la ciudad, punto perfecto.", 5),
            Review("Esteban V.", "Demasiado ruido, pero la comida lo vale.", 4),
            Review("Paola R.", "Las papas supremas son adictivas.", 5),
            Review("Julián Z.", "Hamburguesa jugosa y pan fresco.", 5),
            Review("Natalia M.", "El servicio fue un poco lento.", 3),
            Review("Oscar L.", "Gran variedad de salsas, muy bien.", 4),
            Review("Irene C.", "La mejor hamburguesa de Popayán.", 5),
            Review("Sergio T.", "Buena porción, quedé satisfecho.", 4),
            Review("Diana K.", "Un poco grasosa, pero deliciosa.", 4)
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
        latitude = 2.4480, longitude = -76.6025,
        dishes = listOf(
            Dish("Pepperoni King", 38000.0, R.drawable.pepperoni_king),
            Dish("Hawaiana Especial", 35000.0, R.drawable.hawaiana),
            Dish("Chicago Deep Pizza", 42000.0, R.drawable.chicago_pizza)
        ),
        reviews = listOf(
            Review("Tomás A.", "La masa es crocante y los ingredientes frescos.", 5),
            Review("Gabriela P.", "La Deep Dish es pesada pero deliciosa.", 4),
            Review("Laura E.", "La mejor pizza de la ciudad.", 5),
            Review("Ricardo F.", "El pepperoni tiene un toque especial.", 5),
            Review("Sandra J.", "Atención rápida y muy amable.", 4),
            Review("Mauricio B.", "Excelente opción para viernes por la noche.", 5),
            Review("Silvia Q.", "Un poco costosa, pero vale la pena.", 4),
            Review("Felipe W.", "La hawaiana es mejor de lo que esperaba.", 4),
            Review("Andrea N.", "El lugar es pequeño pero acogedor.", 3),
            Review("Kevin R.", "¡Simplemente la mejor pizza!", 5)
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
        latitude = 2.4610, longitude = -76.5980,
        dishes = listOf(
            Dish("Baby Beef 300g", 45000.0, R.drawable.baby_beef),
            Dish("Churrasco Especial", 48000.0, R.drawable.churrasco),
            Dish("Costillas BBQ", 39900.0, R.drawable.costillas_bbq)
        ),
        reviews = listOf(
            Review("Juan D.", "El baby beef estaba en el punto exacto.", 5),
            Review("Elena S.", "El mejor lugar para un domingo en familia.", 5),
            Review("Gabriel M.", "La carne estaba un poco dura.", 2),
            Review("Lucía T.", "Excelente servicio y buen ambiente.", 4),
            Review("Héctor L.", "Porciones gigantes, excelente.", 5),
            Review("María C.", "La parrilla es deliciosa, 10/10.", 5),
            Review("Carlos P.", "Tardaron mucho en traernos la cuenta.", 3),
            Review("Sofía O.", "El ambiente campestre es genial.", 4),
            Review("Diego I.", "Muy buen sabor, calidad precio.", 4),
            Review("Paula H.", "El churrasco estaba espectacular.", 5)
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
        latitude = 2.4390, longitude = -76.6090,
        dishes = listOf(
            Dish("Roll Philadelphia", 28000.0, R.drawable.roll_phila),
            Dish("Ramen Tradicional", 32500.0, R.drawable.ramen),
            Dish("Gyoza Mixtas", 18000.0, R.drawable.gyozas)
        ),
        reviews = listOf(
            Review("Tatiana R.", "El ramen tiene un caldo muy profundo.", 5),
            Review("Mateo G.", "El sushi es muy fresco, gran calidad.", 5),
            Review("Paula D.", "Un poco caro, pero delicioso.", 4),
            Review("Fernando S.", "La atención es muy detallista.", 5),
            Review("Luciana M.", "Los rolls son muy pequeños.", 3),
            Review("Andrés O.", "Experiencia muy auténtica y zen.", 5),
            Review("Valeria C.", "El ambiente es perfecto para una cita.", 5),
            Review("Pablo J.", "Gran variedad de platos japoneses.", 4),
            Review("Karen L.", "El tiempo de espera es razonable.", 4),
            Review("Santiago B.", "Simplemente exquisito.", 5)
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
        latitude = 2.4350, longitude = -76.6030,
        dishes = listOf(
            Dish("Tacos al Pastor x3", 21000.0, R.drawable.tacos_pastor),
            Dish("Burrito Supremo", 26000.0, R.drawable.burrito),
            Dish("Quesadilla de Birria", 24500.0, R.drawable.quesabirria)
        ),
        reviews = listOf(
            Review("Miguel A.", "El picante está en su punto, muy real.", 5),
            Review("Luisa F.", "El pastor es idéntico a México.", 5),
            Review("Javier M.", "Muy ricas las quesadillas.", 4),
            Review("Daniela P.", "El lugar es sencillo pero la comida es wow.", 5),
            Review("Tomás K.", "Poca variedad de salsas.", 3),
            Review("Elena W.", "Los burritos son enormes, muy recomendados.", 5),
            Review("Raúl C.", "El servicio al cliente puede mejorar.", 3),
            Review("Andrea R.", "La mejor comida mexicana que he probado.", 5),
            Review("Sergio L.", "Rapidez en la entrega.", 4),
            Review("Natalia D.", "Buen precio y buen sabor.", 4)
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
        latitude = 2.4520, longitude = -76.6055,
        dishes = listOf(
            Dish("Lasaña Boloñesa", 29000.0, R.drawable.lasana),
            Dish("Fettuccine Alfredo", 27500.0, R.drawable.fettuccine),
            Dish("Raviolis de Espinaca", 31000.0, R.drawable.raviolis)
        ),
        reviews = listOf(
            Review("Valentina P.", "Un rincón italiano muy romántico.", 5),
            Review("Diego M.", "La lasaña es muy reconfortante.", 5),
            Review("Camila S.", "El servicio fue algo lento.", 3),
            Review("Santiago V.", "La pasta está cocida perfectamente.", 5),
            Review("Luisa N.", "Excelente selección de vinos.", 4),
            Review("Carlos H.", "Un poco oscuro el local.", 3),
            Review("Elena G.", "Los raviolis son deliciosos.", 5),
            Review("Felipe A.", "La mejor experiencia italiana.", 5),
            Review("Sofía R.", "Ambiente muy tranquilo y relajado.", 4),
            Review("Pedro B.", "Muy buena calidad, recomiendo.", 4)
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
        latitude = 2.4410, longitude = -76.6010,
        dishes = listOf(
            Dish("Ceviche de Camarón", 26000.0, R.drawable.ceviche),
            Dish("Mojarra Frita", 32000.0, R.drawable.mojarra),
            Dish("Cazuela de Mariscos", 45000.0, R.drawable.cazuela)
        ),
        reviews = listOf(
            Review("Raúl M.", "El ceviche sabe a mar puro, fresco.", 5),
            Review("Ana T.", "La cazuela es abundante y deliciosa.", 5),
            Review("Luis P.", "La mojarra estaba un poco seca.", 3),
            Review("Sara H.", "Muy buen sabor costeño.", 4),
            Review("Julián E.", "El lugar está un poco descuidado.", 3),
            Review("Marta R.", "Excelente atención y frescura.", 5),
            Review("Andrés C.", "Los mariscos son de primera.", 5),
            Review("Laura V.", "Precio justo para la cantidad.", 4),
            Review("Diego G.", "Muy recomendado para ir en familia.", 4),
            Review("Lucía B.", "La mejor pescadería de la zona.", 5)
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
        latitude = 2.4440, longitude = -76.6075,
        dishes = listOf(
            Dish("Combo 8 Presas", 48000.0, R.drawable.combo_pollo),
            Dish("Alitas Picantes x12", 29900.0, R.drawable.alitas),
            Dish("Sándwich de Pollo", 19500.0, R.drawable.sand_pollo)
        ),
        reviews = listOf(
            Review("Pedro L.", "El pollo es muy crocante, genial.", 5),
            Review("Andrea M.", "Ideal para compartir con amigos.", 4),
            Review("Carlos D.", "Un poco grasoso, pero rico.", 3),
            Review("Javier S.", "Las alitas picantes son lo mejor.", 5),
            Review("Valentina R.", "Rapidez y buen precio.", 4),
            Review("Esteban C.", "El sándwich de pollo es enorme.", 5),
            Review("Sara P.", "El ambiente es muy familiar.", 4),
            Review("Daniel H.", "Un poco desordenado el servicio.", 3),
            Review("Paula O.", "Me encanta este lugar de pollo.", 5),
            Review("Miguel I.", "Pollo fresco y muy buen sazón.", 4)
        )
    )
)

val allProducts = listOf(
    FoodProduct(1, "Cheeseburger", 25.000, R.drawable.cheese, "Rapida"),
    FoodProduct(2, "Big Mac", 32.000, R.drawable.bicmac, "Rapida"),
    FoodProduct(7, "Hamburguesa", 15.000, R.drawable.clasica, "Rapida"),
    FoodProduct(3, "Philadelphia", 28.000, R.drawable.philadelphia, "Oriental"),
    FoodProduct(4, "Ojo de Tigre", 35.000, R.drawable.ojotigre, "Oriental"),
    FoodProduct(5, "Coca Cola 1L", 6.000, R.drawable.cocacola, "Bebidas"),
    FoodProduct(6, "Ramen Tonkotsu", 22.000, R.drawable.ramen, "Oriental"),
    FoodProduct(7,"Mega-Taco", 23.000, R.drawable.cheese, "Mexicana"),
    FoodProduct(8, "Sopa de guisantes", 16.000, R.drawable.lomosaltado, "Mexicana"),
    FoodProduct(9, "Helado de Yogurt", 9.000, R.drawable.helado, "Postres"),
    FoodProduct(10, "Pastel de Chocolate", 8.000, R.drawable.ensaladacesar, "Postres")
)

fun searchRestaurants(query: String): List<Restaurant> {
    return restaurants.filter { restaurant ->
        restaurant.name.contains(query, ignoreCase = true) ||
                restaurant.address.contains(query, ignoreCase = true)
    }
}