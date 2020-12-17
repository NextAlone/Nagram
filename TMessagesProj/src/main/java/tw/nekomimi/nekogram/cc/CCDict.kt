package tw.nekomimi.nekogram.cc

import org.telegram.messenger.ApplicationLoader
import java.util.*
import kotlin.collections.HashMap

enum class CCDict {
    HKVariantsRevPhrases,
    HKVariantsRev,
    HKVariants,
    JPShinjitaiCharacters,
    JPShinjitaiPhrases,
    JPVariantsRev,
    JPVariants,
    STCharacters,
    STPhrases,
    TSCharacters,
    TSPhrases,
    TWPhrasesIT,
    TWPhrasesName,
    TWPhrasesOther,
    TWPhrasesRev,
    TWVariantsRevPhrases,
    TWVariantsRev,
    TWVariants;

    lateinit var storage: HashMap<String, LinkedList<String>>
    fun postInit() {
        if (::storage.isInitialized) return
        storage = HashMap()

        ApplicationLoader.applicationContext.assets.open("dictionary/$name.txt").bufferedReader().forEachLine {
            storage[it.substringBefore("\t")] =
                    LinkedList(it.substringAfter("\t").split(" "))
        }
    }
}