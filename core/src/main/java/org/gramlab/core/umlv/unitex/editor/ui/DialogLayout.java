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
package org.gramlab.core.umlv.unitex.editor.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

class DialogLayout implements LayoutManager {
	private int m_divider = -1;
	private int m_hGap = 10;
	private int m_vGap = 5;

	public DialogLayout(int hGap, int vGap) {
		m_hGap = hGap;
		m_vGap = vGap;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// nothing to do
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// nothing to do
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		final int divider = getDivider(parent);
		int w = 0;
		int h = 0;
		for (int k = 1; k < parent.getComponentCount(); k += 2) {
			final Component comp = parent.getComponent(k);
			final Dimension d = comp.getPreferredSize();
			w = Math.max(w, d.width);
			h += d.height + m_vGap;
		}
		h -= m_vGap;
		final Insets insets = parent.getInsets();
		return new Dimension(divider + w + insets.left + insets.right, h
				+ insets.top + insets.bottom);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		final int divider = getDivider(parent);
		final Insets insets = parent.getInsets();
		final int w = parent.getWidth() - insets.left - insets.right - divider;
		final int x = insets.left;
		int y = insets.top;
		for (int k = 1; k < parent.getComponentCount(); k += 2) {
			final Component comp1 = parent.getComponent(k - 1);
			final Component comp2 = parent.getComponent(k);
			final Dimension d = comp2.getPreferredSize();
			comp1.setBounds(x, y, divider - m_hGap, d.height);
			comp2.setBounds(x + divider, y, w, d.height);
			y += d.height + m_vGap;
		}
	}

	public int getHGap() {
		return m_hGap;
	}

	public int getVGap() {
		return m_vGap;
	}

	public void setDivider(int divider) {
		if (divider > 0)
			m_divider = divider;
	}

	public int getDivider() {
		return m_divider;
	}

	int getDivider(Container parent) {
		if (m_divider > 0)
			return m_divider;
		int divider = 0;
		for (int k = 0; k < parent.getComponentCount(); k += 2) {
			final Component comp = parent.getComponent(k);
			final Dimension d = comp.getPreferredSize();
			divider = Math.max(divider, d.width);
		}
		divider += m_hGap;
		return divider;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[hgap=" + m_hGap + ",vgap=" + m_vGap
				+ ",divider=" + m_divider + "]";
	}
}
