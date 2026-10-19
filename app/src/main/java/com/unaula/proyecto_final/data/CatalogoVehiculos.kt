package com.unaula.proyecto_final.data

import com.unaula.proyecto_final.model.TipoVehiculo

object CatalogoVehiculos {

    fun obtenerMarcas(tipo: TipoVehiculo): List<String> {
        return when (tipo) {
            TipoVehiculo.AUTOMOVIL -> listOf(
                "Toyota", "Chevrolet", "Renault", "Mazda", "Nissan",
                "Kia", "Hyundai", "Volkswagen", "Ford", "Suzuki", "Otra (Manual)"
            )
            TipoVehiculo.CAMIONETA -> listOf(
                "Toyota", "Chevrolet", "Renault", "Mazda", "Nissan",
                "Kia", "Hyundai", "Ford", "Volkswagen", "Jeep", "Otra (Manual)"
            )
            TipoVehiculo.MOTOCICLETA -> listOf(
                "Yamaha", "Honda", "Bajaj", "TVS", "AKT",
                "Suzuki", "KTM", "Hero", "BMW", "Kawasaki", "Otra (Manual)"
            )
        }
    }

    fun obtenerModelos(tipo: TipoVehiculo, marca: String): List<String> {
        val modelosMap: Map<String, List<String>> = when (tipo) {
            TipoVehiculo.AUTOMOVIL -> mapOf(
                "Toyota" to listOf("Corolla", "Yaris", "Camry", "Prius"),
                "Chevrolet" to listOf("Onix", "Spark", "Sail", "Joy", "Tracker", "Cruze"),
                "Renault" to listOf("Logan", "Sandero", "Stepway", "Kwid", "Megane"),
                "Mazda" to listOf("Mazda 2", "Mazda 3", "CX-30", "Allegro"),
                "Nissan" to listOf("Versa", "March", "Sentra", "Note"),
                "Kia" to listOf("Picanto", "Rio", "Soluto", "Cerato"),
                "Hyundai" to listOf("Grand i10", "Accent", "Elantra", "HB20"),
                "Volkswagen" to listOf("Gol", "Polo", "Virtus", "Jetta", "Voyage"),
                "Ford" to listOf("Fiesta", "Focus", "Fusion"),
                "Suzuki" to listOf("Swift", "Alto", "Baleno")
            )
            TipoVehiculo.CAMIONETA -> mapOf(
                "Toyota" to listOf("RAV4", "Hilux", "Fortuner", "Prado", "Corolla Cross"),
                "Chevrolet" to listOf("Tracker", "Captiva", "D-Max", "Trailblazer", "Tahoe"),
                "Renault" to listOf("Duster", "Koleos", "Captur", "Oroch"),
                "Mazda" to listOf("CX-30", "CX-5", "CX-50", "CX-9", "BT-50"),
                "Nissan" to listOf("Kicks", "Qashqai", "X-Trail", "Frontier"),
                "Kia" to listOf("Sportage", "Seltos", "Sonet", "Sorento"),
                "Hyundai" to listOf("Tucson", "Creta", "Santa Fe", "Kona"),
                "Ford" to listOf("Ranger", "Explorer", "Escape", "Bronco"),
                "Volkswagen" to listOf("T-Cross", "Nivus", "Taos", "Tiguan", "Amarok"),
                "Jeep" to listOf("Renegade", "Compass", "Grand Cherokee")
            )
            TipoVehiculo.MOTOCICLETA -> mapOf(
                "Yamaha" to listOf("FZ 25", "FZ 150", "NMAX", "Crypton", "YCZ 110", "XTZ 125", "XTZ 250", "MT-03", "R15"),
                "Honda" to listOf("CB 125F", "CB 190R", "XR 150L", "XR 190L", "Wave 110", "Navi", "X-ADV"),
                "Bajaj" to listOf("Pulsar NS 200", "Pulsar NS 160", "Pulsar N250", "Boxer CT 100", "Discover 125", "Dominar 400"),
                "TVS" to listOf("Apache RTR 160", "Apache RTR 200", "Raider 125", "NTORQ 125", "Sport 100"),
                "AKT" to listOf("NKD 125", "CR4 125", "Dynamic Pro 125", "TT Dual Sport 200", "Flex LED 125"),
                "Suzuki" to listOf("Gixxer 150", "Gixxer 250", "GN 125", "AX 100", "V-Strom 250"),
                "KTM" to listOf("Duke 200", "Duke 250", "Duke 390", "RC 200", "Adventure 390"),
                "Hero" to listOf("Eco Deluxe", "Splendor iSmart", "Thriller 160R", "Xpulse 200"),
                "BMW" to listOf("G 310 GS", "F 850 GS", "R 1250 GS"),
                "Kawasaki" to listOf("Ninja 300", "Z400", "Versys 300")
            )
        }

        val lista = modelosMap[marca] ?: emptyList()
        return lista + listOf("Otro (Manual)")
    }
}
