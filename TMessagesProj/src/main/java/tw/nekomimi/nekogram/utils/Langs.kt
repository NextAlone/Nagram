package tw.nekomimi.nekogram.utils

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.StrUtil
import java.math.BigInteger
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

/**
 * 一些基于语言特性的全局函数
 */

fun <T> T.applyIf(boolean: Boolean, block: (T.() -> Unit)?): T {
    if (boolean) block?.invoke(this)
    return this
}

fun <T> T.applyIfNot(boolean: Boolean, block: (T.() -> Unit)?): T {
    if (!boolean) block?.invoke(this)
    return this
}

fun String.input(vararg params: Any): String {

    return StrUtil.format(this, *params)

}

val Number.asByteArray get() = BigInteger.valueOf(toLong()).toByteArray()!!

val ByteArray.asLong get() = BigInteger(this).toLong()
val ByteArray.asInt get() = BigInteger(this).toInt()

fun <T> Array<T>.shift(): Array<T> {

    return shift(1)

}

fun <T> Array<T>.shift(size: Int): Array<T> {

    return ArrayUtil.sub(this, size, this.size)

}

fun <T> Collection<T>.shift() = shift(1)

fun <T> Collection<T>.shift(size: Int): Collection<T> {

    return LinkedList(CollUtil.sub(this, size, this.size))

}

class WriteOnlyField<T>(val setter: (T) -> Unit) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = error("WriteOnlyField : ${property.name}")

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {

        setter.invoke(value)

    }

}

class WeakField<T> {

    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
                ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = value
    }

}

fun <T, R> receive(initializer: T.() -> R) = LazyReceiver(initializer)

class LazyReceiver<T, R>(val initializer: T.() -> R) {

    private var isInitialized by AtomicBoolean()
    private var _impl: R? = null

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): R {

        if (isInitialized) return _impl as R

        synchronized(this) {

            if (isInitialized) return _impl as R

            _impl = initializer(thisRef as T)

            isInitialized = true

            return _impl as R

        }

    }

}

operator fun <F> KProperty0<F>.getValue(thisRef: Any?, property: KProperty<*>): F = get()
operator fun <F> KMutableProperty0<F>.setValue(thisRef: Any?, property: KProperty<*>, value: F) = set(value)

operator fun AtomicBoolean.getValue(thisRef: Any?, property: KProperty<*>): Boolean = get()
operator fun AtomicBoolean.setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) = set(value)

operator fun AtomicInteger.getValue(thisRef: Any?, property: KProperty<*>): Int = get()
operator fun AtomicInteger.setValue(thisRef: Any?, property: KProperty<*>, value: Int) = set(value)

operator fun AtomicLong.getValue(thisRef: Any?, property: KProperty<*>): Long = get()
operator fun AtomicLong.setValue(thisRef: Any?, property: KProperty<*>, value: Long) = set(value)

operator fun <T> AtomicReference<T>.getValue(thisRef: Any?, property: KProperty<*>): T = get()
operator fun <T> AtomicReference<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)