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
 * @(#)       LoadAndPrepareTexts.java
 * 
 * Created    Sat Sep 11 17:34:28 1999
 * 
 * Copyright  1999 (C) PATRICE BONHOMME
 *            UMR LORIA (Universities of Nancy, CNRS & INRIA)
 *            
 */


/* Bertrand.Gaiffe@atilf.fr le 7 décembre 2006 :
   J'ai besoin de quelque chose de plus modulaire :
      -> découpage de la fonction de lecture de deux textes
      pout obtenir un fonction de lecture d'un texte */




package fr.loria.xsilfide.multialign;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import fr.loria.nguyen.mytools.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Stack;
import java.util.ArrayList;


/**
 * Load and prepared 2 texts.
 * 
 * 
 * @author    Patrice Bonhomme
 * @version   
 */



public class LoadAndPrepareTexts {
    // Element Properties : 
    static final short EMPTY  = 0;
    static final short IGNORE = 1;
    static final short PHRASE = 2;
    static final short PARAG  = 3;
    static final short DIV    = 4;
    static final short SEQ    = 5;
    static final short TRANSP = 6;
    static final short BODY = 7;
    
    static final String S_IGNORE	= "IGNORE";
    static final String S_PHRASE	= "PHRASE";
    static final String S_PARAG	= "PARAG";
    static final String S_DIV	= "DIV";
    static final String S_SEQ	= "SEQ";
    static final String S_TRANSP	= "TRANSP";
    static final String S_BODY = "BODY";
    

    /* Les deux classes suivantes sont uniquement là pour
       afficher non seulement les id "internes" de XAlign mais également
       les id originaux tirés des fichiers source, respectivement target */
    class MyVectorSrc extends Vector{
	

	public String toString(){
	    String res="[";
	    Object cour;
	    String sRes;
	    Vector bidon = new Vector();
	    MyVectorSrc temp;

	    // Ok, c'est une horreur !
	    for (Enumeration e = this.elements(); e.hasMoreElements();){
		cour = e.nextElement();
		if (cour.getClass() == bidon.getClass()){
		    temp = new MyVectorSrc();
		    for (Enumeration e2 = ((Vector)cour).elements();
			 e2.hasMoreElements();){
			temp.addElement(e2.nextElement());
		    }
		    sRes = temp.toString();
		}
		else{
		    sRes = cour.toString();
		}
		if (idSrc.get(cour) != null){
		    sRes = sRes + "("+idSrc.get(cour)+")";
		}
		res = res + sRes;
		if (e.hasMoreElements()){
		    res = res + ", ";
		}
	    }
	    res = res + "]";
	    return res;
	}
    }

    class MyVectorTar extends Vector{
	

	public String toString(){
	    String res="[";
	    Object cour;
	    String sRes;
	    Vector bidon = new Vector();
	    MyVectorTar temp;

	    // Ok, c'est une horreur !
	    for (Enumeration e = this.elements(); e.hasMoreElements();){
		cour = e.nextElement();
		if (cour.getClass() == bidon.getClass()){
		    temp = new MyVectorTar();
		    for (Enumeration e2 = ((Vector)cour).elements();
			 e2.hasMoreElements();){
			temp.addElement(e2.nextElement());
		    }
		    sRes = temp.toString();
		}
		else{
		    sRes = cour.toString();
		}
		if (idSrc.get(cour) != null){
		    sRes = sRes + "("+idTar.get(cour)+")";
		}
		res = res + sRes;
		if (e.hasMoreElements()){
		    res = res + ", ";
		}
	    }
	    res = res + "]";
	    return res;
	}
    }

    



    // Big block of public ugliness to return things:
    public Vector stcSrc = new MyVectorSrc(), stcTar = new MyVectorTar(); //for sentences
    public Vector paraSrc = new MyVectorSrc(), paraTar = new MyVectorTar();//for paragraphs
    public Vector divSrc = new MyVectorSrc(), divTar = new MyVectorTar();//for division
    
    // SEAN: ID remapping.
    public Hashtable idSrc = new Hashtable();
    public Hashtable idTar = new Hashtable();
    
    public String lsource = "", ltarget = "";

    public String uriSource, uriTarget;
    private ArrayList<String> fakeIdsSource;
    private ArrayList<String> fakeIdsTarget;
  
    // SEAN: "prop" maps tags -> types.
    String psource, ptarget;
    Properties prop; // variable containing properties saved in a file
    
    
    public LoadAndPrepareTexts(Vector dSrc, Vector parSrc, Vector senSrc,
			       Vector dTar, Vector parTar, Vector senTar,
			       String uSrc, String uTar){
	stcSrc = senSrc;
	stcTar = senTar;
	paraSrc = parSrc;
	paraTar = parTar;
	divSrc = dSrc;
	divTar = dTar;
	uriSource = uSrc;
	uriTarget = uTar;
    }

    public LoadAndPrepareTexts(String sfile, String tfile,
			       String psource, String ptarget,
			       String outfile)  {
	this(sfile, tfile, psource, ptarget);
	uriSource = sfile;
	uriTarget = tfile;
	savePreparedTexts(outfile);
    }
    
    public LoadAndPrepareTexts(String sfile, String tfile,
			       String ps, String pt){
	psource = ps;
	ptarget = pt;
	uriSource = sfile;
	uriTarget = tfile;
	loadAndPrepareTexts(sfile, tfile, "", "", true);
    }

    public LoadAndPrepareTexts(String sfile, String tfile,
			       String ps, String pt, String idEnglobantSource, String idEnglobantCible){
	psource = ps;
	ptarget = pt;
	uriSource = sfile;
	uriTarget = tfile;
	loadAndPrepareTexts(sfile, tfile, idEnglobantSource, idEnglobantCible,
			    true);
    }

    public LoadAndPrepareTexts(String sfile, String tfile,
			       String idEnglobantSource, 
			       String idEnglobantCible,
			       Properties prop){
	uriSource = sfile;
	uriTarget = tfile;
	setProperties(prop);
	loadAndPrepareTexts(sfile, tfile, idEnglobantSource, 
			    idEnglobantCible, false);
	
    }

    /* BG : */
    /* idEnglobant : si "" on ne change rien à l'ancien comportement, 
       si != "" on ne lit que ce qui est sous le champ de cet id */

    private final ArrayList<String> loadAndPrepareAText(String fileName, 
							Vector sentences, 
							Vector paragraphs,
							Vector divisions, 
							Hashtable ids, 
							String idEnglobant,
							boolean withProperties){
	XMLReader parser = null;
	ArrayList<String> res = new ArrayList<String>();

	System.out.println("Preparing "+fileName+" with idEnglobant = "+idEnglobant+"\n");

	try { 
	    parser = XMLReaderFactory.createXMLReader(XMLTools.parserClass); 
	}  
	catch (SAXException e) {
	    System.err.println("lAPT: " + e);
	    e.printStackTrace();
	    System.exit(-1);
	}
	MAHandler handler = new MAHandler(sentences, paragraphs, divisions, ids, idEnglobant, res);
	parser.setEntityResolver(handler);
	parser.setDTDHandler(handler);
	parser.setErrorHandler(handler);
	parser.setContentHandler(handler);
	
	System.out.println("Parsing '" + fileName + "'...");
	try {
	    InputSource is = new InputSource(FileIO.openLargeInput(fileName));
	    is.setEncoding("UTF-8");
	    if (withProperties){
		initXMLProperties(psource);
	    }
	    is.setSystemId(fileName);      
	    parser.parse(is);
	    lsource = handler.lang;
	} catch(Exception e) {
	    System.err.println("source: " + e);
	    e.printStackTrace();
	}
	System.out.println("Done.");
	if(handler.nospec.size() > 0) {
	    System.err.println("Unspecified tags: " + handler.nospec.toString());
	}
	return res;
    }

    private final void loadAndPrepareTexts(String sfile, String tfile, 
					   String idEnglobantSource,
					   String idEnglobantCible,
					   boolean withProperties)  {

	uriSource = sfile;
	uriTarget = tfile;
	

	fakeIdsSource = loadAndPrepareAText(sfile, stcSrc, paraSrc, divSrc, idSrc, idEnglobantSource, withProperties);
	fakeIdsTarget = loadAndPrepareAText(tfile, stcTar, paraTar, divTar, idTar, idEnglobantCible, withProperties);


	// add the xYYY's for xptrs:
	int i=1;
	for(Enumeration e = stcTar.elements(); e.hasMoreElements(); ) {
	    Vector v = (Vector)e.nextElement();
	    v.insertElementAt("x" + i++, 0);
	}
	if (divSrc.size() == 0){
	    System.err.println("No divisions in  source file?\nBye.\n");
	    System.exit(1);
	}
	String divsize = "" + ((Vector)divSrc.elementAt(0)).size();
	for(i=1; i<divSrc.size(); i++)
	    divsize += ", " + ((Vector)divSrc.elementAt(i)).size();
	MultiAlign.debug("Source: [" + divsize + "] divs, "
			 + paraSrc.size() + " paras, "
			 + stcSrc.size() + " stcs.");
	
	if (divTar.size() == 0){
		System.err.println("No divisions in  target file?\nBye.\n");
	    System.exit(1);
	}
	divsize = "" + ((Vector)divTar.elementAt(0)).size();
	for(i=1; i<divTar.size(); i++)
	    divsize += ", " + ((Vector)divTar.elementAt(i)).size();
	
	MultiAlign.debug("Target: [" + divsize + "] divs, "
			 + paraTar.size() + " paras, "
			 + stcTar.size() + " stcs.");
    }
    
    public final void savePreparedTexts(String outfile)    {
	try {
	    Writer fw = new FileWriter(outfile);
	    fw.write("divSrc="+divSrc.toString()+"\n"); 
	    fw.write("divTar="+divTar.toString()+"\n"); 
	    fw.write("paraSrc="+paraSrc.toString()+"\n");
	    fw.write("paraTar="+paraTar.toString()+"\n"); 
	    fw.write("stcSrc="+stcSrc.toString()+"\n"); 
	    fw.write("stcTar="+stcTar.toString()+"\n"); 
	    fw.close();
	} catch (IOException e){
	    e.printStackTrace();
	}
    }
    
    
    /** 
     * Return the context corresponding to the type of tag.
     * 
     * @param     name The name of the tag.
     */
    public final short getContext(String name)   {
	if (name == null) return EMPTY;
	
	String p = prop.getProperty(name);
	
	if (S_PHRASE.equals(p))
	    return PHRASE;
	else if (S_PARAG.equals(p))
	    return PARAG;
	else if (S_DIV.equals(p))
	    return DIV;
	else if (S_SEQ.equals(p))
	    return SEQ;
	else if (S_TRANSP.equals(p))
	    return TRANSP;
	else if (S_IGNORE.equals(p))
	    return IGNORE;
	else if(S_BODY.equals(p))
	    return BODY;
	else
	    return EMPTY;
    }
    
    // Methode qui initialise les Properties a partir du fichier qu'on donne en parametre   @Pat
    private final void initXMLProperties(String propFile)  {
	prop = new Properties();
	try {
	    FileInputStream f = new FileInputStream(propFile);
	    prop.load(f);
	} catch (Exception e) {
	    System.err.println("initXMLProperties: " + e);
	    e.printStackTrace();
	}
    }
    
    class MAHandler extends DefaultHandler  {
	
	private String id=""; // id of the current element
	private boolean body = false, para = false;
	private Stack <String> idBuf = new Stack <String>();
    
	// B.G. pile pour numéroter récursivement les div... et calculer correctement
	// les longueurs des divisions. On y empile également le compteur de paragraphes
	// et le compteur de phrases.
	private Stack <Object> divStack = new Stack <Object>();

	private int paraLength = 0; // length of current paragraph
	private int stcLength; // buffer saving the current read sentence length 
	private int divLength = 0; // buffer saving current division length
	private int divLengthMother = 0;
	private int divLevel=0; // for the deep level of current division
    
	private short context; // the current context showing the current element
	
	private Vector vstc, vpara, vdiv;
	private Hashtable ids;
	
	public String lang;		// language gleaned from TEIHeader (if any).
	public Vector nospec;	// Tags for which a type wasn't defined.
	
	private String curdiv, curpar;
	private int d = 0, p = 0, s = 0; //counter for generating ids if it does not exist
	private int nignore = 0; // insiding some ignored elements
	
	private String idEnglob;
	private boolean inPartOfDocumentToWorkOn;
        private String nameOfEnglobingElement;
	private int nestingOfEnglobingElement; /* En espérant ne pas dépasser Integer.MAX_VALUE */
	private ArrayList<String> allIds;

	public MAHandler(Vector stcs, Vector paras, Vector divs, Hashtable ids, String idEnglobant, ArrayList<String>Ids){
	    this.vstc = stcs;
	    this.vpara = paras;
	    this.vdiv = divs;
	    this.ids = ids;
	    nospec = new Vector();
	    idEnglob = idEnglobant;
	    allIds = Ids;

	    if (idEnglobant.equals("")){
		inPartOfDocumentToWorkOn = true;
	    }
	    else{
		inPartOfDocumentToWorkOn = false;
	    }
	    // B.G. 12/12/2006
	    curdiv = "";
	}

	public void endDocument() {/* */}

	public void startElement(String uri, String name, String qName, Attributes attrs) {
	    if ("".equals (uri))
		name = qName;
	    context = getContext(name);
	    
	    if (context==BODY) {
		body = true;
	    }
	    
	    id = attrs.getValue("xml:id");
	    //System.out.println(id);
	    if(id == null) 
		id = "";	    
	    else{
		if ((idEnglob != "") && (id.equals(idEnglob))){
		    inPartOfDocumentToWorkOn = true;
		    nameOfEnglobingElement = name;
		    nestingOfEnglobingElement = 1;
		}
	    }
	    if (inPartOfDocumentToWorkOn){
		if(body) 
		    try {
			String fakeid;
			switch (context) {
			case PHRASE:
			    if(nignore > 0) break;
			    fakeid = curpar + "s"+ (++s);
			    ids.put(fakeid, id);
			    idBuf.push(fakeid);
			    id = fakeid;
			    allIds.add(fakeid);
			    stcLength = 0;
			    break;
			case PARAG:
			    if(nignore > 0) break;
			    para = true;
			    fakeid = curdiv + "p"+ (++p);
			    ids.put(fakeid, id);
			    idBuf.push(fakeid);
			    curpar = fakeid; 
			    id = fakeid;
			    allIds.add(fakeid);
			    paraLength = 0;
			    break;
			case DIV:
			    if(nignore > 0) break;
			    divLevel++;                      
			    Vector divTmp = new Vector();
			    
			    /* B.G. 12/12/2006
			       fakeid = "d" + (++d);
			    */
			    fakeid = curdiv + "d" + (++d);
			    divStack.push(curdiv);
			    divStack.push(new Integer(d));
			    d = 0;

			    ids.put(fakeid, id);
			    curdiv = fakeid;
			    id = fakeid;
			    allIds.add(fakeid);
			    divTmp.addElement(fakeid);
			    divTmp.addElement(new Integer(0));

			    /* B.G. 12/12/2006 */
			    divStack.push(new Integer(divLength));
			    divLength = 0;
			    divStack.push(new Integer(p));
			    divStack.push(new Integer(s));
			    p = 0;
			    s = 0;

			    if(divLevel>vdiv.size()){
				// division du niveau courant
				Vector tmp = new Vector();
				tmp.addElement(divTmp);
				vdiv.addElement(tmp);
			    }
			    else {
				// sous-division d'une division.
				Vector tmp = (Vector)vdiv.elementAt(divLevel-1);
				tmp.addElement(divTmp);
			    }
			    break;
			case SEQ:
			case TRANSP:
			case BODY:
			    break;
			case IGNORE:
			    nignore++;
			    break;
			default: // not done yet
			    if(nospec.indexOf(name) == -1)
				nospec.addElement(name);
			    break;
			}
		    } catch(Exception e) {
			System.err.println("startElement: (name=" + name
					   + ", id=" + id + "): " + e);
			e.printStackTrace();
			System.exit(1);
		    }
		else if(name.equals("language")) // I write this just for TEI document
		    lang = id;
	    }
	}
	
	public void endElement(String uri, String name, String qName)	{
	    if ("".equals (uri))
		name = qName;
	    
	    context = getContext(name);
	    if(context == BODY)
		body = false;
	    
	    // Pb : sur la fermeture de l'élément, on ne reçoit pas les attributs.
	    // Pour savoir qu'on a fini notre boulot, il va nous faloir 
	    //      - une pile ! :-(  NON : UN COMPTEUR SUFFIT 
	    //      - le nom de l'élément ouvrant.
	    
	    if (name.equals(nameOfEnglobingElement)){
		nestingOfEnglobingElement--;
		if (nestingOfEnglobingElement == 0){
		    inPartOfDocumentToWorkOn = false;
		}
	    }

	    if (inPartOfDocumentToWorkOn){
		if (body) try {
		    switch (context) {
		    case PHRASE:
			if(nignore > 0) break;
			Vector elt = new Vector();
			elt.addElement(idBuf.pop());
			elt.addElement(new Integer(stcLength));
			vstc.addElement(elt);
			break;
		    case PARAG:
			if(nignore > 0) break;
			s = 0; // reset number of id for sentences
			para = false;
			Vector vp = new Vector();
			vp.addElement(idBuf.pop());
			vp.addElement(new Integer(paraLength));
			vpara.addElement(vp);
			break;
		    case DIV:
			if(nignore > 0) break;
			// XXX: what's going on here?

			s = ((Integer)divStack.pop()).intValue();
			p = ((Integer)divStack.pop()).intValue();

			//p = 0; // reset number of id for paragraphes
			

			/* B.G. 12/12/2006 :
			   je change complètement le calcul des longueurs des divisions...
			*/
			divLengthMother = ((Integer)divStack.pop()).intValue();
			
			/* B.G 12/12/2006 */
			d = ((Integer)divStack.pop()).intValue();
			curdiv = (String)divStack.pop();
			


			Vector tmp=(Vector)((Vector)vdiv.elementAt(divLevel-1)).lastElement();
			if(divLength>0) { 
			    // it's the case we're at the last level of division tree
			    tmp.setElementAt(new Integer(divLength),1);		
			    
			    if(divLevel>1){
				Vector v=(Vector)((Vector)vdiv.elementAt(divLevel-2)).lastElement();
				v.setElementAt(new Integer(divLength),1);
			    }
			}
			else if(divLevel>1){
			    Vector v=(Vector)((Vector)vdiv.elementAt(divLevel-2)).lastElement();
			    v.setElementAt(new Integer(divLength),1);
			}
			//divLength = 0;
			divLength = divLengthMother + divLength;
			divLevel--;
			break;
		    case SEQ:
		    case TRANSP:
			if(nignore > 0) break;
			break;
		    case IGNORE:
			nignore--;
			break;
		    default: // not done yet
		    }
		} catch (Exception e) {
		    System.err.println("endElement: " + e);
		    e.printStackTrace();
		    System.exit(1);
		}
	    }
	}
	
	// characters est appelée quand on tombe sur des caractères...
	// pas sûr que ça soit seulement ceux qu'on veut....
	public void characters(char[] ch, int start, int length) {
	    if(body){
		divLength+=length; 
		stcLength+=length;
		// SEAN: to make sure lengths agree.
		// XXX: does it break something to not check for para context?
		if(para){
		    paraLength = paraLength + length;	    
		}
	    }
	}
    }

    // la liste des id des segments inclus dans l'id id.
    // On renvoie un vecteur d'id "internes" à XAlign alors que id est un
    // id tel qu'apparaissant dans le fichier source.
    // si inSource alors c'est relatif au fichier source, sinon, c'est relatif
    // au fichier cible.
    Vector containsId(String id, boolean inSource){
	String internalId;
	Vector res = new Vector();
	Vector cour;
	String idCour;
	Vector sent;
	Vector divs;
	Vector para;
	Vector level;

	if (inSource){
	    internalId = (String)idSrc.get(id);
	    sent = stcSrc;
	    para = paraSrc;
	    divs = divSrc;
	}
	else{
	    internalId = (String)idTar.get(id);
	    sent = stcTar;
	    para = paraTar;
	    divs = divTar;
	}
	if (isIdOfSentence(internalId)){
	    return res;
	}
	else if (isIdOfParagraph(internalId)){
	    // On cherche toutes les sentences de src qui commencent
	    // par id.
	    for (Enumeration e = sent.elements();
		 e.hasMoreElements();){
		cour = (Vector)e.nextElement();
		idCour = (String)cour.elementAt(0);
		if (idCour.startsWith(internalId)){
		    res.addElement(idCour);
		}
	    }
	    return res;
	}
	else if (isIdOfDiv(internalId)){
	    // On peut aussi avoir des div inclus dans des div...
	    // Il faut donc chercher l'internalId initial, puis
	    // renvoyer les div incluses.
	    for (Enumeration e = divs.elements();
		 e.hasMoreElements();){
		level = (Vector)e.nextElement();
		for (Enumeration e2 = level.elements();
		     e2.hasMoreElements();){
		    cour = (Vector)e2.nextElement();
		    idCour = (String)cour.elementAt(0);
		    if (idCour.startsWith(internalId)){
			res.addElement(idCour);
		    }
		}
	    }
	    for (Enumeration e = para.elements();
		 e.hasMoreElements();){
		cour = (Vector)e.nextElement();
		idCour = (String)cour.elementAt(0);
		if (idCour.startsWith(internalId)){
		    res.addElement(idCour);
		}
	    }	    
	    for (Enumeration e = sent.elements();
		 e.hasMoreElements();){
		cour = (Vector)e.nextElement();
		idCour = (String)cour.elementAt(0);
		if (idCour.startsWith(internalId)){
		    res.addElement(idCour);
		}
	    }
	    return res;
	}
	else{
	    System.err.println("Problem with id : "+id+" in function includedInto\n");
	    System.exit(1);
	}
	return res;
    }

    // Tests wether id2 corresponds to something
    // which is into id1. For instance : id2 = d1p1s1 and
    // id1 is d1p1. 
    // It is not as simple as testing wether id2 startsWith id1 because
    //   d1d4p106 startsWith d1d4p1 but d1d4p106 is not into d1d4p1
    private boolean containsAsInternalId(String id1, String id2){
	// id2 finishes with a number, we remove the number
	// then we remove all what is not a number.
	if (id2.equals("")){
	    return false;
	}
	int i = id2.length()-1;
	while ((i >= 0) && Character.isDigit(id2.charAt(i))){
	    i--;
	}
	while ((i >= 0) && !Character.isDigit(id2.charAt(i))){
	    i--;
	}
	String subId2 = id2.substring(0, i+1);
	return id1.equals(subId2) || containsAsInternalId(id1, subId2);
    }


    // La fonction symétrique qui donne les niveaux de structure qui contiennent
    //        un id donné.
    //  id est un xml:id tel qu'appraissant dans le fichier source. On renvoie un vecteur d'Id
    //  internes à Xalign (ceux qui s'appelent des "fakeid" dans LoadAndPrepareTexts).
    // on les veux de haut en bas dans le résultat...

    public Vector includedInto(String id, boolean inSource){
	String internalId;
	Vector res = new Vector();
	Vector cour;
	String idCour;
	Vector divs;
	Vector para;
	Vector level;

	//System.out.println("includedInto("+id+", "+inSource+")\n");

	if (inSource){
	    internalId = reverseSearch(idSrc, id);
	    para = paraSrc;
	    divs = divSrc;
	}
	else{
	    internalId = reverseSearch(idTar, id);
	    para = paraTar;
	    divs = divTar;
	}
	//System.out.println("internalId = "+internalId);
	//System.out.println("idSrc = "+idSrc);

	// normalement, c'est aussi simple que de parcourir les vecteurs
	// et de renvoyer tous les ids tels que : internalId = id.w
	// ou, en termes java, les id tels que internalId.startsWith(id).

	// No ! That's false !!! for instance d1d4p1 is not
	// includedInto d1d4p10.

	for (Enumeration e = divs.elements();
	     e.hasMoreElements();){
	    level = (Vector)e.nextElement();
	    for (Enumeration e2 = level.elements();
		 e2.hasMoreElements();){
		cour = (Vector)e2.nextElement();
		idCour = (String)cour.elementAt(0);
		//if (internalId.startsWith(idCour)){
		if (containsAsInternalId(idCour, internalId)){
		    res.addElement(idCour);
		}
	    }
	}
	for (Enumeration e = para.elements();
	     e.hasMoreElements();){
	    cour = (Vector)e.nextElement();
	    idCour = (String)cour.elementAt(0);
	    //if (internalId.startsWith(idCour)){
	    if (containsAsInternalId(idCour, internalId)){
		res.addElement(idCour);
	    }
	}
	return res;
    }



    public boolean isIdOfDiv(String id){
	return (containsChar(id, 'd') && (!containsChar(id, 'p')) && (!containsChar(id, 's')));
    }
    private boolean containsChar(String id, char c){
	char[] chars = id.toCharArray();
	for (int i = 0; i < chars.length; i++){
	    if (chars[i] == c){
		return true;
	    }
	}
	return false;
    }

    public boolean isIdOfParagraph(String id){
	return (containsChar(id, 'p') && (!containsChar(id, 's')));
    }

    public boolean isIdOfSentence(String id){
	return containsChar(id, 's');
    }

    private String reverseSearch(Hashtable ta, String val){
	String clef;

	for (Enumeration k = ta.keys();
	     k.hasMoreElements();){
	    clef = (String)k.nextElement();
	    if (ta.get(clef).equals(val)){
		return clef;
	    }
	}
	return null;
	
    }
    public String extToIntIdSource(String extId){
	return reverseSearch(idSrc, extId);
    }
    public String extToIntIdTarget(String extId){
	return reverseSearch(idTar, extId);
    }
    public int ordreDansTexte(XmlId i1, XmlId i2){
	ArrayList<String> lesIds;

	if (i1.getUri().equals(uriSource)){
	    lesIds = fakeIdsSource;
	}
	else{
	    lesIds = fakeIdsTarget;
	}
	return lesIds.indexOf(i1.getLocalName()) -
		lesIds.indexOf(i2.getLocalName());
    }
    public int ordreDansTexte(String i1, String i2, boolean inSource){
	ArrayList<String> lesIds;
	if (inSource){
	    lesIds = fakeIdsSource;
	}
	else{
	    lesIds = fakeIdsTarget;
	}
	return lesIds.indexOf(i1) - lesIds.indexOf(i2);
    }

    public void setProperties(Properties p){
	prop = p;
    }
}

// EOF LoadAndPrepareTexts
