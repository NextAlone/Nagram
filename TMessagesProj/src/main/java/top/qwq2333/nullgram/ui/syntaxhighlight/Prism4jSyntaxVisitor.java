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

import top.qwq2333.nullgram.ui.syntaxhighlight.prism4j.AbsVisitor;
import top.qwq2333.nullgram.ui.syntaxhighlight.prism4j.Prism4j;

class Prism4jSyntaxVisitor extends AbsVisitor {

    private final String language;
    private final Prism4jTheme theme;
    private final Spannable spannable;

    private int currentPos;

    Prism4jSyntaxVisitor(
        @NonNull String language,
        @NonNull Prism4jTheme theme,
        @NonNull Spannable spannable,
        int start) {
        this.language = language;
        this.theme = theme;
        this.spannable = spannable;

        currentPos = start;
    }

    @Override
    protected void visitText(@NonNull Prism4j.Text text) {
        currentPos += text.textLength();
    }

    @Override
    protected void visitSyntax(@NonNull Prism4j.Syntax syntax) {
        final int start = currentPos;
        visit(syntax.children());
        final int end = currentPos;

        if (end != start) {
            theme.apply(language, syntax, spannable, start, end);
        }
    }
}
