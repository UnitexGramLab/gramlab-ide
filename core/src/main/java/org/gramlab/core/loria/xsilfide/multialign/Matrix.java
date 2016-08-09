/*
 * XAlign
 *
 * Copyright (C) LORIA
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
/*
 * @(#)       Matrix.java
 * 
 * 
 * 
 * 
 * @version   
 * @author    Patrice Bonhomme
 * Copyright  1997 (C) PATRICE BONHOMME
 *            CRIN/CNRS & INRIA Lorraine
 *
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

class Matrix {
	private final int[][] data;
	private final int num_rows;
	private final int num_cols;

	/*
	 * Construct & Initialize to 0.0
	 */
	public Matrix(int rows, int cols) {
		this(rows, cols, 0);
	}

	/*
	 * Construct & Initialize to init_value
	 */
	private Matrix(int rows, int cols, int init_value) {
		num_rows = rows;
		num_cols = cols;
		data = new int[num_rows][num_cols];
		for (int i = 0; i < num_rows; i++)
			for (int j = 0; j < num_cols; j++)
				data[i][j] = init_value;
	}

	/*
	 * Construct & Initialize to data in raw_data
	 */
	public Matrix(int[][] raw_data) {
		num_rows = raw_data.length;
		num_cols = raw_data[0].length;
		data = new int[num_rows][num_cols];
		for (int i = 0; i < num_rows; i++)
			System.arraycopy(raw_data[i], 0, data[i], 0, num_cols);
	}

	/*
	 * Construct from an Input Stream
	 */
	public Matrix(InputStream input) throws IOException {
		final DataInputStream in = new DataInputStream(input);
		num_rows = in.readInt();
		num_cols = in.readInt();
		data = new int[num_rows][num_cols];
		for (int i = 0; i < num_rows; i++)
			for (int j = 0; j < num_cols; j++)
				data[i][j] = in.readInt();
	}

	public int getElem(int row, int col) {
		if (row < 0 || row >= num_rows)
			return 0;
		if (col < 0 || col >= num_cols)
			return 0;
		return data[row][col];
	}

	public void setElem(int row, int col, int x) {
		if (row < 0 || row >= num_rows)
			return;
		if (col < 0 || col >= num_cols)
			return;
		data[row][col] = x;
	}

	public int getNumberOfRows() {
		return (num_rows);
	}

	public int getNumberOfCols() {
		return (num_cols);
	}

	/*
	 * Returns a string representing this matrix. Each row is separated by a
	 * newline character ("\n").
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < num_rows; i++) {
			for (int j = 0; j < (num_cols - 1); j++) {
				s += (Integer.toString(data[i][j]) + " ");
			}
			s += (Integer.toString(data[i][num_cols - 1]) + "\n");
		}
		return s;
	}
}
// EOF
