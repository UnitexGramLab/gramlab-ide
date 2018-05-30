package fr.umlv.unitex.frames;

public class ConjugationFactory {
	private ConjugationFrame dialog;
	public ConjugationFrame newConjugaisonDialog() {
		dialog = new ConjugationFrame();
		return dialog;
	}

}
