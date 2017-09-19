package leximir;


public class StrategyFactory {
	private Strategy dialog;

	public Strategy newStrategyDialog() {
		dialog = new Strategy();
		return dialog;
	}
}
