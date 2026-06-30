package com.example.appifood_movil.ui.map

object MapStyles {

    // Ya no la usaremos en AnimatedMapSection, pero la dejamos por si la
    // necesitas en otro lugar (fallback offline, etc.)
    fun osmRasterStyle(): String = """
    {
      "version": 8,
      "sources": {
        "raster-tiles": {
          "type": "raster",
          "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
          "tileSize": 256,
          "attribution": "© OpenStreetMap contributors"
        }
      },
      "layers": [
        {
          "id": "osm-tiles",
          "type": "raster",
          "source": "raster-tiles",
          "minzoom": 0,
          "maxzoom": 19
        }
      ]
    }
    """.trimIndent()

    // 🆕 Estilo vectorial con detalle tipo Google Maps
    const val openFreeMapStyleUrl = "https://tiles.openfreemap.org/styles/liberty"
}