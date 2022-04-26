package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_json {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        return Prism4j.grammar(
                "json",
                Prism4j.token("property", Prism4j.pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"(?=\\s*:)", CASE_INSENSITIVE))),
                Prism4j.token("string", Prism4j.pattern(compile("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"(?!\\s*:)"), false, true)),
                Prism4j.token("number", Prism4j.pattern(compile("\\b0x[\\dA-Fa-f]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)(?:[Ee][+-]?\\d+)?"))),
                Prism4j.token("punctuation", Prism4j.pattern(compile("[{}\\[\\]);,]"))),
                // not sure about this one...
                Prism4j.token("operator", Prism4j.pattern(compile(":"))),
                Prism4j.token("boolean", Prism4j.pattern(compile("\\b(?:true|false)\\b", CASE_INSENSITIVE))),
                Prism4j.token("null", Prism4j.pattern(compile("\\bnull\\b", CASE_INSENSITIVE)))
        );
    }
}
