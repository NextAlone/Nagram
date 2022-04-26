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
import android.text.style.BackgroundColorSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.telegram.ui.Components.TextStyleSpan;

public class Prism4jThemeDefault extends Prism4jThemeBase {

    @NonNull
    public static Prism4jThemeDefault create() {
        return new Prism4jThemeDefault(0xFFf5f2f0);
    }

    @NonNull
    public static Prism4jThemeDefault create(@ColorInt int background) {
        return new Prism4jThemeDefault(background);
    }

    private final int background;

    public Prism4jThemeDefault(@ColorInt int background) {
        this.background = background;
    }

    @Override
    public int background() {
        return background;
    }

    @Override
    public int textColor() {
        return 0xdd000000;
    }

    @NonNull
    @Override
    protected ColorHashMap init() {
        return new ColorHashMap()
                .add(0xFF708090, "comment", "prolog", "doctype", "cdata")
                .add(0xFF999999, "punctuation")
                .add(0xFF990055, "property", "tag", "boolean", "number", "constant", "symbol", "deleted")
                .add(0xFF669900, "selector", "attr-name", "string", "char", "builtin", "inserted")
                .add(0xFF9a6e3a, "operator", "entity", "url")
                .add(0xFF0077aa, "atrule", "attr-value", "keyword")
                .add(0xFFDD4A68, "function", "class-name")
                .add(0xFFee9900, "regex", "important", "variable");
    }

    @Override
    protected void applyColor(
            @NonNull String language,
            @NonNull String type,
            @Nullable String alias,
            @ColorInt int color,
            @NonNull Spannable spannable,
            int start,
            int end) {

        if ("css".equals(language) && isOfType("string", type, alias)) {
            super.applyColor(language, type, alias, 0xFF9a6e3a, spannable, start, end);
            spannable.setSpan(new BackgroundColorSpan(0x80ffffff), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return;
        }

        if (isOfType("namespace", type, alias)) {
            color = applyAlpha(.7F, color);
        }

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
