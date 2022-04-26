package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.compile;

public class Prism_brainfuck {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        return Prism4j.grammar("brainfuck",
                Prism4j.token("pointer", Prism4j.pattern(compile("<|>"), false, false, "keyword")),
                Prism4j.token("increment", Prism4j.pattern(compile("\\+"), false, false, "inserted")),
                Prism4j.token("decrement", Prism4j.pattern(compile("-"), false, false, "deleted")),
                Prism4j.token("branching", Prism4j.pattern(compile("\\[|\\]"), false, false, "important")),
                Prism4j.token("operator", Prism4j.pattern(compile("[.,]"))),
                Prism4j.token("comment", Prism4j.pattern(compile("\\S+")))
        );
    }
}
