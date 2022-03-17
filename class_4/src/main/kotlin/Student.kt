import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

data class Student(
    val number: Int,
    val name: String,
    val type: StudentType? = null
)

enum class StudentType {
    Bachelor, Master, Doctoral
}

// saber se um KClassifier é um enumerado
fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)
fun KType?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)

// obter uma lista de constantes de um tipo enumerado
fun <T : Any> KClass<T>.enumConstants(): List<T> {
    require(isEnum()) { "class must be enum" }
    return this.java.enumConstants.toList()
}

fun KType.enumConstants(): Array<out KType>? {
    require(isEnum()) { "class must be enum" }
    return this::class.java.getEnumConstants()
}

fun test() {
    println(StudentType::class.isEnum())
    println(StudentType::class.enumConstants())
}
