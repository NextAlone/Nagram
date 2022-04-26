package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.compile;

public class Prism_dart {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar dart = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "clike"),
                "dart",
                Prism4j.token("string",
                        Prism4j.pattern(compile("r?(\"\"\"|''')[\\s\\S]*?\\1"), false, true),
                        Prism4j.pattern(compile("r?(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
                ),
                Prism4j.token("keyword",
                        Prism4j.pattern(compile("\\b(?:async|sync|yield)\\*")),
                        Prism4j.pattern(compile("\\b(?:abstract|assert|async|await|break|case|catch|class|const|continue|default|deferred|do|dynamic|else|enum|export|external|extends|factory|final|finally|for|get|if|implements|import|in|library|new|null|operator|part|rethrow|return|set|static|super|switch|this|throw|try|typedef|var|void|while|with|yield)\\b"))
                ),
                Prism4j.token("operator", Prism4j.pattern(compile("\\bis!|\\b(?:as|is)\\b|\\+\\+|--|&&|\\|\\||<<=?|>>=?|~(?:\\/=?)?|[+\\-*\\/%&^|=!<>]=?|\\?")))
        );

        GrammarUtils.insertBeforeToken(dart, "function",
                Prism4j.token("metadata", Prism4j.pattern(compile("@\\w+"), false, false, "symbol"))
        );

        return dart;
    }
}
