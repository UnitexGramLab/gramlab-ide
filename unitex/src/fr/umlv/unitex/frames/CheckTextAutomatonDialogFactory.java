package fr.umlv.unitex.frames;

/**
 * @author Maxime Petit
 */
public class CheckTextAutomatonDialogFactory {

  private CheckTextAutomatonDialog dialog;

  CheckTextAutomatonDialog newCheckTextAutomatonDialog() {
    if (dialog == null) {
      dialog = CheckTextAutomatonDialog.createCheckTextAutomatonDialog();
    } else {
      dialog.updateDialog();
    }
    return dialog;
  }
}
