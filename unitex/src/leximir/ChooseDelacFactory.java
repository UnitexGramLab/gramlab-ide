package leximir;

public class ChooseDelacFactory {
	private ChooseDelac dialog;

	public ChooseDelac newChooseDelacDialog() {
		dialog = new ChooseDelac();
		return dialog;
	}
}
