package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class Prism_markdown {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar markdown = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "markup"),
                "markdown"
        );

        final Prism4j.Token bold = Prism4j.token("bold", Prism4j.pattern(
                compile("(^|[^\\\\])(\\*\\*|__)(?:(?:\\r?\\n|\\r)(?!\\r?\\n|\\r)|.)+?\\2"),
                true,
                false,
                null,
                Prism4j.grammar("inside", Prism4j.token("punctuation", Prism4j.pattern(compile("^\\*\\*|^__|\\*\\*$|__$"))))
        ));

        final Prism4j.Token italic = Prism4j.token("italic", Prism4j.pattern(
                compile("(^|[^\\\\])([*_])(?:(?:\\r?\\n|\\r)(?!\\r?\\n|\\r)|.)+?\\2"),
                true,
                false,
                null,
                Prism4j.grammar("inside", Prism4j.token("punctuation", Prism4j.pattern(compile("^[*_]|[*_]$"))))
        ));

        final Prism4j.Token url = Prism4j.token("url", Prism4j.pattern(
                compile("!?\\[[^\\]]+\\](?:\\([^\\s)]+(?:[\\t ]+\"(?:\\\\.|[^\"\\\\])*\")?\\)| ?\\[[^\\]\\n]*\\])"),
                false,
                false,
                null,
                Prism4j.grammar("inside",
                        Prism4j.token("variable", Prism4j.pattern(compile("(!?\\[)[^\\]]+(?=\\]$)"), true)),
                        Prism4j.token("string", Prism4j.pattern(compile("\"(?:\\\\.|[^\"\\\\])*\"(?=\\)$)")))
                )
        ));

        GrammarUtils.insertBeforeToken(markdown, "prolog",
                Prism4j.token("blockquote", Prism4j.pattern(compile("^>(?:[\\t ]*>)*", MULTILINE))),
                Prism4j.token("code",
                        Prism4j.pattern(compile("^(?: {4}|\\t).+", MULTILINE), false, false, "keyword"),
                        Prism4j.pattern(compile("``.+?``|`[^`\\n]+`"), false, false, "keyword")
                ),
                Prism4j.token(
                        "title",
                        Prism4j.pattern(
                                compile("\\w+.*(?:\\r?\\n|\\r)(?:==+|--+)"),
                                false,
                                false,
                                "important",
                                Prism4j.grammar("inside", Prism4j.token("punctuation", Prism4j.pattern(compile("==+$|--+$"))))
                        ),
                        Prism4j.pattern(
                                compile("(^\\s*)#+.+", MULTILINE),
                                true,
                                false,
                                "important",
                                Prism4j.grammar("inside", Prism4j.token("punctuation", Prism4j.pattern(compile("^#+|#+$"))))
                        )
                ),
                Prism4j.token("hr", Prism4j.pattern(
                        compile("(^\\s*)([*-])(?:[\\t ]*\\2){2,}(?=\\s*$)", MULTILINE),
                        true,
                        false,
                        "punctuation"
                )),
                Prism4j.token("list", Prism4j.pattern(
                        compile("(^\\s*)(?:[*+-]|\\d+\\.)(?=[\\t ].)", MULTILINE),
                        true,
                        false,
                        "punctuation"
                )),
                Prism4j.token("url-reference", Prism4j.pattern(
                        compile("!?\\[[^\\]]+\\]:[\\t ]+(?:\\S+|<(?:\\\\.|[^>\\\\])+>)(?:[\\t ]+(?:\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|\\((?:\\\\.|[^)\\\\])*\\)))?"),
                        false,
                        false,
                        "url",
                        Prism4j.grammar("inside",
                                Prism4j.token("variable", Prism4j.pattern(compile("^(!?\\[)[^\\]]+"), true)),
                                Prism4j.token("string", Prism4j.pattern(compile("(?:\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|\\((?:\\\\.|[^)\\\\])*\\))$"))),
                                Prism4j.token("punctuation", Prism4j.pattern(compile("^[\\[\\]!:]|[<>]")))
                        )
                )),
                bold,
                italic,
                url
        );

        add(GrammarUtils.findFirstInsideGrammar(bold), url, italic);
        add(GrammarUtils.findFirstInsideGrammar(italic), url, bold);

        return markdown;
    }

    private static void add(@Nullable Prism4j.Grammar grammar, @NonNull Prism4j.Token first, @NonNull Prism4j.Token second) {
        if (grammar != null) {
            grammar.tokens().add(first);
            grammar.tokens().add(second);
        }
    }
}
