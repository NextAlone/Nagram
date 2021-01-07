package tw.nekomimi.nekogram.cc

import java.util.*
import kotlin.collections.HashMap

class CCConverter(val target: CCTarget) {

    companion object {

        val instances = HashMap<CCTarget, CCConverter>()

        @JvmStatic
        fun get(target: CCTarget): CCConverter {
            return instances[target] ?: synchronized(instances) {
                instances[target] ?: CCConverter(target).also {
                    instances[target] = it
                }
            }
        }

    }

    val tries = LinkedList<AhoCorasickDoubleArrayTrie<String>>()

    private fun addTrie(dicts: Array<CCDict>, revDicts: Array<CCDict> = arrayOf()) {
        val storage = TreeMap<String, LinkedList<String>>()
        for (dictionary in dicts) {
            dictionary.postInit()
            storage.putAll(dictionary.storage)
        }
        for (dictionary in revDicts) {
            dictionary.postInit()
            val revMap = HashMap<String, LinkedList<String>>()
            dictionary.storage.forEach { (key, value) ->
                value.forEach {
                    revMap.getOrPut(it) { LinkedList() }.add(key)
                }
            }
            storage.putAll(revMap)
        }
        tries.add(AhoCorasickDoubleArrayTrie<String>().apply {
            build(TreeMap(mapOf(* storage.mapNotNull { it.key to it.value[0] }.toTypedArray())))
        })
    }

    init {

        when (target) {
            CCTarget.SC -> {
                addTrie(arrayOf(
                        CCDict.JPShinjitaiCharacters,
                        CCDict.JPVariantsRev
                ))
                addTrie(arrayOf(
                        CCDict.TSCharacters
                ))
            }
            CCTarget.SP -> {
                addTrie(arrayOf(
                        CCDict.JPShinjitaiCharacters,
                        CCDict.JPShinjitaiPhrases,
                        CCDict.JPVariantsRev,
                        CCDict.HKVariantsRev,
                        CCDict.HKVariantsRevPhrases,
                        CCDict.TWVariantsRev,
                        CCDict.TWPhrasesRev,
                        CCDict.TWVariantsRevPhrases
                ))
                addTrie(arrayOf(
                        CCDict.TSCharacters,
                        CCDict.TSPhrases
                ))
            }
            CCTarget.TC -> {
                addTrie(arrayOf(
                        CCDict.JPShinjitaiCharacters,
                        CCDict.JPVariantsRev,
                        CCDict.STCharacters,
                        CCDict.STPhrases
                ))
            }
            CCTarget.TT -> {
                addTrie(arrayOf(
                        CCDict.JPShinjitaiCharacters,
                        CCDict.JPShinjitaiPhrases,
                        CCDict.JPVariantsRev,
                        CCDict.STCharacters,
                        CCDict.STPhrases
                ))
                addTrie(arrayOf(
                        CCDict.TWVariants,
                        CCDict.TWPhrasesIT,
                        CCDict.TWPhrasesName,
                        CCDict.TWPhrasesOther
                ))
            }
            CCTarget.HK -> {
                addTrie(arrayOf(
                        CCDict.JPShinjitaiCharacters,
                        CCDict.JPShinjitaiPhrases,
                        CCDict.JPVariantsRev,
                        CCDict.STCharacters,
                        CCDict.STPhrases
                ))
                addTrie(arrayOf(
                        CCDict.HKVariants
                ))
            }
            CCTarget.JP -> {
                addTrie(arrayOf(
                        CCDict.STCharacters,
                        CCDict.STPhrases
                ))
                addTrie(arrayOf(
                        CCDict.JPVariants,
                        CCDict.JPShinjitaiCharacters,
                        CCDict.JPShinjitaiPhrases
                ))
            }
        }
    }

    fun convert(input: String): String {
        var result = input
        for (trie in tries) {
            result = OpenCCUtil.segLongest(result.toCharArray(), trie)
        }
        return result
    }

}