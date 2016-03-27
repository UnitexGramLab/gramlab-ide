/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex;

import java.awt.Font;

public class FontInfo {
	private Font font;
	private int size;

	/**
	 * We have to store the size that will be displayed to the user, because
	 * font.getSize() returns a point size that is not the same because of the
	 * $*!# .72 factor !
	 */
	public FontInfo(Font font, int size) {
		this.font = font;
		this.size = size;
	}

	@Override
	public FontInfo clone() {
		return new FontInfo(font, size);
	}

	public Font getFont() {
		return font;
	}

	public int getSize() {
		return size;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
