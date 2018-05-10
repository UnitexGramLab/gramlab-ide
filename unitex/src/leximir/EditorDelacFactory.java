package leximir;

public class EditorDelacFactory {
	private EditorDelac dialog;

	public EditorDelac newEditorDelacDialog(boolean alldelas) {
		dialog = new EditorDelac(alldelas);
		return dialog;
	}
}
