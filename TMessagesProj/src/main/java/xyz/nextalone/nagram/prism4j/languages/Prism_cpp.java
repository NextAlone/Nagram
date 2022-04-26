package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_cpp {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar cpp = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "c"),
                "cpp",
                Prism4j.token("keyword", Prism4j.pattern(compile("\\b(?:alignas|alignof|asm|auto|bool|break|case|catch|char|char16_t|char32_t|class|compl|const|constexpr|const_cast|continue|decltype|default|delete|do|double|dynamic_cast|else|enum|explicit|export|extern|float|for|friend|goto|if|inline|int|int8_t|int16_t|int32_t|int64_t|uint8_t|uint16_t|uint32_t|uint64_t|long|mutable|namespace|new|noexcept|nullptr|operator|private|protected|public|register|reinterpret_cast|return|short|signed|sizeof|static|static_assert|static_cast|struct|switch|template|this|thread_local|throw|try|typedef|typeid|typename|union|unsigned|using|virtual|void|volatile|wchar_t|while)\\b"))),
                Prism4j.token("operator", Prism4j.pattern(compile("--?|\\+\\+?|!=?|<{1,2}=?|>{1,2}=?|->|:{1,2}|={1,2}|\\^|~|%|&{1,2}|\\|\\|?|\\?|\\*|\\/|\\b(?:and|and_eq|bitand|bitor|not|not_eq|or|or_eq|xor|xor_eq)\\b")))
        );

        // in prism-js cpp is extending c, but c has not booleans... (like classes)
        GrammarUtils.insertBeforeToken(cpp, "function",
                Prism4j.token("boolean", Prism4j.pattern(compile("\\b(?:true|false)\\b")))
        );

        GrammarUtils.insertBeforeToken(cpp, "keyword",
                Prism4j.token("class-name", Prism4j.pattern(compile("(class\\s+)\\w+", CASE_INSENSITIVE), true))
        );

        GrammarUtils.insertBeforeToken(cpp, "string",
                Prism4j.token("raw-string", Prism4j.pattern(compile("R\"([^()\\\\ ]{0,16})\\([\\s\\S]*?\\)\\1\""), false, true, "string"))
        );

        return cpp;
    }
}
