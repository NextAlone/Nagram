package xyz.nextalone.nagram.prism4j.languages;


import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

public class Prism_csharp {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar classNameInsidePunctuation = Prism4j.grammar("inside",
                Prism4j.token("punctuation", Prism4j.pattern(compile("\\.")))
        );

        final Prism4j.Grammar csharp = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "clike"),
                "csharp",
                Prism4j.token("keyword", Prism4j.pattern(compile("\\b(?:abstract|add|alias|as|ascending|async|await|base|bool|break|byte|case|catch|char|checked|class|const|continue|decimal|default|delegate|descending|do|double|dynamic|else|enum|event|explicit|extern|false|finally|fixed|float|for|foreach|from|get|global|goto|group|if|implicit|in|int|interface|internal|into|is|join|let|lock|long|namespace|new|null|object|operator|orderby|out|override|params|partial|private|protected|public|readonly|ref|remove|return|sbyte|sealed|select|set|short|sizeof|stackalloc|static|string|struct|switch|this|throw|true|try|typeof|uint|ulong|unchecked|unsafe|ushort|using|value|var|virtual|void|volatile|where|while|yield)\\b"))),
                Prism4j.token("string",
                        Prism4j.pattern(compile("@(\"|')(?:\\1\\1|\\\\[\\s\\S]|(?!\\1)[^\\\\])*\\1"), false, true),
                        Prism4j.pattern(compile("(\"|')(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*?\\1"), false, true)
                ),
                Prism4j.token("class-name",
                        Prism4j.pattern(
                                compile("\\b[A-Z]\\w*(?:\\.\\w+)*\\b(?=\\s+\\w+)"),
                                false,
                                false,
                                null,
                                classNameInsidePunctuation
                        ),
                        Prism4j.pattern(
                                compile("(\\[)[A-Z]\\w*(?:\\.\\w+)*\\b"),
                                true,
                                false,
                                null,
                                classNameInsidePunctuation
                        ),
                        Prism4j.pattern(
                                compile("(\\b(?:class|interface)\\s+[A-Z]\\w*(?:\\.\\w+)*\\s*:\\s*)[A-Z]\\w*(?:\\.\\w+)*\\b"),
                                true,
                                false,
                                null,
                                classNameInsidePunctuation
                        ),
                        Prism4j.pattern(
                                compile("((?:\\b(?:class|interface|new)\\s+)|(?:catch\\s+\\())[A-Z]\\w*(?:\\.\\w+)*\\b"),
                                true,
                                false,
                                null,
                                classNameInsidePunctuation
                        )
                ),
                Prism4j.token("number", Prism4j.pattern(compile("\\b0x[\\da-f]+\\b|(?:\\b\\d+\\.?\\d*|\\B\\.\\d+)f?", CASE_INSENSITIVE)))
        );

        GrammarUtils.insertBeforeToken(csharp, "class-name",
                Prism4j.token("generic-method", Prism4j.pattern(
                        compile("\\w+\\s*<[^>\\r\\n]+?>\\s*(?=\\()"),
                        false,
                        false,
                        null,
                        Prism4j.grammar("inside",
                                Prism4j.token("function", Prism4j.pattern(compile("^\\w+"))),
                                Prism4j.token("class-name", Prism4j.pattern(compile("\\b[A-Z]\\w*(?:\\.\\w+)*\\b"), false, false, null, classNameInsidePunctuation)),
                                GrammarUtils.findToken(csharp, "keyword"),
                                Prism4j.token("punctuation", Prism4j.pattern(compile("[<>(),.:]")))
                        )
                )),
                Prism4j.token("preprocessor", Prism4j.pattern(
                        compile("(^\\s*)#.*", MULTILINE),
                        true,
                        false,
                        "property",
                        Prism4j.grammar("inside",
                                Prism4j.token("directive", Prism4j.pattern(
                                        compile("(\\s*#)\\b(?:define|elif|else|endif|endregion|error|if|line|pragma|region|undef|warning)\\b"),
                                        true,
                                        false,
                                        "keyword"
                                ))
                        )
                ))
        );

        return csharp;
    }
}
