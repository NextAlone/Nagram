package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_go {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar go = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "clike"),
                "go",
                new GrammarUtils.TokenFilter() {
                    @Override
                    public boolean test(@NonNull Prism4j.Token token) {
                        return !"class-name".equals(token.name());
                    }
                },
                Prism4j.token("keyword", Prism4j.pattern(compile("\\b(?:break|case|chan|const|continue|default|defer|else|fallthrough|for|func|go(?:to)?|if|import|interface|map|package|range|return|select|struct|switch|type|var)\\b"))),
                Prism4j.token("boolean", Prism4j.pattern(compile("\\b(?:_|iota|nil|true|false)\\b"))),
                Prism4j.token("operator", Prism4j.pattern(compile("[*\\/%^!=]=?|\\+[=+]?|-[=-]?|\\|[=|]?|&(?:=|&|\\^=?)?|>(?:>=?|=)?|<(?:<=?|=|-)?|:=|\\.\\.\\."))),
                Prism4j.token("number", Prism4j.pattern(compile("(?:\\b0x[a-f\\d]+|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[-+]?\\d+)?)i?", CASE_INSENSITIVE))),
                Prism4j.token("string", Prism4j.pattern(
                        compile("([\"'`])(\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1"),
                        false,
                        true
                ))
        );

        // clike doesn't have builtin
        GrammarUtils.insertBeforeToken(go, "boolean",
                Prism4j.token("builtin", Prism4j.pattern(compile("\\b(?:bool|byte|complex(?:64|128)|error|float(?:32|64)|rune|string|u?int(?:8|16|32|64)?|uintptr|append|cap|close|complex|copy|delete|imag|len|make|new|panic|print(?:ln)?|real|recover)\\b")))
        );

        return go;
    }
}
