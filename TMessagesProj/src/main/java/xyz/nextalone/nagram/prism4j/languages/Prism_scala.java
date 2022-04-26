package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_scala {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        final Prism4j.Grammar scala = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "java"),
                "scala",
                new GrammarUtils.TokenFilter() {
                    @Override
                    public boolean test(@NonNull Prism4j.Token token) {
                        final String name = token.name();
                        return !"class-name".equals(name) && !"function".equals(name);
                    }
                },
                Prism4j.token("keyword", Prism4j.pattern(
                        compile("<-|=>|\\b(?:abstract|case|catch|class|def|do|else|extends|final|finally|for|forSome|if|implicit|import|lazy|match|new|null|object|override|package|private|protected|return|sealed|self|super|this|throw|trait|try|type|val|var|while|with|yield)\\b")
                )),
                Prism4j.token("string",
                        Prism4j.pattern(compile("\"\"\"[\\s\\S]*?\"\"\""), false, true),
                        Prism4j.pattern(compile("(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
                ),
                Prism4j.token("number", Prism4j.pattern(
                        compile("\\b0x[\\da-f]*\\.?[\\da-f]+|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e\\d+)?[dfl]?", CASE_INSENSITIVE)
                ))
        );

        scala.tokens().add(
                Prism4j.token("symbol", Prism4j.pattern(compile("'[^\\d\\s\\\\]\\w*")))
        );

        GrammarUtils.insertBeforeToken(scala, "number",
                Prism4j.token("builtin", Prism4j.pattern(compile("\\b(?:String|Int|Long|Short|Byte|Boolean|Double|Float|Char|Any|AnyRef|AnyVal|Unit|Nothing)\\b")))
        );

        return scala;
    }
}
