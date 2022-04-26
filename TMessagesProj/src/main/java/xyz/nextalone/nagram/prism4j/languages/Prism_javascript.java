package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_javascript {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar js = GrammarUtils.extend(GrammarUtils.require(prism4j, "clike"), "javascript",
                Prism4j.token("keyword", Prism4j.pattern(compile("\\b(?:as|async|await|break|case|catch|class|const|continue|debugger|default|delete|do|else|enum|export|extends|finally|for|from|function|get|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|set|static|super|switch|this|throw|try|typeof|var|void|while|with|yield)\\b"))),
                Prism4j.token("number", Prism4j.pattern(compile("\\b(?:0[xX][\\dA-Fa-f]+|0[bB][01]+|0[oO][0-7]+|NaN|Infinity)\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:[Ee][+-]?\\d+)?"))),
                Prism4j.token("function", Prism4j.pattern(compile("[_$a-z\\xA0-\\uFFFF][$\\w\\xA0-\\uFFFF]*(?=\\s*\\()", CASE_INSENSITIVE))),
                Prism4j.token("operator", Prism4j.pattern(compile("-[-=]?|\\+[+=]?|!=?=?|<<?=?|>>?>?=?|=(?:==?|>)?|&[&=]?|\\|[|=]?|\\*\\*?=?|\\/=?|~|\\^=?|%=?|\\?|\\.{3}")))
        );

        GrammarUtils.insertBeforeToken(js, "keyword",
                Prism4j.token("regex", Prism4j.pattern(
                        compile("((?:^|[^$\\w\\xA0-\\uFFFF.\"'\\])\\s])\\s*)\\/(\\[[^\\]\\r\\n]+]|\\\\.|[^/\\\\\\[\\r\\n])+\\/[gimyu]{0,5}(?=\\s*($|[\\r\\n,.;})\\]]))"),
                        true,
                        true
                )),
                Prism4j.token(
                        "function-variable",
                        Prism4j.pattern(
                                compile("[_$a-z\\xA0-\\uFFFF][$\\w\\xA0-\\uFFFF]*(?=\\s*=\\s*(?:function\\b|(?:\\([^()]*\\)|[_$a-z\\xA0-\\uFFFF][$\\w\\xA0-\\uFFFF]*)\\s*=>))", CASE_INSENSITIVE),
                                false,
                                false,
                                "function"
                        )
                ),
                Prism4j.token("constant", Prism4j.pattern(compile("\\b[A-Z][A-Z\\d_]*\\b")))
        );

        final Prism4j.Token interpolation = Prism4j.token("interpolation");

        GrammarUtils.insertBeforeToken(js, "string",
                Prism4j.token(
                        "template-string",
                        Prism4j.pattern(
                                compile("`(?:\\\\[\\s\\S]|\\$\\{[^}]+\\}|[^\\\\`])*`"),
                                false,
                                true,
                                null,
                                Prism4j.grammar(
                                        "inside",
                                        interpolation,
                                        Prism4j.token("string", Prism4j.pattern(compile("[\\s\\S]+")))
                                )
                        )
                )
        );

        final Prism4j.Grammar insideInterpolation;
        {
            final List<Prism4j.Token> tokens = new ArrayList<>(js.tokens().size() + 1);
            tokens.add(Prism4j.token(
                    "interpolation-punctuation",
                    Prism4j.pattern(compile("^\\$\\{|\\}$"), false, false, "punctuation")
            ));
            tokens.addAll(js.tokens());
            insideInterpolation = Prism4j.grammar("inside", tokens);
        }

        interpolation.patterns().add(Prism4j.pattern(
                compile("\\$\\{[^}]+\\}"),
                false,
                false,
                null,
                insideInterpolation
        ));

        final Prism4j.Grammar markup = prism4j.grammar("markup");
        if (markup != null) {
            GrammarUtils.insertBeforeToken(markup, "tag",
                    Prism4j.token(
                            "script", Prism4j.pattern(
                                    compile("(<script[\\s\\S]*?>)[\\s\\S]*?(?=<\\/script>)", CASE_INSENSITIVE),
                                    true,
                                    true,
                                    "language-javascript",
                                    js
                            )
                    )
            );
        }

        return js;
    }
}
