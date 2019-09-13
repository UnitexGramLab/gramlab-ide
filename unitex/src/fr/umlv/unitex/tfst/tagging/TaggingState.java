/*
 * Unitex
 *
 * Copyright (C) 2001-2019 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.tfst.tagging;

public enum TaggingState {
	/* The box is accessible and co-accessible, 
	 * and it has been marked as preferred by the user or is a factorization node*/
	PREFERRED,
	/* The box is accessible and co-accessible and is competing with a preferred box */
	NOT_PREFERRED,
	/* The box is not both accessible and co-accessible */
	USELESS,
	/* None of previous cases */
	NEUTRAL,
	/* new tag, used when a new branch ie series of boxes connected with transitions gets connected to the main graph */
	TO_CHECK,
}
