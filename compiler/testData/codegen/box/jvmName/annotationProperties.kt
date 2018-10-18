// TARGET_BACKEND: JVM
// IGNORE_BACKEND: JVM_IR
// WITH_REFLECT

import kotlin.test.assertEquals
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaGetter

annotation class Anno(@get:JvmName("uglyJvmName") val value: String)
annotation class Meta(val anno: Anno)

@Meta(Anno(value = "OK"))
fun bar() {}


fun box(): String {
    val property = Anno::class.declaredMemberProperties.single()
    assertEquals("value", property.name)
    assertEquals("uglyJvmName", property.javaGetter!!.name)

    val b = ::bar.annotations.single() as Meta

    return b.anno.value
}
