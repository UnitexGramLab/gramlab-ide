/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractListModel;


public class HexListModel extends AbstractListModel {

	private byte[] data;
	private boolean isUTF16LE;
	private int nBytesPerLine=16;
	
	public HexListModel(byte[] b) {
		this.data=b;
		isUTF16LE=(b.length>2 && b.length%2==0 && b[0]==(byte)0xFF && b[1]==(byte)0xFE);
	}

	public static HexListModel createModel(File f) {
		if (!f.exists()) return null;
		try {
			byte[] b=new byte[(int) f.length()];
			new FileInputStream(f).read(b);
			return new HexListModel(b);
		} catch (OutOfMemoryError e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	
	
	public static void main(String[] args) {
		HexListModel m=HexListModel.createModel(new File("/home/paumier/tmp/concord_uima/Corpus17/unitexInput.snt"));
		int size=m.getSize();
		for (int i=0;i<size;i++) {
			System.err.println(m.getElementAt(i));
		}
	}

	private StringBuilder builder=new StringBuilder();

	public String getElementAt(int index) {
		builder.setLength(0);
		if (isUTF16LE) {
			int start=index*nBytesPerLine;
			int end=(index+1)*nBytesPerLine;
			if (end>data.length) end=data.length;
			for (int i=start;i<end;i+=2) {
				builder.append(String.format("%2$02X%1$02X ",data[i],data[i+1]));
			}
			for (int i=end;i<(index+1)*nBytesPerLine;i+=2) {
				builder.append("     ");
			}
			builder.append(' ');
			for (int i=start;i<end;i+=2) {
				int n=(0xFF&data[i+1])<<8|(0xFF&data[i]);
				char c=(char)((n>=32)?n:'.');
				builder.append(c);
			}
			return builder.toString();
		}
		int start=index*nBytesPerLine;
		int end=(index+1)*nBytesPerLine;
		if (end>data.length) end=data.length;
		for (int i=start;i<end;i++) {
			builder.append(String.format("%1$02X ",data[i]));
		}
		for (int i=end;i<(index+1)*nBytesPerLine;i++) {
			builder.append("   ");
		}
		builder.append(' ');
		for (int i=start;i<end;i++) {
			int n=0xFF&data[i];
			char c=(char) ((n>=32)?n:'.');
			builder.append(c);
		}
		for (int i=end;i<(index+1)*nBytesPerLine;i++) {
			builder.append(' ');
		}
		return builder.toString();
	}

	public int getSize() {
		return data.length/nBytesPerLine+(data.length%nBytesPerLine);
	}
	
}
