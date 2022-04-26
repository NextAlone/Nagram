package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class Prism_java {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Token keyword = Prism4j.token("keyword", Prism4j.pattern(compile("\\b(?:abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\b")));

        final Prism4j.Grammar java = GrammarUtils.extend(GrammarUtils.require(prism4j, "clike"), "java",
                keyword,
                Prism4j.token("number", Prism4j.pattern(compile("\\b0b[01]+\\b|\\b0x[\\da-f]*\\.?[\\da-fp-]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?[df]?", CASE_INSENSITIVE))),
                Prism4j.token("operator", Prism4j.pattern(
                        compile("(^|[^.])(?:\\+[+=]?|-[-=]?|!=?|<<?=?|>>?>?=?|==?|&[&=]?|\\|[|=]?|\\*=?|\\/=?|%=?|\\^=?|[?:~])", MULTILINE),
                        true
                ))
        );

        GrammarUtils.insertBeforeToken(java, "function",
                Prism4j.token("annotation", Prism4j.pattern(
                        compile("(^|[^.])@\\w+"),
                        true,
                        false,
                        "punctuation"
                ))
        );

        GrammarUtils.insertBeforeToken(java, "class-name",
                Prism4j.token("generics", Prism4j.pattern(
                        compile("<\\s*\\w+(?:\\.\\w+)?(?:\\s*,\\s*\\w+(?:\\.\\w+)?)*>", CASE_INSENSITIVE),
                        false,
                        false,
                        "function",
                        Prism4j.grammar(
                                "inside",
                                keyword,
                                Prism4j.token("punctuation", Prism4j.pattern(compile("[<>(),.:]")))
                        )
                ))
        );

        return java;
    }
}
