package fr.umlv.unitex.frames;

import java.util.ArrayList;

/**
 * @author Maxime Petit
 */
public class CheckTextAutomatonDialogFactory {

  	private CheckTextAutomatonDialog dialog;

  	CheckTextAutomatonDialog newCheckTextAutomatonDialog(ArrayList<String> checkList) {
    	dialog = CheckTextAutomatonDialog.createCheckTextAutomatonDialog(checkList);
    	return dialog;
  	}
}