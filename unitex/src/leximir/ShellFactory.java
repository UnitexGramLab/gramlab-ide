package leximir;


public class ShellFactory {
	private Shell dialog;

	public Shell newShellDialog() {
		dialog = new Shell();
		return dialog;
	}
}
