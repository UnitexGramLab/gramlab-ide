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
 * @(#)       Div.java
 * 
 * 
 * Alignment at "div" level. 
 * 
 * @version   
 * @author    NGUYEN Thi Minh Huyen
 * Copyright  1999
 *
 */

package fr.loria.xsilfide.multialign;

import java.util.*;

public class Div
{
  
  private Vector Linking = new Vector(); //links of sentences within a text
  private Vector Links = new Vector(); //links of the two texts
  int spMax, tpMax;
  int cpSrc = 0, cpTar = 0;
  int level = 0; // counter for saving the current level
  
  private Dist srcLengths,tarLengths; //lengths of divisions to align    
  
  public Div(Vector divSrc, Vector divTar, Vector paraSrc, Vector paraTar, Vector stceSrc, Vector stceTar, Cognates cogn, LoadAndPrepareTexts lpt)
  {


      // B.G : normalement, divSrc, divTar, paraSrc, paraTar, stceSrc
      // stceTar sont des vecteurs contenant à la fois les id et les longueurs.

      // nb1, nb2 = nombre de niveaux de divisions imbriquees
      // dans source et target.

    int nb1 = divSrc.size(), nb2 = divTar.size();
    
    /* Algorithm:
       For each level in the tree of divisions, align the divisions of 
       the source and target texts. Then get aligned pairs and continue to 
       align the divisions at the next level corresponding to these pairs.
       In case a div has not children in the next level, we consider it as
       child of itself.

       Ajout B.G le 13/12/2006 : à chaque niveau on veut prendre en compte
       des cognates. Cela signfie qu'il faut traduire les cognates
       concernant ce niveau de structures en contraintes de chemin.

     */

    Vector Src_Tar = new Vector(); //vector of pairs of src and tar div to align
  
    Vector src = (Vector)divSrc.elementAt(0);
    Vector tar = (Vector)divTar.elementAt(0);

    Vector tmp = new Vector();
    

    tmp.addElement(src);
    tmp.addElement(tar);
    Src_Tar.addElement(tmp);


    while ((level < nb1-1)||(level<nb2-1)) { // repeat for each internal level

      Vector nextSrc = new Vector();
      Vector nextTar = new Vector();
      //MultiAlign.debug.println("au cours:"+Src_Tar.toString());
      int size = Src_Tar.size();
      
      // get divisions at the next level in the src and the tar
      if(level<nb1-1)
	nextSrc = (Vector)divSrc.elementAt(level+1);
      
      if(level<nb2-1)
	nextTar = (Vector)divTar.elementAt(level+1);

      int cNextSrc = 0, cNextTar = 0; // repere in the next level


      System.out.println("\r                                                               ");
      System.out.print("\rDiv level " + (level+1) + ": ");
      int mod = (size < MultiAlign.NDOTS)?1:(size/MultiAlign.NDOTS);
      for(int count=0;count<size;count++) {
	if(count % mod == 0) System.out.print(".");
	Vector buf = (Vector)Src_Tar.elementAt(0);

	Src_Tar.removeElementAt(0);
	src = (Vector)buf.elementAt(0);
	tar = (Vector)buf.elementAt(1);



	Path cur_path = alignDiv(src,tar, cogn);
	//MultiAlign.debug.println("Niveau "+level+":"+cur_path.toString());
	
	int cSrc = 0, cTar = 0; // counter for passing the src and tar following the path
	
	for(int i=0;i<cur_path.getNumberOfPoint();i++) {
	  Point p = cur_path.getPointAt(i);
	  Vector newSrc = new Vector(), newTar = new Vector();

	  // get divisions in the next level to align

	  // In the source:
	  for(int j=0;j<p.x;j++) {
	    // get the div contained in this point of path
	    Vector current_div = (Vector)src.elementAt(cSrc+j);
	    String idOfDiv = current_div.elementAt(0).toString();
	    int index = (new Integer(idOfDiv.substring(1,idOfDiv.length()))).intValue();
	    
	    while(cNextSrc<nextSrc.size()){
	      // search children of current_div in the next level
	      Vector sdiv = (Vector)nextSrc.elementAt(cNextSrc);
	      String id = sdiv.elementAt(0).toString();
	      // B.G. C'est évidemment faux !
	      if(id.compareTo("d"+(index+1))==0) {
		newSrc.addElement(sdiv);
		index++;
		cNextSrc++;
	      }
	      else
		break;
	    }
	    if(idOfDiv.compareTo("d"+index)==0) // then this div has not child
	      newSrc.addElement(current_div);
	  }
	  
	  //In the target:
	  for(int j=0;j<p.y;j++) {
	    // get the div contained in this point of path
	    Vector current_div = (Vector)tar.elementAt(cTar+j);
	    String idOfDiv = current_div.elementAt(0).toString();
	    int index = (new Integer(idOfDiv.substring(1,idOfDiv.length()))).intValue();
	    while(cNextTar<nextTar.size()){
	      // search children of current_div in the next level
	      Vector sdiv = (Vector)nextTar.elementAt(cNextTar);
	      String id = sdiv.elementAt(0).toString();
	      // B.G. C'est évidemment faux ici aussi !
	      if(id.compareTo("d"+(index+1))==0) {
		newTar.addElement(sdiv);
		index++;
		cNextTar++;
	      }
	      else 
		break;
	    }
	    if(idOfDiv.compareTo("d"+index)==0) // then this div has not child
	      newTar.addElement(current_div);
	  }
	  
	  Vector src_tar = new Vector();
	  src_tar.addElement(newSrc);
	  src_tar.addElement(newTar);
	  Src_Tar.addElement(src_tar);
	  cSrc += p.x;
	  cTar += p.y;
	}
      }
      System.out.println();

      level++;
    } //end while level

    //MultiAlign.debug.println("Dernier niveau:"+Src_Tar.toString()); 
    //System.exit(1);

    spMax = paraSrc.size();
    tpMax = paraTar.size();

    // now we align the last level of division and step by paragraph level

    int size = Src_Tar.size();

    for(int count=0;count<Src_Tar.size();count++) {
      System.out.print(
	"\r                                                               ");
      System.out.print("\rAligning div's (" + (count+1) + "/" + size + "): ");
      // align each pair of division's vector
      Vector src_tar = (Vector)Src_Tar.elementAt(count);

      // get source divisions and target divisions
      src = (Vector)src_tar.elementAt(0);
      tar = (Vector)src_tar.elementAt(1);
      //MultiAlign.debug.println(src_tar.toString());
      //MultiAlign.debug.println(src.toString()+"\n"+tar.toString());
      // align them
      Path cur_path = alignDiv(src,tar, cogn);
      //MultiAlign.debug.println("group "+count+1+":"+cur_path.toString());      
      //System.exit(1);
      int cSrc = 0, cTar = 0;

      for(int i=0;i<cur_path.getNumberOfPoint();i++) {
	Point p = cur_path.getPointAt(i);
	//MultiAlign.debug.println(p.toString());
	// get the coresponding group of divisions

	// In the source:
	Vector idSrcDiv = new Vector();
	for (int j=0;j<p.x;j++){
	  Vector buf = (Vector)src.elementAt(cSrc);
	  String idSrc = buf.elementAt(0).toString();
	  idSrcDiv.addElement(idSrc);
	  cSrc++;
	}
	// In the target:
	Vector idTarDiv = new Vector();
	for (int j=0;j<p.y;j++){
	  Vector buf = (Vector)tar.elementAt(cTar);
	  String idTar = buf.elementAt(0).toString();
	  idTarDiv.addElement(idTar);
	  cTar++;
	}

	//MultiAlign.debug.println("here");
	// align these paragraphs
	//MultiAlign.debug.println(idSrcDiv.toString()+"\n"+idTarDiv.toString());
	//System.exit(1);
	alignParas(idSrcDiv,idTarDiv,paraSrc,paraTar,stceSrc,stceTar, cogn, lpt);
	//MultiAlign.debug.println(Linking.toString()+"\n");
	//MultiAlign.debug.println(Links.toString());
	//	System.exit(1);
      }
      //System.exit(1);
    }
  }
  

    //getLenghts fabrique (effet de bord) srcLength et tarLength
    // qui sont des Dist c.a.d. des vecteurs d'entiers.
    // elle reçoit en entrée des trucs qui contiennent à la fois les id et 
    // les longueurs.
    // Dans srcLength et tarLength on n'a plus que les longueurs.

  void getLengths(Vector Src,Vector Tar) {

    int ns = Src.size(), nt = Tar.size();
    srcLengths = new Dist(ns);
    tarLengths = new Dist(nt);


    for (int i = 0; i < ns; i++){
      int t =((Integer)((Vector)Src.elementAt(i)).elementAt(1)).intValue(); 
      if(level==0) t/=1000;
      else t/=100;
      srcLengths.setDistAt(i, t);
    }

    for (int i = 0; i < nt; i++){
      int t = ((Integer)((Vector)Tar.elementAt(i)).elementAt(1)).intValue();
      if(level==0) t/=1000;
      else t/=100;
      tarLengths.setDistAt(i, t);
    }

    return;
  }

    // B.G : normalement, Src et Tar contiennent des id et des longueurs.
  public Path alignDiv(Vector Src,Vector Tar, Cognates cogn) {

    Path path;
    ContraintesChemin cc = cogn.cognates2Chemins(Src, Tar);

    MultiAlign.debug("div source:"+Src.toString());
    MultiAlign.debug("div target:"+Tar.toString());

    
    getLengths(Src,Tar);
    path = Align.getPath(srcLengths, tarLengths, true, cc);

    MultiAlign.debug("path:"+path.toString());
    //System.exit(1);
    return path;

  }

  public void alignParas(Vector idSrcDiv, Vector idTarDiv, Vector parSrc, Vector parTar, Vector stcSrc, Vector stcTar, Cognates cogn, LoadAndPrepareTexts lpt){

    //MultiAlign.debug.println(idSrcDiv.toString()+"\n"+idTarDiv.toString());

    //MultiAlign.debug.println("here");
    int ssMax = stcSrc.size(), tsMax = stcTar.size();

    Vector pSource = new Vector(), pTarget = new Vector();
    Vector paraSrc = new Vector(), paraTar = new Vector();

    for (int i=0;i<idSrcDiv.size();i++) {
      String idSrc = idSrcDiv.elementAt(i).toString();

      while(cpSrc<spMax) {
	if(!((Vector)parSrc.elementAt(cpSrc)).elementAt(0).toString().startsWith(idSrc)){
	  paraSrc.addElement((Vector)parSrc.elementAt(cpSrc));
	  cpSrc++;
	}
	else 
	  break;
      }
      pSource.addElement(paraSrc.clone());
      paraSrc.removeAllElements();

      while(cpSrc<spMax) {
        if(((Vector)parSrc.elementAt(cpSrc)).elementAt(0).toString().startsWith(idSrc)){
	  paraSrc.addElement((Vector)parSrc.elementAt(cpSrc));
	  cpSrc++;
	}
	else
	  break;
      }

      pSource.addElement(paraSrc.clone());
      paraSrc.removeAllElements();
      
    }

    for (int i=0;i<idTarDiv.size();i++) {
      String idTar = idTarDiv.elementAt(i).toString();

      while(cpTar<tpMax) {
	if(!((Vector)parTar.elementAt(cpTar)).elementAt(0).toString().startsWith(idTar)){
	  paraTar.addElement((Vector)parTar.elementAt(cpTar));
	  cpTar++;
	}
	else 
	  break;
      }

      pTarget.addElement(paraTar.clone());
      paraTar.removeAllElements();

      while(cpTar<tpMax) {
	if(((Vector)parTar.elementAt(cpTar)).elementAt(0).toString().startsWith(idTar)){
	  paraTar.addElement((Vector)parTar.elementAt(cpTar));
	  cpTar++;
	}
	else
	  break;
      }
      pTarget.addElement(paraTar.clone());
      paraTar.removeAllElements();
    }

    //MultiAlign.debug.println("Niveau para:"+paraSrc.toString());
    //MultiAlign.debug.println(paraTar.toString());
    if(pSource.size()==pTarget.size()){ 
      for(int i=0;i<pSource.size();i++) {
	paraSrc = (Vector)pSource.elementAt(i);
	paraTar = (Vector)pTarget.elementAt(i);
	Paragraphes prgph = new Paragraphes(paraSrc,paraTar,stcSrc,stcTar,ssMax,tsMax, cogn, lpt); 
	//MultiAlign.debug.println("Links: " + prgph.Links.toString());
	
	// get links of prgph
	Links = Paragraphes.Links;
	
	// get linkings of prgph
	Linking = Paragraphes.Linking;

      }
    }
    else {
      for(int i=0;i<pSource.size();i++) {
	Vector tmp = (Vector)pSource.elementAt(i);
	for(int j=0;j<tmp.size();j++){
	  paraSrc.addElement(tmp.elementAt(j));
	}
      }
      for(int i=0;i<pTarget.size();i++) {
	Vector tmp = (Vector)pTarget.elementAt(i);
	for(int j=0;j<tmp.size();j++){
	  paraTar.addElement(tmp.elementAt(j));
	}
      }
      
      Paragraphes prgph = new Paragraphes(paraSrc,paraTar,stcSrc,stcTar,ssMax,tsMax, cogn, lpt); 
      //MultiAlign.debug.println("Links: " + prgph.Links.toString());
      
      // get links of prgph
      Links = Paragraphes.Links;
	
      // get linkings of prgph
      Linking = Paragraphes.Linking;
    }

  }

    public Vector getLinking(){
	return Linking;
    }
    public Vector getLinks(){
	return Links;
    }
    
}
