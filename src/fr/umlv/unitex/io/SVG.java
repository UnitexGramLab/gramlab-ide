/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.io;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import fr.umlv.unitex.GenericGraphBox;
import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.GraphicalZone;
import fr.umlv.unitex.frames.GraphFrame;

/**
 * This class provides static method for saving graphs as SVG files. 
 * @author Sébastien Paumier
 */
public class SVG {

	private OutputStreamWriter writer;
	private GraphFrame frame;
	private GraphicalZone graphicalZone;
	private Graphics2D graphics;
	private GraphPresentationInfo info;
	private int h_ligne;
	private int descent;
	
	public SVG(OutputStreamWriter writer, GraphFrame frame) {
		this.writer=writer;
		this.frame=frame;
		this.graphicalZone=frame.getGraphicalZone();
		this.graphics=(Graphics2D)graphicalZone.getGraphics();
		this.info=graphicalZone.getGraphPresentationInfo();
	}


	private void header() throws IOException {
		writer.write("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
		writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
		writer.write("<svg width=\""+graphicalZone.Width+"\" height=\""+graphicalZone.Height+"\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\n");
		writer.write("<desc>\n\tThis SVG file was created by Unitex (http://igm.univ-mlv.fr/~unitex).\n");
		writer.write("\tIt represents the graph named: "+frame.getGraph().getAbsolutePath()+"\n</desc>\n");
		
	}


	public void save() throws IOException {
		// we write the SVG header
		header();
		// then we paint the zone with the background color
		fillRect(0,0,graphicalZone.Width,graphicalZone.Height,info.backgroundColor);
		// if necessary, we draw the frame
		if (info.frame) {
			drawRect(10,10,graphicalZone.Width-20,graphicalZone.Height-20,info.foregroundColor,2);
		}
		// if necessary, we print the file name
		Font defaultFont=graphicalZone.getFont();
		if (info.filename) {
			if (info.pathname)
				drawText((frame.getGraph() != null) ? frame.getGraph().getAbsolutePath() : "", 
						20, graphicalZone.Height - 45,info.foregroundColor,
						defaultFont);
			else
				drawText((frame.getGraph() != null) ? frame.getGraph().getName() : "", 
						20, graphicalZone.Height - 45,info.foregroundColor,
						defaultFont);
		}
		// if necessary, we print the date of the day
		if (info.date) {
			drawText(new Date().toString(), 20, graphicalZone.Height - 25,
					info.foregroundColor,defaultFont);
		}
		// if necessary, we draw the grid
		if (graphicalZone.isGrid) {
			for (int x = 10; x < graphicalZone.Width - 20; x = x + graphicalZone.nPixels)
				for (int y = 10; y < graphicalZone.Height - 20; y = y + graphicalZone.nPixels)
					drawLine(x, y, x + 1, y,info.foregroundColor);
		}
		// we draw the transitions
		ArrayList<GenericGraphBox> graphBoxes=graphicalZone.graphBoxes;
		int L = graphBoxes.size();
		for (int i = 0; i < L; i++) {
			drawTransitions(graphBoxes.get(i));
		}
		// then we draw the boxes
		for (int i = 0; i < L; i++) {
			drawBox(graphBoxes.get(i));
		}

		// finally, we close the document
		writer.write("</svg>\n");
	}

	
	
	private void drawBox(GenericGraphBox g) throws IOException {
		graphics.setFont(info.input.font);
		h_ligne = graphics.getFontMetrics().getHeight();
		descent = graphics.getFontMetrics().getDescent();
		if (g.comment == true) {
			// if the box is in comment and not selected
			if (g.type == GenericGraphBox.FINAL)
				drawFinal(g);
			else if (g.type == GenericGraphBox.NORMAL)
				drawOtherComment(g);
			else
				drawInitial(g);
		} else {
			// the box is normal
			if (g.type == GenericGraphBox.FINAL)
				drawFinal(g);
			else if (g.type == GenericGraphBox.NORMAL)
				drawOther(g);
			else
				drawInitial(g);
		}
	}


	private void drawInitial(GenericGraphBox g) throws IOException {
		drawOther(g);
		if (info.rightToLeft == false)
			drawLine(g.X_in, g.Y_in, g.X_in - 10, g.Y_in,info.foregroundColor);
		else
			drawLine(g.X_out - 5, g.Y_out, g.X_out + 5, g.Y_out,info.foregroundColor);
	}


	private void drawOther(GenericGraphBox g) throws IOException {
		if (g.variable) {
			drawVariable(g);
			return;
		}
		if (g.contextMark) {
			drawContextMark(g);
			return;
		}
		if (g.morphologicalModeMark) {
            drawMorphologicalModeMark(g);
            return;
        }
		Color color=info.foregroundColor;
		// drawing the box
		if (g.n_lines == 0) {
			drawLine(g.X_in, g.Y_in, g.X_in + 15, g.Y_in,color);
			if (info.rightToLeft == false)
				drawLine(g.X_in + 15, g.Y1, g.X_in + 15, g.Y1 + g.Height,color);
			else
				drawLine(g.X_in, g.Y1, g.X_in, g.Y1 + g.Height,color);
		} else {
			fillRect(g.X1, g.Y1, g.Width, g.Height,info.backgroundColor);
			drawRect(g.X1, g.Y1, g.Width, g.Height,color,1);
		}
		// and the triangle if necessary
		if (g.hasOutgoingTransitions || g.type == GenericGraphBox.INITIAL) {
			if (info.rightToLeft == false) {
				int a = g.X1 + g.Width;
				int b = g.Y1 + g.Height;
				drawLine(g.X_out,g.Y_out, a, g.Y1,color);
				drawLine(a, g.Y1, a, b,color);
				drawLine(a, b, g.X_out, g.Y_out,color);
			} else {
				drawLine(g.X_in - 5, g.Y_in, g.X1, g.Y1,color);
				drawLine(g.X1, g.Y1, g.X1, g.Y1 + g.Height,color);
				drawLine(g.X1, g.Y1 + g.Height, g.X_in - 5, g.Y_in,color);
			}
		}
		// prints the lines of the box
		for (int i = 0; i < g.n_lines; i++) {
			Boolean is_greyed = g.greyed.get(i);
			String l = g.lines.get(i);
			if (is_greyed.booleanValue()) {
				fillRect(g.X1 + 2, g.Y1 + 4 + i * h_ligne, g.Width - 4, h_ligne,
						(l.startsWith(":")?info.packageColor:info.subgraphColor));
			}
			drawText(l,g.X1 + 5, g.Y1 - descent + 3 + (i + 1) * h_ligne,color,info.input.font);
		}
		// prints the transduction, if exists
		if (!g.transduction.equals("")) {
			drawText(g.transduction, g.X1 + 5, g.Y1 + g.Height
					+ graphics.getFontMetrics().getHeight(),info.foregroundColor,info.output.font);
		}
	}


	private void drawOtherComment(GenericGraphBox g) throws IOException {
		if (g.variable) {
			drawVariable(g);
			return;
		}
		if (g.contextMark) {
			drawContextMark(g);
			return;
		}
		// print lines if the box is empty
		Color color=info.commentColor;
		if (g.n_lines == 0) {
			drawLine(g.X_in, g.Y_in, g.X_in + 15, g.Y_in,color);
			if (info.rightToLeft == false)
				drawLine(g.X_in + 15, g.Y1, g.X_in + 15, g.Y1 + g.Height,color);
			else
				drawLine(g.X_in, g.Y1, g.X_in, g.Y1 + g.Height,color);
		} else {
			fillRect(g.X1, g.Y1, g.Width, g.Height,info.backgroundColor);
		}
		// prints the lines of the box
		for (int i = 0; i < g.n_lines; i++) {
			Boolean is_greyed = g.greyed.get(i);
			String l = g.lines.get(i);
			if (is_greyed.booleanValue()) {
				fillRect(g.X1 + 2, g.Y1 + 3 + i * h_ligne, g.Width - 4, h_ligne,
						(l.startsWith(":")?info.packageColor:info.subgraphColor));
			}
			drawText(l,g.X1 + 5, g.Y1 - descent + 3 + (i + 1) * h_ligne,color,info.input.font);
		}
		// prints the transduction, if exists
		if (!g.transduction.equals("")) {
			/* TODO faire hériter ceci ainsi que generic graphical zone d'une
			 * interface, afin d'être sûr de ne pas oublier de méthodes
			 * quand on change le rendu des graphes, par exemple pour 
			 * ajouter le mode morphologique
			 */
			drawText(g.transduction, g.X1 + 5, g.Y1 + g.Height
					+ graphics.getFontMetrics().getHeight(),info.foregroundColor,info.output.font);
		}
	}


	private void drawMorphologicalModeMark(GenericGraphBox g) throws IOException {
		Color color=info.morphologicalModeColor;
		graphics.setFont(GenericGraphBox.variableFont);
		drawText(g.lines.get(0),g.X1+5,
				g.Y1-graphics.getFontMetrics().getDescent() + graphics.getFontMetrics().getHeight(),
				color,GenericGraphBox.variableFont);
	}


	private void drawContextMark(GenericGraphBox g) throws IOException {
		Color color=info.contextColor;
		graphics.setFont(GenericGraphBox.variableFont);
		drawText(g.lines.get(0),g.X1+5,
				g.Y1-graphics.getFontMetrics().getDescent() + graphics.getFontMetrics().getHeight(),
				color,GenericGraphBox.variableFont);
	}


	private void drawVariable(GenericGraphBox g) throws IOException {
		Color color=info.commentColor;
		graphics.setFont(GenericGraphBox.variableFont);
		drawText(g.lines.get(0),g.X1+5,
				g.Y1-graphics.getFontMetrics().getDescent() + graphics.getFontMetrics().getHeight(),
				color,GenericGraphBox.variableFont);
		graphics.setFont(info.output.font);
		drawText(g.transduction,g.X1+10,
				g.Y1+g.Height+ graphics.getFontMetrics().getHeight(),
				color,info.output.font);
	}


	private void drawFinal(GenericGraphBox g) throws IOException {
		drawCircle(g.x+10,g.Y,10,info.foregroundColor,info.backgroundColor);
		drawRect(g.x+5,g.Y-5,10,10,info.foregroundColor,1);
	}


	private void drawCircle(int x, int y, int radius, Color foregroundColor, Color backgroundColor) throws IOException {
		writer.write("<circle cx=\""+x+"\" cy=\""+y+"\" r=\""+radius+"\" fill=\""+rgb(backgroundColor)+"\" stroke=\""+rgb(foregroundColor)+"\"/>\n");
	}


	private void drawTransitions(GenericGraphBox g) throws IOException {
		ArrayList<GenericGraphBox> transitions=g.getTransitions();
		int L = transitions.size();
		for (int i = 0; i < L; i++) {
			drawTransition(g,transitions.get(i));
		}
	}


	private void drawTransition(GenericGraphBox src, GenericGraphBox dest) throws IOException {
		Color color=info.foregroundColor;
		if (!info.rightToLeft) {
			if (dest.X_in > src.X_out) {
				// easiest case: drawing a line
				drawLine(src.X_out, src.Y_out, dest.X_in, dest.Y_in,color);
				//GraphicalToolBox.drawLine(g, src.X_out, src.Y_out, dest.X_in, dest.Y_in);
				return;
			}
			if (src.equals(dest)) {
				// if the box is relied to itself
				int diametre1 = 10 + src.Height / 2;
				int radius=diametre1/2;
				drawArc1(src.X_out,src.Y_out,radius,color);
				drawArc2(src.X_out+radius,src.Y_out-radius,radius,color);
				drawArc3(src.X_in,src.Y_in-2*radius,radius,color);
				drawArc4(src.X_in-radius,src.Y_in-radius,radius,color);
				drawLine(src.X_out,src.Y_out-2*radius,src.X_in,src.Y_in-2*radius,color);
				return;
			}
			if ((src.Y1 < (dest.Y1 + dest.Height)) && ((src.Y1 + src.Height) > dest.Y1)) {
				int radius1=(10 + src.Height / 2)/2;
				drawArc1(src.X_out,src.Y_out,radius1,color);
				drawArc2(src.X_out+radius1,src.Y_out-radius1,radius1,color);
				int radius2=(10 + dest.Height / 2)/2;
				drawArc3(dest.X_in,dest.Y_in-2*radius2,radius2,color);
				drawArc4(dest.X_in-radius2,dest.Y_in-radius2,radius2,color);
				int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
				Xpoint1 = dest.X_in;
				Ypoint1 = dest.Y_in - 2*radius2;
				Xpoint2 = src.X_out;
				Ypoint2 = src.Y_out - 2*radius1;
				int Xmilieu, Ymilieu, largeurLimite;
				Xmilieu = (Xpoint2 + Xpoint1) / 2;
				Ymilieu = (Ypoint2 + Ypoint1) / 2;
				largeurLimite = 2*radius1 + 2*radius2;
				drawCurve(Xpoint1, Ypoint1, Xpoint1 + largeurLimite,
						Ypoint1, Xmilieu, Ymilieu,color);
				drawCurve(Xpoint2, Ypoint2, Xpoint2 - largeurLimite,
						Ypoint2, Xmilieu, Ymilieu,color);
				return;
			}
			if (src.Y1 < (dest.Y1 + dest.Height)) {
				int radius1=(10 + src.Height / 2)/2;
				drawArc2(src.X_out+radius1,src.Y_out+radius1,radius1,color);
				int radius2 = (10 + dest.Height / 2)/2;
				drawArc4(dest.X_in-radius2,dest.Y_in-radius2,radius2,color);
				int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
				Xpoint1 = dest.X_in - 2*radius2 / 2;
				Ypoint1 = dest.Y_in - 2*radius2 / 2;
				Xpoint2 = src.X_out + 2*radius1 / 2;
				Ypoint2 = src.Y_out + 2*radius1 / 2;
				int hauteurLimite = 2*radius1 + 2*radius2 - 20;
				int Xmilieu, Ymilieu;
				Xmilieu = (Xpoint2 + Xpoint1) / 2;
				Ymilieu = (Ypoint2 + Ypoint1) / 2;
				drawCurve(Xpoint1, Ypoint1, Xpoint1,
						Ypoint1 - hauteurLimite, Xmilieu, Ymilieu,color);
				drawCurve(Xpoint2, Ypoint2, Xpoint2,
						Ypoint2 + hauteurLimite, Xmilieu, Ymilieu,color);
				return;
			}
			int radius1 = (10 + src.Height / 2)/2;
			drawArc1(src.X_out,src.Y_out,radius1,color);
			int radius2 = (10 + dest.Height / 2)/2;
			drawArc3(dest.X_in,dest.Y_in,radius2,color);
			int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
			Xpoint1 = dest.X_in - 2*radius2 / 2;
			Ypoint1 = dest.Y_in + 2*radius2 / 2;
			Xpoint2 = src.X_out + 2*radius1 / 2;
			Ypoint2 = src.Y_out - 2*radius1 / 2;
			int hauteurLimite = 2*radius1 + 2*radius2 - 20;
			int Xmilieu, Ymilieu;
			Xmilieu = (Xpoint2 + Xpoint1) / 2;
			Ymilieu = (Ypoint2 + Ypoint1) / 2;
			drawCurve(Xpoint1, Ypoint1, Xpoint1, Ypoint1 + hauteurLimite,
					Xmilieu, Ymilieu,color);
			drawCurve(Xpoint2, Ypoint2, Xpoint2, Ypoint2 - hauteurLimite,
					Xmilieu, Ymilieu,color);
			return;
		}
		// end of the left to right mode
		if (dest.X_out - 5 < src.X_in - 5) {
			// easiest case: drawing a line
			drawLine(src.X_in - 5, src.Y_in, dest.X_out - 5, dest.Y_out,color);
			return;
		}
		if (src.equals(dest)) {
			// if the box is relied to itself
			int radius = (10 + src.Height / 2)/2;
			drawArc1(src.X_out-5,src.Y_out,radius,color);
			drawArc2(src.X_out-5+radius,src.Y_out-radius,radius,color);
			drawArc3(src.X_in-5,src.Y_out-2*radius,radius,color);
			drawArc4(src.X_in-5-radius,src.Y_out-radius,radius,color);
			drawLine(src.X_out-5,src.Y_out-2*radius,src.X_in-5,src.Y_out-2*radius,color);
			return;
		}
		if ((src.Y1 < (dest.Y1 + dest.Height)) && ((src.Y1 + src.Height) > dest.Y1)) {
			int radius1 = (10 + src.Height / 2)/2;
			int radius2 = (10 + dest.Height / 2)/2;
			drawArc3(src.X_in-5,src.Y_in-2*radius1,radius1,color);
			drawArc4(src.X_in-5-radius1,src.Y_in-radius1,radius1,color);
			drawArc1(dest.X_out-5,dest.Y_out,radius2,color);
			drawArc2(dest.X_out-5+radius2,dest.Y_out-radius2,radius2,color);
			
			int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
			Xpoint2 = dest.X_out - 5;
			Ypoint2 = dest.Y_out - 2*radius2;
			Xpoint1 = src.X_in - 5;
			Ypoint1 = src.Y_in - 2*radius1;
			int Xmilieu, Ymilieu, largeurLimite;
			Xmilieu = (Xpoint2 + Xpoint1) / 2;
			Ymilieu = (Ypoint2 + Ypoint1) / 2;
			largeurLimite = 2*radius1 + 2*radius2;
			drawCurve(Xpoint1, Ypoint1, Xpoint1 + largeurLimite, Ypoint1,
					Xmilieu, Ymilieu,color);
			drawCurve(Xpoint2, Ypoint2, Xpoint2 - largeurLimite, Ypoint2,
					Xmilieu, Ymilieu,color);
			return;
		}
		if (src.Y1 < (dest.Y1 + dest.Height)) {
			int radius1 = (10 + src.Height / 2)/2;
			drawArc3(src.X_in-5,src.Y_in,radius1,color);
			int radius2 = (10 + dest.Height / 2)/2;
			drawArc1(dest.X_out-5,dest.Y_out,radius2,color);
			int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
			Xpoint1 = dest.X_out - 5 + 2*radius2 / 2;
			Ypoint1 = dest.Y_out - 2*radius2 / 2;
			Xpoint2 = src.X_in - 5 - 2*radius1 / 2;
			Ypoint2 = src.Y_in + 2*radius1 / 2;
			int hauteurLimite = 2*radius1 + 2*radius2 - 20;
			int Xmilieu, Ymilieu;
			Xmilieu = (Xpoint2 + Xpoint1) / 2;
			Ymilieu = (Ypoint2 + Ypoint1) / 2;
			drawCurve(Xpoint1, Ypoint1, Xpoint1, Ypoint1 - hauteurLimite,
					Xmilieu, Ymilieu,color);
			drawCurve(Xpoint2, Ypoint2, Xpoint2, Ypoint2 + hauteurLimite,
					Xmilieu, Ymilieu,color);
			return;
		}
		int radius1 = (10 + src.Height / 2)/2;
		drawArc4(src.X_in-5-radius1,src.Y_in-radius1,radius1,color);
		int radius2 = (10 + dest.Height / 2)/2;
		drawArc2(dest.X_out-5+radius2,dest.Y_out+radius2,radius2,color);
		int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
		Xpoint1 = dest.X_out - 5 + 2*radius2 / 2;
		Ypoint1 = dest.Y_out + 2*radius2 / 2;
		Xpoint2 = src.X_in - 5 - 2*radius1 / 2;
		Ypoint2 = src.Y_in - 2*radius1 / 2;
		int hauteurLimite = 2*radius1 + 2*radius2 - 20;
		int Xmilieu, Ymilieu;
		Xmilieu = (Xpoint2 + Xpoint1) / 2;
		Ymilieu = (Ypoint2 + Ypoint1) / 2;
		drawCurve(Xpoint1, Ypoint1, Xpoint1, Ypoint1 + hauteurLimite,
				Xmilieu, Ymilieu,color);
		drawCurve(Xpoint2, Ypoint2, Xpoint2, Ypoint2 - hauteurLimite,
				Xmilieu, Ymilieu,color);
	}


	private void drawCurve(int x1, int y1, int x2, int y2, int xCtrl, int yCtrl, Color color) throws IOException {
		writer.write("<path d=\"M"+x1+","+y1+" Q"+x2+","+y2+" "+xCtrl+","+yCtrl+"\" fill=\"none\" stroke=\""+rgb(color)+"\"/>\n");
	}

	/**
	 * Draws an arc of 90� as follows, where $ is the point of coordinates (x,y):
	 * 
	 *        X
	 *        X
	 *        X
	 *      XX
	 *  $XXX 
	 *  
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @throws IOException 
	 */
	private void drawArc1(int x, int y, int radius, Color color) throws IOException {
		writer.write("<path d=\"M"+x+","+y+" a"+radius+",-"+radius+" 0 0,0 "+radius+",-"+radius+"\" fill=\"none\" stroke=\""+rgb(color)+"\"/>\n");
	}

	
	/**
	 * Draws an arc of 90� as follows, where $ is the point of coordinates (x,y):
	 * 
	 * XXXX 
	 *     XX
	 *       X
	 *       X
	 *       $
	 *  
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @throws IOException 
	 */
	private void drawArc2(int x, int y, int radius, Color color) throws IOException {
		writer.write("<path d=\"M"+x+","+y+" a-"+radius+",-"+radius+" 0 0,0 -"+radius+",-"+radius+"\" fill=\"none\" stroke=\""+rgb(color)+"\"/>\n");
	}

	/**
	 * Draws an arc of 90� as follows, where $ is the point of coordinates (x,y):
	 * 
	 *     XXX$ 
	 *   XX
	 *  X
	 *  X
	 *  X
	 *  
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @throws IOException 
	 */
	private void drawArc3(int x, int y, int radius, Color color) throws IOException {
		writer.write("<path d=\"M"+x+","+y+" a-"+radius+","+radius+" 0 0,0 -"+radius+","+radius+"\" fill=\"none\" stroke=\""+rgb(color)+"\"/>\n");
	}

	/**
	 * Draws an arc of 90� as follows, where $ is the point of coordinates (x,y):
	 * 
	 *  $
	 *  X
	 *  X
	 *   XX
	 *     XXXX 
	 *  
	 * @param x
	 * @param y
	 * @param radius
	 * @param color
	 * @throws IOException 
	 */
	private void drawArc4(int x, int y, int radius, Color color) throws IOException {
		writer.write("<path d=\"M"+x+","+y+" a"+radius+","+radius+" 0 0,0 "+radius+","+radius+"\" fill=\"none\" stroke=\""+rgb(color)+"\"/>\n");
	}

	


	private void drawLine(int x1, int y1, int x2, int y2, Color color) throws IOException {
		writer.write("<line stroke=\""+rgb(color)+"\"  x1=\""+x1+"\" y1=\""+y1+"\" x2=\""+x2+"\" y2=\""+y2+"\"/>\n");
	}


	private void drawText(String text, int x, int y, Color color, Font font) throws IOException {
		writer.write("<text x=\""+x+"\" y=\""+y+"\" font-family=\""+font.getFamily()+"\" font-size=\""+font.getSize()+"\" fill=\""+rgb(color)+"\">");
		writer.write(replaceXMLChars(text));
		writer.write("</text>\n");
	}


	private String replaceXMLChars(String text) {
		String s=text.replaceAll("&","&amp;");
		s=s.replaceAll("<","&lt;");
		return s.replaceAll(">","&gt;");
	}


	/**
	 * src method fill a rectangle with a given color but does not draw its outline.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @throws IOException
	 */
	private void fillRect(int x, int y, int width, int height, Color color) throws IOException {
		writer.write("<rect stroke=\"none\" fill=\""+rgb(color)+"\" x=\""+x+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\"/>\n");		
	}

	/**
	 * src method draws a rectangle with given color and stroke width, but does not
	 * fill it.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @param strokeWidth
	 * @throws IOException
	 */
	private void drawRect(int x, int y, int width, int height, Color color,int strokeWidth) throws IOException {
		writer.write("<rect stroke=\""+rgb(color)+"\" stroke-width=\""+strokeWidth+"\" fill=\"none\" x=\""+x+"\" y=\""+y+"\" width=\""+width+"\" height=\""+height+"\"/>\n");		
	}


	private String rgb(Color color) {
		return "rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")";
	}


}
