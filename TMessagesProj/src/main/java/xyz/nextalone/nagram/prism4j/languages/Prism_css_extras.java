package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_css_extras {

    @Nullable
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar css = prism4j.grammar("css");

        if (css != null) {

            final Prism4j.Token selector = GrammarUtils.findToken(css, "selector");
            if (selector != null) {
                final Prism4j.Pattern pattern = Prism4j.pattern(
                        compile("[^{}\\s][^{}]*(?=\\s*\\{)"),
                        false,
                        false,
                        null,
                        Prism4j.grammar("inside",
                                Prism4j.token("pseudo-element", Prism4j.pattern(compile(":(?:after|before|first-letter|first-line|selection)|::[-\\w]+"))),
                                Prism4j.token("pseudo-class", Prism4j.pattern(compile(":[-\\w]+(?:\\(.*\\))?"))),
                                Prism4j.token("class", Prism4j.pattern(compile("\\.[-:.\\w]+"))),
                                Prism4j.token("id", Prism4j.pattern(compile("#[-:.\\w]+"))),
                                Prism4j.token("attribute", Prism4j.pattern(compile("\\[[^\\]]+\\]")))
                        )
                );
                selector.patterns().clear();
                selector.patterns().add(pattern);
            }

            GrammarUtils.insertBeforeToken(css, "function",
                    Prism4j.token("hexcode", Prism4j.pattern(compile("#[\\da-f]{3,8}", CASE_INSENSITIVE))),
                    Prism4j.token("entity", Prism4j.pattern(compile("\\\\[\\da-f]{1,8}", CASE_INSENSITIVE))),
                    Prism4j.token("number", Prism4j.pattern(compile("[\\d%.]+")))
            );
        }
        return null;
    }
}
