package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class Prism_makefile {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        return Prism4j.grammar("makefile",
                Prism4j.token("comment", Prism4j.pattern(
                        compile("(^|[^\\\\])#(?:\\\\(?:\\r\\n|[\\s\\S])|[^\\\\\\r\\n])*"),
                        true
                )),
                Prism4j.token("string", Prism4j.pattern(
                        compile("([\"'])(?:\\\\(?:\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"),
                        false,
                        true
                )),
                Prism4j.token("builtin", Prism4j.pattern(compile("\\.[A-Z][^:#=\\s]+(?=\\s*:(?!=))"))),
                Prism4j.token("symbol", Prism4j.pattern(
                        compile("^[^:=\\r\\n]+(?=\\s*:(?!=))", MULTILINE),
                        false,
                        false,
                        null,
                        Prism4j.grammar("inside",
                                Prism4j.token("variable", Prism4j.pattern(compile("\\$+(?:[^(){}:#=\\s]+|(?=[({]))")))
                        )
                )),
                Prism4j.token("variable", Prism4j.pattern(compile("\\$+(?:[^(){}:#=\\s]+|\\([@*%<^+?][DF]\\)|(?=[({]))"))),
                Prism4j.token("keyword",
                        Prism4j.pattern(compile("-include\\b|\\b(?:define|else|endef|endif|export|ifn?def|ifn?eq|include|override|private|sinclude|undefine|unexport|vpath)\\b")),
                        Prism4j.pattern(
                                compile("(\\()(?:addsuffix|abspath|and|basename|call|dir|error|eval|file|filter(?:-out)?|findstring|firstword|flavor|foreach|guile|if|info|join|lastword|load|notdir|or|origin|patsubst|realpath|shell|sort|strip|subst|suffix|value|warning|wildcard|word(?:s|list)?)(?=[ \\t])"),
                                true
                        )
                ),
                Prism4j.token("operator", Prism4j.pattern(compile("(?:::|[?:+!])?=|[|@]"))),
                Prism4j.token("punctuation", Prism4j.pattern(compile("[:;(){}]")))
        );
    }
}
