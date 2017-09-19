package fr.umlv.unitex.frames;

import leximir.Shell;

public class ConjugaisonFactory {
	private ConjugaisonFrame dialog;
	public ConjugaisonFrame newConjugaisonDialog() {
		dialog = new ConjugaisonFrame();
		return dialog;
	}

}
