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

package xyz.nextalone.nagram.ui.syntaxhighlight;

import android.text.Spannable;
import android.text.Spanned;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.telegram.ui.Components.TextStyleSpan;

public class Prism4jThemeDarkula extends Prism4jThemeBase {

    @NonNull
    public static Prism4jThemeDarkula create() {
        return new Prism4jThemeDarkula(0xFF2d2d2d);
    }

    @NonNull
    public static Prism4jThemeDarkula create(@ColorInt int background) {
        return new Prism4jThemeDarkula(background);
    }

    private final int background;

    public Prism4jThemeDarkula(@ColorInt int background) {
        this.background = background;
    }

    @Override
    public int background() {
        return background;
    }

    @Override
    public int textColor() {
        return 0xFFa9b7c6;
    }

    @NonNull
    @Override
    protected ColorHashMap init() {
        return new ColorHashMap()
                .add(0xFF808080, "comment", "prolog", "cdata")
                .add(0xFFcc7832, "delimiter", "boolean", "keyword", "selector", "important", "atrule")
                .add(0xFFa9b7c6, "operator", "punctuation", "attr-name")
                .add(0xFFe8bf6a, "tag", "doctype", "builtin")
                .add(0xFF6897bb, "entity", "number", "symbol")
                .add(0xFF9876aa, "property", "constant", "variable")
                .add(0xFF6a8759, "string", "char")
                .add(0xFFbbb438, "annotation")
                .add(0xFFa5c261, "attr-value")
                .add(0xFF287bde, "url")
                .add(0xFFffc66d, "function")
                .add(0xFF364135, "regex")
                .add(0xFF294436, "inserted")
                .add(0xFF484a4a, "deleted");
    }

    @Override
    protected void applyColor(@NonNull String language, @NonNull String type, @Nullable String alias, int color, @NonNull Spannable spannable, int start, int end) {
        super.applyColor(language, type, alias, color, spannable, start, end);

        if (isOfType("important", type, alias)
                || isOfType("bold", type, alias)) {
            var run = new TextStyleSpan.TextStyleRun();
            run.flags |= TextStyleSpan.FLAG_STYLE_BOLD;
            spannable.setSpan(new TextStyleSpan(run), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (isOfType("italic", type, alias)) {
            var run = new TextStyleSpan.TextStyleRun();
            run.flags |= TextStyleSpan.FLAG_STYLE_ITALIC;
            spannable.setSpan(new TextStyleSpan(run), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
