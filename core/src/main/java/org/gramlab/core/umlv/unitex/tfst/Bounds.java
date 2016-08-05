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
package org.gramlab.core.umlv.unitex.tfst;

/**
 * This class represent the bounds of a sequence in the text that goes with a
 * sentence automaton.
 * 
 * @author paumier
 */
public class Bounds {
	/* Offsets in chars relative to the beginning of the sentence */
	private int global_start_in_chars;
	private int global_end_in_chars;
	/*
	 * Offsets in tokens and positions in chars inside those tokens.
	 */
	private int start_in_tokens;
	private int start_in_chars;
	private int end_in_tokens;
	private int end_in_chars;
	/* Special information for Korean */
	private int start_in_letters;
	private int end_in_letters;

	public Bounds(int global_start_in_chars, int global_end_in_chars) {
		// System.out.println(global_start_in_chars+" -> "+global_end_in_chars);
		this.global_start_in_chars = global_start_in_chars;
		this.global_end_in_chars = global_end_in_chars;
		global_to_relative();
	}

	public Bounds(int start_in_tokens, int start_in_chars,
			int start_in_letters, int end_in_tokens, int end_in_chars,
			int end_in_letters) {
		if (start_in_tokens < 0 || start_in_chars < 0 || start_in_letters < 0
				|| end_in_tokens < 0 || end_in_chars < 0 /* || end_in_letters<0 */) {
			/*
			 * We don't test end_in_letters<0, because end_in_letters==-1 is the
			 * sign that the tag has an empty surface form like {<E>,.JO}
			 */
			throw new IllegalArgumentException("Invalid negative bounds: "
					+ start_in_tokens + " " + start_in_chars + " "
					+ end_in_tokens + " " + end_in_chars);
		}
		this.start_in_tokens = start_in_tokens;
		this.start_in_chars = start_in_chars;
		this.start_in_letters = start_in_letters;
		this.end_in_tokens = end_in_tokens;
		this.end_in_chars = end_in_chars;
		this.end_in_letters = end_in_letters;
		relative_to_global();
		// System.out.println("2e constructeur: "+global_start_in_chars+" -> "+global_end_in_chars);
	}

	private void global_to_relative() {
		/*
		 * We set those values by default for Korean transitions. This may
		 * change in the future if we allow real edition for Korean sentence
		 * graphs
		 */
		start_in_letters = 0;
		end_in_letters = 0;
		start_in_tokens = 0;
		int current_length = 0;
		while (current_length + TokensInfo.getTokenLength(start_in_tokens) <= global_start_in_chars) {
			current_length = current_length
					+ TokensInfo.getTokenLength(start_in_tokens);
			start_in_tokens++;
		}
		if (global_start_in_chars != current_length) {
			start_in_chars = global_start_in_chars - current_length;
		} else {
			start_in_chars = 0;
		}
		end_in_tokens = start_in_tokens;
		while (current_length + TokensInfo.getTokenLength(end_in_tokens) <= global_end_in_chars) {
			current_length = current_length
					+ TokensInfo.getTokenLength(end_in_tokens);
			end_in_tokens++;
		}
		end_in_chars = global_end_in_chars - current_length;
		/*
		 * if (global_end_in_chars!=current_length) {
		 * end_in_chars=global_end_in_chars-current_length; } else {
		 * end_in_chars=-1; }
		 */
	}

	private void relative_to_global() {
		global_start_in_chars = 0;
		for (int i = 0; i < start_in_tokens; i++) {
			global_start_in_chars = global_start_in_chars
					+ TokensInfo.getTokenLength(i);
		}
		global_start_in_chars = global_start_in_chars + start_in_chars;
		/*
		 * if (start_in_chars!=-1) {
		 * global_start_in_chars=global_start_in_chars+start_in_chars; }
		 */
		global_end_in_chars = 0;
		final int last = end_in_tokens;
		/*
		 * if (end_in_chars==-1) { last++; }
		 */
		for (int i = 0; i < last; i++) {
			global_end_in_chars = global_end_in_chars
					+ TokensInfo.getTokenLength(i);
		}
		global_end_in_chars = global_end_in_chars + end_in_chars;
		/*
		 * if (end_in_chars!=-1) {
		 * global_end_in_chars=global_end_in_chars+end_in_chars; } else { // We
		 * want to set the ending position on the last char, not after it
		 * global_end_in_chars--; }
		 */
		/*
		 * System.out.println(start_in_tokens+"."+start_in_chars+"."+
		 * start_in_letters
		 * +" "+end_in_tokens+"."+end_in_chars+"."+end_in_letters+"  =>  "+
		 * global_start_in_chars+"-"+global_end_in_chars);
		 */
	}

	public int getGlobal_start_in_chars() {
		return global_start_in_chars;
	}

	public int getGlobal_end_in_chars() {
		return global_end_in_chars;
	}

	public int getStart_in_tokens() {
		return start_in_tokens;
	}

	public int getStart_in_chars() {
		return start_in_chars;
	}

	public int getStart_in_letters() {
		return start_in_letters;
	}

	public int getEnd_in_tokens() {
		return end_in_tokens;
	}

	public int getEnd_in_chars() {
		return end_in_chars;
	}

	public int getEnd_in_letters() {
		return end_in_letters;
	}

	@Override
	public String toString() {
		return start_in_tokens + " " + start_in_chars + " " + start_in_letters
				+ " " + end_in_tokens + " " + end_in_chars + " "
				+ end_in_letters;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			final Bounds b = (Bounds) obj;
			return b != null && start_in_tokens == b.start_in_tokens
					&& start_in_chars == b.start_in_chars
					&& start_in_letters == b.start_in_letters
					&& end_in_tokens == b.end_in_tokens
					&& end_in_chars == b.end_in_chars
					&& end_in_letters == b.end_in_letters;
		} catch (final ClassCastException e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (start_in_tokens * 17 + start_in_chars * 5 + start_in_letters)
				* 23
				+ (end_in_tokens * 23 + end_in_chars * 11 + end_in_letters);
	}
}
