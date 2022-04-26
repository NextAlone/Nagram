package xyz.nextalone.nagram.prism4j.languages;

import androidx.annotation.NonNull;

import java.util.List;

import xyz.nextalone.nagram.prism4j.GrammarUtils;
import xyz.nextalone.nagram.prism4j.Prism4j;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Prism_swift {

    @NonNull
    public static Prism4j.Grammar create(@NonNull Prism4j prism4j) {

        final Prism4j.Grammar swift = GrammarUtils.extend(
                GrammarUtils.require(prism4j, "clike"),
                "swift",
                Prism4j.token("string", Prism4j.pattern(
                        compile("(\"|')(\\\\(?:\\((?:[^()]|\\([^)]+\\))+\\)|\\r\\n|[\\s\\S])|(?!\\1)[^\\\\\\r\\n])*\\1"),
                        false,
                        true,
                        null,
                        Prism4j.grammar("inside", Prism4j.token("interpolation", Prism4j.pattern(
                                compile("\\\\\\((?:[^()]|\\([^)]+\\))+\\)"),
                                false,
                                false,
                                null,
                                Prism4j.grammar("inside", Prism4j.token("delimiter", Prism4j.pattern(
                                        compile("^\\\\\\(|\\)$"),
                                        false,
                                        false,
                                        "variable"
                                )))
                        )))
                )),
                Prism4j.token("keyword", Prism4j.pattern(
                        compile("\\b(?:as|associativity|break|case|catch|class|continue|convenience|default|defer|deinit|didSet|do|dynamic(?:Type)?|else|enum|extension|fallthrough|final|for|func|get|guard|if|import|in|infix|init|inout|internal|is|lazy|left|let|mutating|new|none|nonmutating|operator|optional|override|postfix|precedence|prefix|private|protocol|public|repeat|required|rethrows|return|right|safe|self|Self|set|static|struct|subscript|super|switch|throws?|try|Type|typealias|unowned|unsafe|var|weak|where|while|willSet|__(?:COLUMN__|FILE__|FUNCTION__|LINE__))\\b")
                )),
                Prism4j.token("number", Prism4j.pattern(
                        compile("\\b(?:[\\d_]+(?:\\.[\\de_]+)?|0x[a-f0-9_]+(?:\\.[a-f0-9p_]+)?|0b[01_]+|0o[0-7_]+)\\b", CASE_INSENSITIVE)
                ))
        );

        final List<Prism4j.Token> tokens = swift.tokens();

        tokens.add(Prism4j.token("constant", Prism4j.pattern(compile("\\b(?:nil|[A-Z_]{2,}|k[A-Z][A-Za-z_]+)\\b"))));
        tokens.add(Prism4j.token("atrule", Prism4j.pattern(compile("@\\b(?:IB(?:Outlet|Designable|Action|Inspectable)|class_protocol|exported|noreturn|NS(?:Copying|Managed)|objc|UIApplicationMain|auto_closure)\\b"))));
        tokens.add(Prism4j.token("builtin", Prism4j.pattern(compile("\\b(?:[A-Z]\\S+|abs|advance|alignof(?:Value)?|assert|contains|count(?:Elements)?|debugPrint(?:ln)?|distance|drop(?:First|Last)|dump|enumerate|equal|filter|find|first|getVaList|indices|isEmpty|join|last|lexicographicalCompare|map|max(?:Element)?|min(?:Element)?|numericCast|overlaps|partition|print(?:ln)?|reduce|reflect|reverse|sizeof(?:Value)?|sort(?:ed)?|split|startsWith|stride(?:of(?:Value)?)?|suffix|swap|toDebugString|toString|transcode|underestimateCount|unsafeBitCast|with(?:ExtendedLifetime|Unsafe(?:MutablePointers?|Pointers?)|VaList))\\b"))));

        final Prism4j.Token interpolationToken = GrammarUtils.findToken(swift, "string/interpolation");
        final Prism4j.Grammar interpolationGrammar = interpolationToken != null
                ? GrammarUtils.findFirstInsideGrammar(interpolationToken)
                : null;
        if (interpolationGrammar != null) {
            interpolationGrammar.tokens().addAll(swift.tokens());
        }

        return swift;
    }
}
