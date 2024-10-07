package tw.nekomimi.nekogram.parts

private val mapArr = mapOf(
    '（' to '(',
    '）' to ')',
    '。' to '.',
    '，' to ',',
    '？' to '?',
    '；' to ';'
)

fun filter(input: String) = input.toCharArray().let { c ->
    c.flatMapIndexed { index, char ->
        if (char in mapArr) {
            if (c.size - index > 1 && c[index + 1] != ' ') {
                listOf(mapArr[char], ' ')
            } else {
                listOf(mapArr[char])
            }
        } else {
            listOf(char)
        }
    }.filterNotNull().toCharArray().let(::String)
}
