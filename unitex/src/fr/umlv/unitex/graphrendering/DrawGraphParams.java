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
package fr.umlv.unitex.graphrendering;

import java.awt.Color;

/**
 * This class contains generic drawing parameters for graphs.
 * 
 * @author Nebojša Vasiljević
 */
public class DrawGraphParams {
	
public static final int BASE_DPI =72;
	
private double scaleFactor;
private int dpi;
private float compressionQuality;

private boolean antialiasing;
private Color backgroundColor;
private Color foregroundColor;
private Color commentColor;
private Color selectedColor;
private Color subgraphColor;
private Color packageColor;
private Color unreachableGraphColor;
private Color outputVariableColor;
private Color contextColor;
private Color morphologicalModeColor;
private Color genericGrfColor;
private boolean frame;
private boolean filename;
private boolean pathname;
private boolean date;

private boolean crop;
private int cropMarginW;
private int cropMarginH;


public double getTotalScale() {
	return scaleFactor * dpi / DrawGraphParams.BASE_DPI;
}


public double getScaleFactor() {
	return scaleFactor;
}

public void setScaleFactor(double scaleFactor) {
	this.scaleFactor = scaleFactor;
}

public boolean isAntialiasing() {
	return antialiasing;
}

public void setAntialiasing(boolean antialiasing) {
	this.antialiasing = antialiasing;
}

public Color getBackgroundColor() {
	return backgroundColor;
}

public void setBackgroundColor(Color backgroundColor) {
	this.backgroundColor = backgroundColor;
}

public Color getForegroundColor() {
	return foregroundColor;
}

public void setForegroundColor(Color foregroundColor) {
	this.foregroundColor = foregroundColor;
}

public boolean isFrame() {
	return frame && !crop;
}

public void setFrame(boolean frame) {
	this.frame = frame;
}

public boolean isFilename() {
	return filename && !crop;
}

public void setFilename(boolean filename) {
	this.filename = filename;
}

public boolean isPathname() {
	return pathname && !crop;
}

public void setPathname(boolean pathname) {
	this.pathname = pathname;
}

public boolean isDate() {
	return date && !crop;
}

public void setDate(boolean date) {
	this.date = date;
}

public Color getCommentColor() {
	return commentColor;
}

public void setCommentColor(Color commentColor) {
	this.commentColor = commentColor;
}

public Color getSelectedColor() {
	return selectedColor;
}

public void setSelectedColor(Color selectedColor) {
	this.selectedColor = selectedColor;
}

public Color getSubgraphColor() {
	return subgraphColor;
}

public void setSubgraphColor(Color subgraphColor) {
	this.subgraphColor = subgraphColor;
}

public Color getPackageColor() {
	return packageColor;
}

public void setPackageColor(Color packageColor) {
	this.packageColor = packageColor;
}

public Color getUnreachableGraphColor() {
	return unreachableGraphColor;
}

public void setUnreachableGraphColor(Color unreachableGraphColor) {
	this.unreachableGraphColor = unreachableGraphColor;
}

public Color getOutputVariableColor() {
	return outputVariableColor;
}

public void setOutputVariableColor(Color outputVariableColor) {
	this.outputVariableColor = outputVariableColor;
}

public Color getContextColor() {
	return contextColor;
}

public void setContextColor(Color contextColor) {
	this.contextColor = contextColor;
}

public Color getMorphologicalModeColor() {
	return morphologicalModeColor;
}

public void setMorphologicalModeColor(Color morphologicalModeColor) {
	this.morphologicalModeColor = morphologicalModeColor;
}

public Color getGenericGrfColor() {
    return genericGrfColor;
}

public void setGenericGrfColor(Color gGrfColor) {
    this.genericGrfColor = gGrfColor;
}
public int getDpi() {
	return dpi;
}

public void setDpi(int dpi) {
	this.dpi = dpi;
}


public boolean isCrop() {
	return crop;
}


public void setCrop(boolean crop) {
	this.crop = crop;
}


public int getCropMarginW() {
	return cropMarginW;
}


public void setCropMarginW(int cropMarginW) {
	this.cropMarginW = cropMarginW;
}


public int getCropMarginH() {
	return cropMarginH;
}


public void setCropMarginH(int cropMarginH) {
	this.cropMarginH = cropMarginH;
}

public float getCompressionQuality() {
	return compressionQuality;
}


public void setCompressionQuality(float compressionQuality) {
	this.compressionQuality = compressionQuality;
}


}
