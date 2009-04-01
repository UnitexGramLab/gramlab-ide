/*
 * Unitex
 *
 * Copyright (C) 2001-2009 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.tfst;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField.AbstractFormatter;

public class BoundsFormatter extends AbstractFormatter {

    public final static String NO_BOUNDS_DEFINED="??-??";
    private final static Pattern pattern=Pattern.compile("([0-9]+)-([0-9]+)");
    
    @Override
    public Bounds stringToValue(String text) throws ParseException {
        if (NO_BOUNDS_DEFINED.equals(text)) {
            return null;
        }
        Matcher matcher=pattern.matcher(text);
        if (!matcher.matches()) {
            throw new ParseException("Invalid bounds (1): "+text,0);
        }
        int a=Integer.valueOf(matcher.group(1));
        int b=Integer.valueOf(matcher.group(2));
        if (a>b) {
            throw new ParseException("Invalid bounds (2): "+text, 0);
        }
        return new Bounds(a,b);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value==null) {
            return NO_BOUNDS_DEFINED;
        }
        Bounds b=(Bounds)value;
        return b.getGlobal_start_in_chars()+"-"+b.getGlobal_end_in_chars();
    }

}
