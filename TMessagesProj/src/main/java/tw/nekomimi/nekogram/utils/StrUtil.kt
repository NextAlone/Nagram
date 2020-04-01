package tw.nekomimi.nekogram.utils

object StrUtil {

    @JvmStatic
    fun isBlank(string: CharSequence?) = string?.isBlank() ?: true

}