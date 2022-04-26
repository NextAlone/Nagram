package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.compile;

public abstract class Prism_clike {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        return Prism4j.grammar(
                "clike",
                Prism4j.token(
                        "comment",
                        Prism4j.pattern(compile("(^|[^\\\\])\\/\\*[\\s\\S]*?(?:\\*\\/|$)"), true),
                        Prism4j.pattern(compile("(^|[^\\\\:])\\/\\/.*"), true, true)
                ),
                Prism4j.token(
                        "string",
                        Prism4j.pattern(compile("([\"'])(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
                ),
                Prism4j.token(
                        "class-name",
                        Prism4j.pattern(
                                compile("((?:\\b(?:class|interface|extends|implements|trait|instanceof|new)\\s+)|(?:catch\\s+\\())[\\w.\\\\]+"),
                                true,
                                false,
                                null,
                                Prism4j.grammar("inside", Prism4j.token("punctuation", Prism4j.pattern(compile("[.\\\\]"))))
                        )
                ),
                Prism4j.token(
                        "keyword",
                        Prism4j.pattern(compile("\\b(?:if|else|while|do|for|return|in|instanceof|function|new|try|throw|catch|finally|null|break|continue)\\b"))
                ),
                Prism4j.token("boolean", Prism4j.pattern(compile("\\b(?:true|false)\\b"))),
                Prism4j.token("function", Prism4j.pattern(compile("[a-z0-9_]+(?=\\()", Pattern.CASE_INSENSITIVE))),
                Prism4j.token(
                        "number",
                        Prism4j.pattern(compile("\\b0x[\\da-f]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:e[+-]?\\d+)?", Pattern.CASE_INSENSITIVE))
                ),
                Prism4j.token("operator", Prism4j.pattern(compile("--?|\\+\\+?|!=?=?|<=?|>=?|==?=?|&&?|\\|\\|?|\\?|\\*|\\/|~|\\^|%"))),
                Prism4j.token("punctuation", Prism4j.pattern(compile("[{}\\[\\];(),.:]")))
        );
    }

    private Prism_clike() {
    }
}
