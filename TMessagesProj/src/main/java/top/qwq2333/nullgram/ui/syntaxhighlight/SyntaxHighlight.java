/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.ui.syntaxhighlight;

import android.graphics.Color;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.TextStyleSpan;

import top.qwq2333.nullgram.config.ConfigManager;
import top.qwq2333.nullgram.ui.syntaxhighlight.prism4j.Prism4j;
import top.qwq2333.nullgram.utils.Defines;


public class SyntaxHighlight {

    private static final Prism4jThemeDefault theme = Prism4jThemeDefault.create();
    private static Prism4jSyntaxHighlight highlight;

    public static void highlight(TextStyleSpan.TextStyleRun run, Spannable spannable) {
        if (run.urlEntity instanceof TLRPC.TL_messageEntityHashtag) {
            var length = run.end - run.start;
            if (length == 7 || length == 9) {
                try {
                    int color = Color.parseColor(spannable.subSequence(run.start, run.end).toString());
                    spannable.setSpan(new ColorHighlightSpan(color, run), run.end - 1, run.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (IllegalArgumentException ignore) {
                }
            }
        } else if (ConfigManager.getBooleanOrDefault(Defines.codeSyntaxHighlight, true) && !TextUtils.isEmpty(run.urlEntity.language)) {
            if (highlight == null) {
                highlight = Prism4jSyntaxHighlight.create(new Prism4j(new Prism4jGrammarLocator()), theme);
            }
            highlight.highlight(run.urlEntity.language, spannable, run.start, run.end);
        }
    }

    public static void highlight(String language, int start, int end, Spannable spannable) {
        if (ConfigManager.getBooleanOrDefault(Defines.codeSyntaxHighlight, true) && !TextUtils.isEmpty(language)) {
            if (highlight == null) {
                highlight = Prism4jSyntaxHighlight.create(new Prism4j(new Prism4jGrammarLocator()), theme);
            }
            highlight.highlight(language, spannable, start, end);
        }
    }

    public static void updateColors() {
        theme.updateColors();
    }
}

