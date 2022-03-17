import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

class Point(val x: Int, val y: Int) {
    constructor() : this(0, 0)

    val isOrigin: Boolean get() = x == 0 && y == 0

    fun moveDown() = Point(x, y + 1)
    fun moveRight() = Point(x + 1, y)
    fun sum(x: Int, y: Int) = Point(this.x + x, this.y + y)
    override fun toString(): String {
        return "Point(x=$x, y=$y)"
    }

}

fun main(args: Array<String>) {
    reflectionSample()
    ex1()
    println(sample2())
    val point = Point(123,4)
    println(createInsert(point))
}

private fun sample2(): Point? {
    val point = Point(2,3)
    val clazz = point::class
    val cons = clazz.constructors.find { it.parameters.size == 2 }
    val o = cons?.call(1, 2)
    val propNames = clazz.declaredMemberProperties.map { it.name + " : " + it.call(point) }
    println(propNames)
    return o
}

private fun ex1() {
    val clazz = Student::class
    val sql: String = createTable(clazz)
    println(sql)
}

fun createTable(clazz: KClass<Student>): String {
    val stringJoiner = StringJoiner(",", "CREATE TABLE " + clazz.simpleName + " ( ", ");" )
    clazz.declaredMemberProperties.map { matchType(it)}.forEach(stringJoiner::add)
    return stringJoiner.toString()
}

fun createInsert(value: Any): String {
    val clazz = value::class
    val stringBuilder = StringBuilder("INSERT INTO " + clazz.simpleName + " (" )
    stringBuilder.append(clazz.declaredMemberProperties.map { it.name}.joinToString { it })
    stringBuilder.append(" ) VALUES (")
    stringBuilder.append(clazz.declaredMemberProperties.map { "'" + it.call(value) + "'"}.joinToString { it })
    stringBuilder.append(" )")
    return stringBuilder.toString()
}

fun matchType(it: KProperty1<*, *>): String {
    val name = it.name
    val typeVal = it.returnType
    val s = when (typeVal) {
        Int::class.createType() -> "NUMBER"
        String::class.createType() -> "VARCHAR(255)"
        else -> if(typeVal.classifier.isEnum()) { "ENUM(" + (typeVal.classifier as KClass<*>).enumConstants().joinToString(transform = { "'$it'" }) + ")" } else { "NA" }
    }
    return "$name $s"
}

private fun reflectionSample() {
    val clazz: KClass<*> = Point::class
    println(clazz.simpleName)
    val propNames = clazz.declaredMemberFunctions.map { it.name }
//    val funNoParams = clazz.declaredMemberFunctions.filter {it.valueParameters.isEmpty()} // moveDown(), moveRight()
    println(propNames)
}