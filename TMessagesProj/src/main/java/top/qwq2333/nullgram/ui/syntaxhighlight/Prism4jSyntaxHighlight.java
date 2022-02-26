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

import android.text.Spannable;

import androidx.annotation.NonNull;

import top.qwq2333.nullgram.ui.syntaxhighlight.prism4j.Prism4j;

public class Prism4jSyntaxHighlight {

    @NonNull
    public static Prism4jSyntaxHighlight create(
        @NonNull Prism4j prism4j,
        @NonNull Prism4jTheme theme) {
        return new Prism4jSyntaxHighlight(prism4j, theme);
    }

    private final Prism4j prism4j;
    private final Prism4jTheme theme;

    protected Prism4jSyntaxHighlight(
        @NonNull Prism4j prism4j,
        @NonNull Prism4jTheme theme) {
        this.prism4j = prism4j;
        this.theme = theme;
    }

    public void highlight(@NonNull String info, @NonNull Spannable spannable, int start, int end) {
        final Prism4j.Grammar grammar = prism4j.grammar(info);
        if (grammar != null) {
            final Prism4jSyntaxVisitor visitor = new Prism4jSyntaxVisitor(info, theme, spannable, start);
            visitor.visit(prism4j.tokenize(spannable.subSequence(start, end).toString(), grammar));
        }
    }

    @NonNull
    protected Prism4j prism4j() {
        return prism4j;
    }

    @NonNull
    protected Prism4jTheme theme() {
        return theme;
    }
}
