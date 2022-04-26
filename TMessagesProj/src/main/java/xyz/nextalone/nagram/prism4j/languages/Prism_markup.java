package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.compile;

public abstract class Prism_markup {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {
        final Prism4j.Token entity = Prism4j.token("entity", Prism4j.pattern(compile("&#?[\\da-z]{1,8};", Pattern.CASE_INSENSITIVE)));
        return Prism4j.grammar(
                "markup",
                Prism4j.token("comment", Prism4j.pattern(compile("<!--[\\s\\S]*?-->"))),
                Prism4j.token("prolog", Prism4j.pattern(compile("<\\?[\\s\\S]+?\\?>"))),
                Prism4j.token("doctype", Prism4j.pattern(compile("<!DOCTYPE[\\s\\S]+?>", Pattern.CASE_INSENSITIVE))),
                Prism4j.token("cdata", Prism4j.pattern(compile("<!\\[CDATA\\[[\\s\\S]*?]]>", Pattern.CASE_INSENSITIVE))),
                Prism4j.token(
                        "tag",
                        Prism4j.pattern(
                                compile("<\\/?(?!\\d)[^\\s>\\/=$<%]+(?:\\s+[^\\s>\\/=]+(?:=(?:(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1|[^\\s'\">=]+))?)*\\s*\\/?>", Pattern.CASE_INSENSITIVE),
                                false,
                                true,
                                null,
                                Prism4j.grammar(
                                        "inside",
                                        Prism4j.token(
                                                "tag",
                                                Prism4j.pattern(
                                                        compile("^<\\/?[^\\s>\\/]+", Pattern.CASE_INSENSITIVE),
                                                        false,
                                                        false,
                                                        null,
                                                        Prism4j.grammar(
                                                                "inside",
                                                                Prism4j.token("punctuation", Prism4j.pattern(compile("^<\\/?"))),
                                                                Prism4j.token("namespace", Prism4j.pattern(compile("^[^\\s>\\/:]+:")))
                                                        )
                                                )
                                        ),
                                        Prism4j.token(
                                                "attr-value",
                                                Prism4j.pattern(
                                                        compile("=(?:(\"|')(?:\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1|[^\\s'\">=]+)", Pattern.CASE_INSENSITIVE),
                                                        false,
                                                        false,
                                                        null,
                                                        Prism4j.grammar(
                                                                "inside",
                                                                Prism4j.token(
                                                                        "punctuation",
                                                                        Prism4j.pattern(compile("^=")),
                                                                        Prism4j.pattern(compile("(^|[^\\\\])[\"']"), true)
                                                                ),
                                                                entity
                                                        )
                                                )
                                        ),
                                        Prism4j.token("punctuation", Prism4j.pattern(compile("\\/?>"))),
                                        Prism4j.token(
                                                "attr-name",
                                                Prism4j.pattern(
                                                        compile("[^\\s>\\/]+"),
                                                        false,
                                                        false,
                                                        null,
                                                        Prism4j.grammar(
                                                                "inside",
                                                                Prism4j.token("namespace", Prism4j.pattern(compile("^[^\\s>\\/:]+:")))
                                                        )
                                                )
                                        )
                                )
                        )
                ),
                entity
        );
    }

    private Prism_markup() {
    }
}
