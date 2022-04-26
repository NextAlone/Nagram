package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.compile;

public class Prism_kotlin {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar kotlin = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "clike"),
                "kotlin",
                new GrammarUtils.TokenFilter() {
                    @Override
                    public boolean test(@NonNull Prism4j.Token token) {
                        return !"class-name".equals(token.name());
                    }
                },
                Prism4j.token(
                        "keyword",
                        Prism4j.pattern(compile("(^|[^.])\\b(?:abstract|actual|annotation|as|break|by|catch|class|companion|const|constructor|continue|crossinline|data|do|dynamic|else|enum|expect|external|final|finally|for|fun|get|if|import|in|infix|init|inline|inner|interface|internal|is|lateinit|noinline|null|object|open|operator|out|override|package|private|protected|public|reified|return|sealed|set|super|suspend|tailrec|this|throw|to|try|typealias|val|var|vararg|when|where|while)\\b"), true)
                ),
                Prism4j.token(
                        "function",
                        Prism4j.pattern(compile("\\w+(?=\\s*\\()")),
                        Prism4j.pattern(compile("(\\.)\\w+(?=\\s*\\{)"), true)
                ),
                Prism4j.token(
                        "number",
                        Prism4j.pattern(compile("\\b(?:0[xX][\\da-fA-F]+(?:_[\\da-fA-F]+)*|0[bB][01]+(?:_[01]+)*|\\d+(?:_\\d+)*(?:\\.\\d+(?:_\\d+)*)?(?:[eE][+-]?\\d+(?:_\\d+)*)?[fFL]?)\\b"))
                ),
                Prism4j.token(
                        "operator",
                        Prism4j.pattern(compile("\\+[+=]?|-[-=>]?|==?=?|!(?:!|==?)?|[\\/*%<>]=?|[?:]:?|\\.\\.|&&|\\|\\||\\b(?:and|inv|or|shl|shr|ushr|xor)\\b"))
                )
        );

        GrammarUtils.insertBeforeToken(kotlin, "string",
                Prism4j.token("raw-string", Prism4j.pattern(compile("(\"\"\"|''')[\\s\\S]*?\\1"), false, false, "string"))
        );

        GrammarUtils.insertBeforeToken(kotlin, "keyword",
                Prism4j.token("annotation", Prism4j.pattern(compile("\\B@(?:\\w+:)?(?:[A-Z]\\w*|\\[[^\\]]+\\])"), false, false, "builtin"))
        );

        GrammarUtils.insertBeforeToken(kotlin, "function",
                Prism4j.token("label", Prism4j.pattern(compile("\\w+@|@\\w+"), false, false, "symbol"))
        );

        // this grammar has 1 token: interpolation, which has 2 patterns
        final Prism4j.Grammar interpolationInside;
        {

            // okay, I was cloning the tokens of kotlin grammar (so there is no recursive chain of calls),
            // but it looks like it wants to have recursive calls
            // I did this because interpolation test was failing due to the fact that `string`
            // `raw-string` tokens didn't have `inside`, so there were not tokenized
            // I still find that it has potential to fall with stackoverflow (in some cases)
            final List<Prism4j.Token> tokens = new ArrayList<>(kotlin.tokens().size() + 1);
            tokens.add(Prism4j.token("delimiter", Prism4j.pattern(compile("^\\$\\{|\\}$"), false, false, "variable")));
            tokens.addAll(kotlin.tokens());

            interpolationInside = Prism4j.grammar(
                    "inside",
                    Prism4j.token("interpolation",
                            Prism4j.pattern(compile("\\$\\{[^}]+\\}"), false, false, null, Prism4j.grammar("inside", tokens)),
                            Prism4j.pattern(compile("\\$\\w+"), false, false, "variable")
                    )
            );
        }

        final Prism4j.Token string = GrammarUtils.findToken(kotlin, "string");
        final Prism4j.Token rawString = GrammarUtils.findToken(kotlin, "raw-string");

        if (string != null
                && rawString != null) {

            final Prism4j.Pattern stringPattern = string.patterns().get(0);
            final Prism4j.Pattern rawStringPattern = rawString.patterns().get(0);

            string.patterns().add(
                    Prism4j.pattern(stringPattern.regex(), stringPattern.lookbehind(), stringPattern.greedy(), stringPattern.alias(), interpolationInside)
            );

            rawString.patterns().add(
                    Prism4j.pattern(rawStringPattern.regex(), rawStringPattern.lookbehind(), rawStringPattern.greedy(), rawStringPattern.alias(), interpolationInside)
            );

            string.patterns().remove(0);
            rawString.patterns().remove(0);

        } else {
            throw new RuntimeException("Unexpected state, cannot find `string` and/or `raw-string` tokens " +
                    "inside kotlin grammar");
        }

        return kotlin;
    }
}
