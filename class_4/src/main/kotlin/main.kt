import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

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
    val point = Point(123, 4)
    println(createInsert(point))
    println(createTable2(Student2::class))
}

private fun sample2(): Point? {
    val point = Point(2, 3)
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
    val stringJoiner = StringJoiner(",", "CREATE TABLE " + clazz.simpleName + " ( ", ");")
    clazz.declaredMemberProperties.map { matchType(it) }.forEach(stringJoiner::add)
    return stringJoiner.toString()
}

fun createInsert(value: Any): String {
    val clazz = value::class
    val stringBuilder = StringBuilder("INSERT INTO " + clazz.simpleName + " (")
    stringBuilder.append(clazz.declaredMemberProperties.map { it.name }.joinToString { it })
    stringBuilder.append(" ) VALUES (")
    stringBuilder.append(clazz.declaredMemberProperties.map { "'" + it.call(value) + "'" }.joinToString { it })
    stringBuilder.append(" )")
    return stringBuilder.toString()
}

fun matchType(it: KProperty1<*, *>): String {
    val name = it.name
    val typeVal = it.returnType
    val s = when (typeVal) {
        Int::class.createType() -> "NUMBER"
        String::class.createType() -> "VARCHAR(255)"
        else -> if (typeVal.classifier.isEnum()) {
            "ENUM(" + (typeVal.classifier as KClass<*>).enumConstants().joinToString(transform = { "'$it'" }) + ")"
        } else {
            "NA"
        }
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

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class DbName(val id: String)

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class PrimaryKey

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class Length(val size: Int)

@DbName("STUDENT2")
data class Student2(
    @PrimaryKey
    val number: Int,
    @Length(50)
    val name: String,
    @DbName("degree2")
    val type: StudentType
)

fun createTable2(clazz: KClass<Student2>): String {
    val annotated = clazz.findAnnotation<DbName>()
    val nameTable = if (annotated?.id == null) {
        clazz.simpleName
    } else annotated.id
    val stringJoiner = StringJoiner(",", "CREATE TABLE $nameTable ( ", ");")
    clazz.declaredMemberProperties.map { matchType2(it) }.forEach(stringJoiner::add)
    return stringJoiner.toString()
}

fun createInsert2(value: Any): String {
    val clazz = value::class
    val stringBuilder = StringBuilder("INSERT INTO " + clazz.simpleName + " (")
    stringBuilder.append(clazz.declaredMemberProperties.map { it.name }.joinToString { it })
    stringBuilder.append(" ) VALUES (")
    stringBuilder.append(clazz.declaredMemberProperties.map { "'" + it.call(value) + "'" }.joinToString { it })
    stringBuilder.append(" )")
    return stringBuilder.toString()
}

fun matchType2(it: KProperty1<*, *>): String {
    val name = getPropName(it)
    val s = when (val typeVal = it.returnType) {
        Int::class.createType() -> "NUMBER" + if(getPropSize(it, 0) == 0) "" else "("+getPropSize(it, 0) + ")"
        String::class.createType() -> "VARCHAR(" + getPropSize(it, 255) + ")"
        else -> if (typeVal.classifier.isEnum()) {
            "ENUM(" + (typeVal.classifier as KClass<*>).enumConstants().joinToString(transform = { "'$it'" }) + ")"
        } else {
            "NA"
        }
    }
    val propPk = getPropPk(it)
    return "$name $s $propPk"
}

private fun getPropName(it: KProperty1<*, *>): String {
    val annotated = it.findAnnotation<DbName>()
    return if (annotated?.id == null) {
        it.name
    } else annotated.id
}

private fun getPropPk(it: KProperty1<*, *>): String {
    val annotated = it.findAnnotation<PrimaryKey>()
    return if (annotated == null) {
        ""
    } else "PRIMARY KEY"
}

private fun getPropSize(it: KProperty1<*, *>, default: Int = 255): Int {
    val annotated = it.findAnnotation<Length>()
    return if (annotated?.size == null) {
        default
    } else annotated.size
}

