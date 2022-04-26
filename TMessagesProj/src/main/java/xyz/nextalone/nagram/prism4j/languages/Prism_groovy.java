package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_groovy {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar groovy = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "clike"),
                "groovy",
                Prism4j.token("keyword", Prism4j.pattern(compile("\\b(?:as|def|in|abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|trait|transient|try|void|volatile|while)\\b"))),
                Prism4j.token("string",
                        Prism4j.pattern(
                                compile("(\"\"\"|''')[\\s\\S]*?\\1|(?:\\$\\/)(?:\\$\\/\\$|[\\s\\S])*?\\/\\$"), false, true
                        ),
                        Prism4j.pattern(
                                compile("([\"'\\/])(?:\\\\.|(?!\\1)[^\\\\\\r\\n])*\\1"), false, true
                        )
                ),
                Prism4j.token("number",
                        Prism4j.pattern(
                                compile("\\b(?:0b[01_]+|0x[\\da-f_]+(?:\\.[\\da-f_p\\-]+)?|[\\d_]+(?:\\.[\\d_]+)?(?:e[+-]?[\\d]+)?)[glidf]?\\b", CASE_INSENSITIVE)
                        )
                ),
                Prism4j.token("operator",
                        Prism4j.pattern(
                                compile("(^|[^.])(?:~|==?~?|\\?[.:]?|\\*(?:[.=]|\\*=?)?|\\.[@&]|\\.\\.<|\\.{1,2}(?!\\.)|-[-=>]?|\\+[+=]?|!=?|<(?:<=?|=>?)?|>(?:>>?=?|=)?|&[&=]?|\\|[|=]?|\\/=?|\\^=?|%=?)"),
                                true
                        )
                ),
                Prism4j.token("punctuation",
                        Prism4j.pattern(compile("\\.+|[{}\\[\\];(),:$]"))
                )
        );

        GrammarUtils.insertBeforeToken(groovy, "string",
                Prism4j.token("shebang", Prism4j.pattern(
                        compile("#!.+"),
                        false,
                        false,
                        "comment"
                ))
        );

        GrammarUtils.insertBeforeToken(groovy, "punctuation",
                Prism4j.token("spock-block", Prism4j.pattern(
                        compile("\\b(?:setup|given|when|then|and|cleanup|expect|where):")
                ))
        );

        GrammarUtils.insertBeforeToken(groovy, "function",
                Prism4j.token("annotation", Prism4j.pattern(
                        compile("(^|[^.])@\\w+"),
                        true,
                        false,
                        "punctuation"
                ))
        );

        // no string templates :(

        return groovy;
    }
}
