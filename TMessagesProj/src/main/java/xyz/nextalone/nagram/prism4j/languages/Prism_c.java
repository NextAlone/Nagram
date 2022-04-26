package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class Prism_c {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar c = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "clike"),
                "c",
                new GrammarUtils.TokenFilter() {
                    @Override
                    public boolean test(@NonNull Prism4j.Token token) {
                        final String name = token.name();
                        return !"class-name".equals(name) && !"boolean".equals(name);
                    }
                },
                Prism4j.token("keyword", Prism4j.pattern(compile("\\b(?:_Alignas|_Alignof|_Atomic|_Bool|_Complex|_Generic|_Imaginary|_Noreturn|_Static_assert|_Thread_local|asm|typeof|inline|auto|break|case|char|const|continue|default|do|double|else|enum|extern|float|for|goto|if|int|long|register|return|short|signed|sizeof|static|struct|switch|typedef|union|unsigned|void|volatile|while)\\b"))),
                Prism4j.token("operator", Prism4j.pattern(compile("-[>-]?|\\+\\+?|!=?|<<?=?|>>?=?|==?|&&?|\\|\\|?|[~^%?*\\/]"))),
                Prism4j.token("number", Prism4j.pattern(compile("(?:\\b0x[\\da-f]+|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?)[ful]*", CASE_INSENSITIVE)))
        );

        GrammarUtils.insertBeforeToken(c, "string",
                Prism4j.token("macro", Prism4j.pattern(
                        compile("(^\\s*)#\\s*[a-z]+(?:[^\\r\\n\\\\]|\\\\(?:\\r\\n|[\\s\\S]))*", CASE_INSENSITIVE | MULTILINE),
                        true,
                        false,
                        "property",
                        Prism4j.grammar("inside",
                                Prism4j.token("string", Prism4j.pattern(compile("(#\\s*include\\s*)(?:<.+?>|(\"|')(?:\\\\?.)+?\\2)"), true)),
                                Prism4j.token("directive", Prism4j.pattern(
                                        compile("(#\\s*)\\b(?:define|defined|elif|else|endif|error|ifdef|ifndef|if|import|include|line|pragma|undef|using)\\b"),
                                        true,
                                        false,
                                        "keyword"
                                ))
                        )
                )),
                Prism4j.token("constant", Prism4j.pattern(compile("\\b(?:__FILE__|__LINE__|__DATE__|__TIME__|__TIMESTAMP__|__func__|EOF|NULL|SEEK_CUR|SEEK_END|SEEK_SET|stdin|stdout|stderr)\\b")))
        );

        return c;
    }
}
