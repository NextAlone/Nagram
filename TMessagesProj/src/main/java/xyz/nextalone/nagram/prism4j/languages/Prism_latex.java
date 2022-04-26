package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class Prism_latex {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Pattern funcPattern = compile("\\\\(?:[^a-z()\\[\\]]|[a-z*]+)", CASE_INSENSITIVE);

        final Prism4j.Grammar insideEqu = Prism4j.grammar("inside",
                Prism4j.token("equation-command", Prism4j.pattern(funcPattern, false, false, "regex"))
        );

        return Prism4j.grammar("latex",
                Prism4j.token("comment", Prism4j.pattern(compile("%.*", MULTILINE))),
                Prism4j.token("cdata", Prism4j.pattern(
                                compile("(\\\\begin\\{((?:verbatim|lstlisting)\\*?)\\})[\\s\\S]*?(?=\\\\end\\{\\2\\})"),
                                true
                        )
                ),
                Prism4j.token("equation",
                        Prism4j.pattern(
                                compile("\\$(?:\\\\[\\s\\S]|[^\\\\$])*\\$|\\\\\\([\\s\\S]*?\\\\\\)|\\\\\\[[\\s\\S]*?\\\\\\]"),
                                false,
                                false,
                                "string",
                                insideEqu
                        ),
                        Prism4j.pattern(
                                compile("(\\\\begin\\{((?:equation|math|eqnarray|align|multline|gather)\\*?)\\})[\\s\\S]*?(?=\\\\end\\{\\2\\})"),
                                true,
                                false,
                                "string",
                                insideEqu
                        )
                ),
                Prism4j.token("keyword", Prism4j.pattern(
                        compile("(\\\\(?:begin|end|ref|cite|label|usepackage|documentclass)(?:\\[[^\\]]+\\])?\\{)[^}]+(?=\\})"),
                        true
                )),
                Prism4j.token("url", Prism4j.pattern(
                        compile("(\\\\url\\{)[^}]+(?=\\})"),
                        true
                )),
                Prism4j.token("headline", Prism4j.pattern(
                        compile("(\\\\(?:part|chapter|section|subsection|frametitle|subsubsection|paragraph|subparagraph|subsubparagraph|subsubsubparagraph)\\*?(?:\\[[^\\]]+\\])?\\{)[^}]+(?=\\}(?:\\[[^\\]]+\\])?)"),
                        true,
                        false,
                        "class-name"
                )),
                Prism4j.token("function", Prism4j.pattern(
                        funcPattern,
                        false,
                        false,
                        "selector"
                )),
                Prism4j.token("punctuation", Prism4j.pattern(compile("[\\[\\]{}&]")))
        );
    }
}
