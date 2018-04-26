package leximir;


public class EditorDelasFactory {
	private EditorDelas dialog;

	public EditorDelas newEditorDelasDialog(boolean alldelas) {
		dialog = new EditorDelas(alldelas);
		return dialog;
	}
}
