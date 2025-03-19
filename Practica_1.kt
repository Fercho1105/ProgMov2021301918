import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    var opcion: Int

    do {
        println("\n--- Menú ---")
        println("1. Sumar tres números")
        println("2. Ingresar nombre completo")
        println("3. Calcular tiempo vivido")
        println("4. Salir")
        print("Seleccione una opción: ")

        opcion = scanner.nextInt()
        scanner.nextLine()  // Limpiar buffer

        when (opcion) {
            1 -> sumarTresNumeros(scanner)
            2 -> ingresarNombre(scanner)
            3 -> calcularTiempoVivido(scanner)
            4 -> println("Saliendo del programa...")
            else -> println("Opción no válida. Intente de nuevo.")
        }
    } while (opcion != 4)
}

fun sumarTresNumeros(scanner: Scanner) {
    print("Ingrese el primer número: ")
    val num1 = scanner.nextDouble()
    print("Ingrese el segundo número: ")
    val num2 = scanner.nextDouble()
    print("Ingrese el tercer número: ")
    val num3 = scanner.nextDouble()

    val suma = num1 + num2 + num3
    println("La suma de los tres números es: $suma")
}

fun ingresarNombre(scanner: Scanner) {
    print("Ingrese su nombre completo: ")
    val nombre = scanner.nextLine()
    println("Hola, $nombre!")
}

fun calcularTiempoVivido(scanner: Scanner) {
    print("Ingrese su fecha de nacimiento (YYYY-MM-DD): ")
    val fechaNacimiento = LocalDate.parse(scanner.nextLine())
    val fechaActual = LocalDate.now()
    val periodo = Period.between(fechaNacimiento, fechaActual)
    val diasTotales = ChronoUnit.DAYS.between(fechaNacimiento, fechaActual)
    val horas = diasTotales * 24
    val minutos = horas * 60
    val segundos = minutos * 60

    println("Has vivido aproximadamente:")
    println("- ${periodo.years} años, ${periodo.months} meses y ${periodo.days} días")
    println("- Total en meses: ${periodo.years * 12 + periodo.months}")
    println("- Total en semanas: ${diasTotales / 7}")
    println("- Total en días: $diasTotales")
    println("- Total en horas: $horas")
    println("- Total en minutos: $minutos")
    println("- Total en segundos: $segundos")
}