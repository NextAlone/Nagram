package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public abstract class Prism_css {

    // todo: really important one..
    // before a language is requested (fro example css)
    // it won't be initialized (so we won't modify markup to highlight css) before it was requested...

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar grammar = Prism4j.grammar(
                "css",
                Prism4j.token("comment", Prism4j.pattern(compile("\\/\\*[\\s\\S]*?\\*\\/"))),
                Prism4j.token(
                        "atrule",
                        Prism4j.pattern(
                                compile("@[\\w-]+?.*?(?:;|(?=\\s*\\{))", CASE_INSENSITIVE),
                                false,
                                false,
                                null,
                                Prism4j.grammar(
                                        "inside",
                                        Prism4j.token("rule", Prism4j.pattern(compile("@[\\w-]+")))
                                )
                        )
                ),
                Prism4j.token(
                        "url",
                        Prism4j.pattern(compile("url\\((?:([\"'])(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1|.*?)\\)", CASE_INSENSITIVE))
                ),
                Prism4j.token("selector", Prism4j.pattern(compile("[^{}\\s][^{};]*?(?=\\s*\\{)"))),
                Prism4j.token(
                        "string",
                        Prism4j.pattern(compile("(\"|')(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true)
                ),
                Prism4j.token(
                        "property",
                        Prism4j.pattern(compile("[-_a-z\\xA0-\\uFFFF][-\\w\\xA0-\\uFFFF]*(?=\\s*:)", CASE_INSENSITIVE))
                ),
                Prism4j.token("important", Prism4j.pattern(compile("\\B!important\\b", CASE_INSENSITIVE))),
                Prism4j.token("function", Prism4j.pattern(compile("[-a-z0-9]+(?=\\()", CASE_INSENSITIVE))),
                Prism4j.token("punctuation", Prism4j.pattern(compile("[(){};:]")))
        );

        // can we maybe add some helper to specify simplified location?

        // now we need to put the all tokens from grammar inside `atrule` (except the `atrule` of cause)
        final Prism4j.Token atrule = grammar.tokens().get(1);
        final Prism4j.Grammar inside = GrammarUtils.findFirstInsideGrammar(atrule);
        if (inside != null) {
            for (Prism4j.Token token : grammar.tokens()) {
                if (!"atrule".equals(token.name())) {
                    inside.tokens().add(token);
                }
            }
        }

        final Prism4j.Grammar markup = prism4j.grammar("markup");
        if (markup != null) {
            GrammarUtils.insertBeforeToken(markup, "tag",
                    Prism4j.token(
                            "style",
                            Prism4j.pattern(
                                    compile("(<style[\\s\\S]*?>)[\\s\\S]*?(?=<\\/style>)", CASE_INSENSITIVE),
                                    true,
                                    true,
                                    "language-css",
                                    grammar
                            )
                    )
            );

            // important thing here is to clone found grammar
            // otherwise we will have stackoverflow (inside tag references style-attr, which
            // references inside tag, etc)
            final Prism4j.Grammar markupTagInside;
            {
                Prism4j.Grammar _temp = null;
                final Prism4j.Token token = GrammarUtils.findToken(markup, "tag");
                if (token != null) {
                    _temp = GrammarUtils.findFirstInsideGrammar(token);
                    if (_temp != null) {
                        _temp = GrammarUtils.clone(_temp);
                    }
                }
                markupTagInside = _temp;
            }

            GrammarUtils.insertBeforeToken(markup, "tag/attr-value",
                    Prism4j.token(
                            "style-attr",
                            Prism4j.pattern(
                                    compile("\\s*style=(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1", CASE_INSENSITIVE),
                                    false,
                                    false,
                                    "language-css",
                                    Prism4j.grammar(
                                            "inside",
                                            Prism4j.token(
                                                    "attr-name",
                                                    Prism4j.pattern(
                                                            compile("^\\s*style", CASE_INSENSITIVE),
                                                            false,
                                                            false,
                                                            null,
                                                            markupTagInside
                                                    )
                                            ),
                                            Prism4j.token("punctuation", Prism4j.pattern(compile("^\\s*=\\s*['\"]|['\"]\\s*$"))),
                                            Prism4j.token(
                                                    "attr-value",
                                                    Prism4j.pattern(
                                                            compile(".+", CASE_INSENSITIVE),
                                                            false,
                                                            false,
                                                            null,
                                                            grammar
                                                    )
                                            )

                                    )
                            )
                    )
            );
        }

        return grammar;
    }

    private Prism_css() {
    }
}
