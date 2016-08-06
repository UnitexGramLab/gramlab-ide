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
 * @(#)       Dist.java
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

class Dist {
	private final int[] dist;
	private final int num;
	static private double align_cst_c = 1.0D; // BG : moyenne ?
	static private double align_cst_s2 = 6.7999999999999998D; // BG : sigma 2 =
																// variance ???
	private static final int BIG_DISTANCE = 2500;

	public Dist(int nb) {
		num = nb;
		dist = new int[num];
	}

	// public Dist() throws IOException
	// {
	// EasyIn easy = new EasyIn();
	// num = easy.readInt();
	// dist = new int[num];
	// for (int i = 0; i < num; i++) {
	// dist[i] = easy.readInt();
	// }
	// }
	public int getSize() {
		return (num);
	}

	public int getDistAt(int at) {
		return (this.dist[at]);
	}

	public void setDistAt(int at, int d) {
		this.dist[at] = d;
	}

	/* Local Distance Function */
	/*
	 * Returns the area under a normal distribution from -inf to z standard
	 * deviations
	 */
	private static double ProbaNorm(double z) {
		double t, pd;
		t = 1.0D / (1.0D + 0.23164190000000001D * z);
		pd = 1.0D
				- 0.39894230000000003D
				* Math.exp((-z * z) / 2.0D)
				* ((((1.3302744289999999D * t - 1.8212559779999999D) * t + 1.781477937D)
						* t - 0.356563782D)
						* t + 0.31938153000000002D) * t;
		/* see Abramowitz, M., and I. Stegun (1964), 26.2.17 p. 932 */
		return pd;
	}

	/*
	 * Return -100 * log probability that an English sentence of length len1 is
	 * a translation of a foreign sentence of length len2. The probability is
	 * based on two parameters, the mean and variance of number of foreign
	 * characters per English character.
	 */
	private static int Match(int len1, int len2) {
		double z, pd, mean;
		if (len1 == 0 && len2 == 0)
			return 0;
		mean = ((len1 + len2) / (2 * align_cst_c));
		z = (align_cst_c * (len1 - len2)) / Math.sqrt(align_cst_s2 * mean);
		/* Need to deal with both sides of the normal distribution */
		if (z < 0.0D)
			z = -z;
		pd = 2D * (1.0D - ProbaNorm(z));
		// System.out.println("=> z=" + z + " pd=" + pd);
		if (pd > 0) {
			return (int) (-100D * Math.log(pd));
		}
		return BIG_DISTANCE;
	}

	public static int TwoSideDistance(int x1, int y1, int x2, int y2) {
		final int penalty21 = 230; /*
									 * 23 : -100 * ln([prob of 2-1 match] /
									 * [prob of 1-1 match])
									 */
		final int penalty22 = 440; /*
									 * 44 : -100 * ln([prob of 2-2 match] /
									 * [prob of 1-1 match])
									 */
		final int penalty01 = 450; /*
									 * 45 : -100 * ln([prob of 0-1 match] /
									 * [prob of 1-1 match])
									 */
		if (x2 == 0 && y2 == 0)
			if (x1 == 0) /* insertion */
				return Match(x1, y1) + penalty01;
			else if (y1 == 0) /* deletion */
				return Match(x1, y1) + penalty01;
			else
				return Match(x1, y1); /* substitution */
		else if (x2 == 0) /* expansion */
			return Match(x1, y1 + y2) + penalty21;
		else if (y2 == 0) /* contraction */
			return Match(x1 + x2, y1) + penalty21;
		else
			/* merger */
			return Match(x1 + x2, y1 + y2) + penalty22;
	}

	public double getCstC() {
		return align_cst_c;
	}

	public double getCstS2() {
		return align_cst_s2;
	}

	public void setCstC(double c) {
		align_cst_c = c;
	}

	public void setCstS2(double s2) {
		align_cst_s2 = s2;
	}
}
// EOF
