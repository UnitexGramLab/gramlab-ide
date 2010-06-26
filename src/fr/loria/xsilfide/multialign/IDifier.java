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

package fr.loria.xsilfide.multialign;

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
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.loria.nguyen.mytools.FileIO;
import fr.loria.nguyen.mytools.XMLTools;

//import javax.swing.*;

// IDifier -- add id's to all PHRASE elements in a document.  Useful
// when aligning a document which has no ID's on its PHRASE elements.
// IDifier will not destroy ID's which already exist.

@SuppressWarnings({ "unchecked", "deprecation" })
public class IDifier extends org.xml.sax.helpers.DefaultHandler {

    XMLReader parser;
    final Hashtable types;
    InputStream in;
    OutputStream out;
    PrintStream sysout;
    DocumentImpl doc;
    NodeImpl curNode;
    int n;
    final boolean force;

    public IDifier(Hashtable t, boolean force) {
        types = t;
        this.force = force;
        try {
            parser = XMLReaderFactory.createXMLReader(XMLTools.parserClass);
            parser.setEntityResolver(this);
            parser.setDTDHandler(this);
            parser.setErrorHandler(this);
            parser.setContentHandler(this);
        } catch (SAXException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void idify(String fileIn, InputStream in1, OutputStream out1) {
        this.in = in1;
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
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public static void usage() {
        System.out.println("Usage: idify [-f] property-file [input [output] ]");
        System.out.println("Add id's to some elements in a document.");
        System.out.println("If input or output is not given, standard input or output is used.");
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
            if (args.length == 1) usage();
        }

        Properties p = FileIO.loadPROPS(args[arg]);

        IDifier id = new IDifier(p, force);

        InputStream input = System.in;
        OutputStream output = System.out;

        if (args.length - arg > 1) try {
            fileIn = args[arg + 1];
            input = new FileInputStream(fileIn);
        } catch (IOException e) {
            System.err.println("Can't open input " + fileIn + ": " + e);
            System.exit(1);
        }

        if (args.length - arg > 2) try {
            output = new FileOutputStream(args[arg + 2]);
        } catch (IOException e) {
            System.err.println("Can't open output " + args[arg + 2] + ": " + e);
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
            Writer fw = new OutputStreamWriter(out, "UTF-8");
            //fw.write(XMLTools.STD_HEADER);
            // Pb les strings java sont limitees en taille a
            // 2 puissance 16 caracteres....
            //System.out.println(XMLTools.toString(doc));
            //
            XMLSerializer xmSer = new XMLSerializer(fw, new OutputFormat());
            Element root = doc.getDocumentElement();
            xmSer.serialize(root);

            // On pourrait remettre les trucs de Huyen,
            // la bonne facon de faire est :
            //   XMLTools.saveDocument(...)
            //fw.write(XMLTools.toString(doc));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc = null;
        System.setOut(sysout);
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes attrs)
            throws SAXException {
        if ("".equals(uri))
            name = qName;
        try {
            ElementImpl elt = new ElementImpl(doc, name);
            for (int i = 0; i < attrs.getLength(); i++)
                elt.setAttribute(attrs.getQName(i), attrs.getValue(i));
            curNode.appendChild(elt);
            curNode = elt;
            if (isPhrase(name)
                    && (force || elt.getAttribute("xml:id") == null)) {
                elt.setAttribute("xml:id", "n" + (++n));
                System.out.println("Found a isPhrase : " + name + elt.getAttribute("xml:id"));

            }
        } catch (Exception e) {
            System.err.println(name + ": startElement: ");
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String name, String qName) throws SAXException {
        if ("".equals(uri))
            name = qName;
        // XXX: I assume we have a legal document, i.e. all elements are nested.
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
        String val = (String) types.get(name);
        return val != null && val.equals("PHRASE");
    }
}
