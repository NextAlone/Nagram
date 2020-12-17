@file:Suppress("unused")

package tw.nekomimi.nekogram.cc

object OpenCCUtil {

    fun segLongest(charArray: CharArray, trie: AhoCorasickDoubleArrayTrie<String>): String {
        val wordNet = arrayOfNulls<String>(charArray.size)
        val lengthNet = IntArray(charArray.size)
        trie.parseText(charArray) { begin, end, value ->
            val length = end - begin
            if (length > lengthNet[begin]) {
                wordNet[begin] = value
                lengthNet[begin] = length
            }
        }
        val sb = StringBuilder(charArray.size)
        var offset = 0
        while (offset < wordNet.size) {
            if (wordNet[offset] == null) {
                sb.append(charArray[offset])
                ++offset
                continue
            }
            sb.append(wordNet[offset])
            offset += lengthNet[offset]
        }
        return sb.toString()
    }

}