package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class Prism_git {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        return Prism4j.grammar("git",
                Prism4j.token("comment", Prism4j.pattern(compile("^#.*", MULTILINE))),
                Prism4j.token("deleted", Prism4j.pattern(compile("^[-–].*", MULTILINE))),
                Prism4j.token("inserted", Prism4j.pattern(compile("^\\+.*", MULTILINE))),
                Prism4j.token("string", Prism4j.pattern(compile("(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1", MULTILINE))),
                Prism4j.token("command", Prism4j.pattern(
                        compile("^.*\\$ git .*$", MULTILINE),
                        false,
                        false,
                        null,
                        Prism4j.grammar("inside",
                                Prism4j.token("parameter", Prism4j.pattern(compile("\\s--?\\w+", MULTILINE)))
                        )
                )),
                Prism4j.token("coord", Prism4j.pattern(compile("^@@.*@@$", MULTILINE))),
                Prism4j.token("commit_sha1", Prism4j.pattern(compile("^commit \\w{40}$", MULTILINE)))
        );
    }
}
