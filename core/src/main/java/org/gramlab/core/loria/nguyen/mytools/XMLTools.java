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
 * @(#)       XMLTools.java
 * 
 * Created    Sat Feb 23 17:07:06 2002
 * 
 * Author     Nguyen Thi Minh Huyen
 *            UMR LORIA (Universities of Nancy, CNRS & INRIA)
 *           
 */
package org.gramlab.core.loria.nguyen.mytools;

import java.io.FileOutputStream;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@SuppressWarnings("deprecation")
public class XMLTools {
	public static final String STD_HEADER = "<?xml version=\"1.0\" encoding = \"UTF-8\"?>\n\n";
	public static final String parserClass = "org.apache.xerces.parsers.SAXParser";

	public static String getTextValue(Node node) {
		Node item = node.getFirstChild();
		while (item != null && item.getNodeType() != Node.TEXT_NODE)
			item = item.getNextSibling();
		if (item == null)
			return "";
		return item.getNodeValue();
	}

	public static void saveDocument(String fileName, Document document,
			String enc) {
		try {
			final OutputFormat format = new OutputFormat(document);
			// format.setLineWidth(70);
			format.setIndenting(true);
			format.setIndent(3);
			format.setEncoding(enc);
			// format.setDoctype("SYSTEM", "token.dtd");
			format.setMediaType("application/xml");
			format.setOmitComments(true);
			format.setOmitXMLDeclaration(false);
			format.setVersion("1.0");
			format.setStandalone(false);
			final FileOutputStream fileOutStrm = new FileOutputStream(fileName);
			final XMLSerializer ser = new XMLSerializer(fileOutStrm, format);
			ser.serialize(document);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static String toString(DocumentImpl xdoc) {
		String ret = null;
		text = "";
		walk(xdoc);
		ret = text;
		text = null;
		return ret;
	}

	private static String text = null;

	public static String getText(Node node) {
		String ret = null;
		text = "";
		walkText(node);
		ret = text;
		text = null;
		return ret;
	}

	private static void walkText(Node node) {
		final int type = node.getNodeType();
		if (type == Node.TEXT_NODE)
			text += node.getNodeValue();
		// recurse
		for (Node child = node.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			walkText(child);
		}
	}// end of walkText

	private static void walk(Node node) {
		final int type = node.getNodeType();
		switch (type) {
		case Node.ELEMENT_NODE: {
			text += '<' + node.getNodeName();
			final NamedNodeMap nnm = node.getAttributes();
			if (nnm != null) {
				final int len = nnm.getLength();
				Attr attr;
				for (int i = 0; i < len; i++) {
					attr = (Attr) nnm.item(i);
					text += ' ' + attr.getNodeName() + "=\""
							+ attr.getNodeValue() + '"';
				}
			}
			text += '>';
			break;
		}// end of element
		case Node.ENTITY_REFERENCE_NODE: {
			text += '&' + node.getNodeName() + ';';
			break;
		}// end of entity
		case Node.CDATA_SECTION_NODE: {
			text += "<![CDATA[" + node.getNodeValue() + "]]>";
			break;
		}
		case Node.TEXT_NODE: {
			text += node.getNodeValue();
			break;
		}
		case Node.PROCESSING_INSTRUCTION_NODE: {
			text += "<?" + node.getNodeName();
			final String data = node.getNodeValue();
			if (data != null && data.length() > 0) {
				text += ' ' + data;
			}
			text += "?>";
			break;
		}
		}// end of switch
			// recurse
		for (Node child = node.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			walk(child);
		}
		// without this the ending tags will miss
		if (type == Node.ELEMENT_NODE) {
			text += "</" + node.getNodeName() + ">";
		}
	}// end of walk
}
