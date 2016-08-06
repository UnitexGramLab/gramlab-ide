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
 * @(#)       IDifier.java
 * 
 * Created    Thu Sep 30 10:44:27 MEST 1999
 * 
 * Copyright  1999 (C) Sean O'ROURKE
 *            UMR LORIA (Universities of Nancy, CNRS & INRIA)
 *       
 * Modifs : Bertrand Gaiffe ATILF.
 * idifier does not work on p5 documents. why ?
 * removed partly Huen's XMLTools from the game....
 * We keep (at the moment) the reader part of it
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.gramlab.core.loria.nguyen.mytools.FileIO;
import org.gramlab.core.loria.nguyen.mytools.XMLTools;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

//import javax.swing.*;
// IDifier -- add id's to all PHRASE elements in a document.  Useful
// when aligning a document which has no ID's on its PHRASE elements.
// IDifier will not destroy ID's which already exist.
@SuppressWarnings({ "deprecation" })
public class IDifier extends org.xml.sax.helpers.DefaultHandler {
	private XMLReader parser;
	private final Hashtable<Object, Object> types;
	private OutputStream out;
	private PrintStream sysout;
	private DocumentImpl doc;
	private NodeImpl curNode;
	private int n;
	private final boolean force;

	private IDifier(Hashtable<Object, Object> t, boolean force) {
		types = t;
		this.force = force;
		try {
			parser = XMLReaderFactory.createXMLReader(XMLTools.parserClass);
			parser.setEntityResolver(this);
			parser.setDTDHandler(this);
			parser.setErrorHandler(this);
			parser.setContentHandler(this);
		} catch (final SAXException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	void idify(String fileIn, InputStream in1, OutputStream out1) {
		this.out = out1;
		try {
			InputSource is = null;
			if (fileIn.equals(""))
				is = new InputSource(in1);
			else {
				is = new InputSource(FileIO.openLargeInput(fileIn));
			}
			is.setSystemId(fileIn);
			System.out.println("Parsing...");
			parser.parse(is);
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	private static void usage() {
		System.out.println("Usage: idify [-f] property-file [input [output] ]");
		System.out.println("Add id's to some elements in a document.");
		System.out
				.println("If input or output is not given, standard input or output is used.");
		System.out.println("With -f, replace existing ID's.");
		System.exit(0);
	}

	public static void main(String args[]) {
		if (args.length == 0) {
			usage();
		}
		boolean force = false;
		int arg = 0;
		String fileIn = "";
		if (args[0].equals("-f")) {
			force = true;
			arg = 1;
			if (args.length == 1)
				usage();
		}
		final Properties p = FileIO.loadPROPS(args[arg]);
		final IDifier id = new IDifier(p, force);
		InputStream input = System.in;
		OutputStream output = System.out;
		if (args.length - arg > 1)
			try {
				fileIn = args[arg + 1];
				input = new FileInputStream(fileIn);
			} catch (final IOException e) {
				System.err.println("Can't open input " + fileIn + ": " + e);
				System.exit(1);
			}
		if (args.length - arg > 2)
			try {
				output = new FileOutputStream(args[arg + 2]);
			} catch (final IOException e) {
				System.err.println("Can't open output " + args[arg + 2] + ": "
						+ e);
				System.exit(1);
			}
		id.idify(fileIn, input, output);
	}

	/*
	 * DocumentHandler implementation.
	 */
	@Override
	public void startDocument() {
		n = 0;
		doc = new DocumentImpl();
		curNode = doc;
		sysout = System.out;
		System.setOut(System.err);
	}

	@Override
	public void endDocument() {
		try {
			final Writer fw = new OutputStreamWriter(out, "UTF-8");
			// fw.write(XMLTools.STD_HEADER);
			// Pb les strings java sont limitees en taille a
			// 2 puissance 16 caracteres....
			// System.out.println(XMLTools.toString(doc));
			//
			final XMLSerializer xmSer = new XMLSerializer(fw,
					new OutputFormat());
			final Element root = doc.getDocumentElement();
			xmSer.serialize(root);
			// On pourrait remettre les trucs de Huyen,
			// la bonne facon de faire est :
			// XMLTools.saveDocument(...)
			// fw.write(XMLTools.toString(doc));
			fw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		doc = null;
		System.setOut(sysout);
	}

	@Override
	public void startElement(String uri, String name, String qName,
			Attributes attrs) throws SAXException {
		if ("".equals(uri))
			name = qName;
		try {
			final ElementImpl elt = new ElementImpl(doc, name);
			for (int i = 0; i < attrs.getLength(); i++)
				elt.setAttribute(attrs.getQName(i), attrs.getValue(i));
			curNode.appendChild(elt);
			curNode = elt;
			if (isPhrase(name) && (force || elt.getAttribute("xml:id") == null)) {
				elt.setAttribute("xml:id", "n" + (++n));
				System.out.println("Found a isPhrase : " + name
						+ elt.getAttribute("xml:id"));
			}
		} catch (final Exception e) {
			System.err.println(name + ": startElement: ");
			e.printStackTrace();
		}
	}

	@Override
	public void endElement(String uri, String name, String qName)
			throws SAXException {
		if ("".equals(uri))
			name = qName;
		// I assume we have a legal document, i.e. all elements are nested.
		curNode = (NodeImpl) curNode.getParentNode();
	}

	@Override
	public void characters(char[] buf, int start, int len) {
		curNode.appendChild(new TextImpl(doc, new String(buf, start, len)));
	}

	@Override
	public void ignorableWhitespace(char[] buf, int start, int len) {
		curNode.appendChild(new TextImpl(doc, new String(buf, start, len)));
	}

	private boolean isPhrase(String name) {
		final String val = (String) types.get(name);
		return val != null && val.equals("PHRASE");
	}
}
