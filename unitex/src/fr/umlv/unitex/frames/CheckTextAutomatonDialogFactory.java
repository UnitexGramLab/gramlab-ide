package fr.umlv.unitex.frames;

<<<<<<< HEAD
import java.util.ArrayList;

=======
>>>>>>> Add a new dialog to the check button
/**
 * @author Maxime Petit
 */
public class CheckTextAutomatonDialogFactory {

  private CheckTextAutomatonDialog dialog;

<<<<<<< HEAD
  CheckTextAutomatonDialog newCheckTextAutomatonDialog(ArrayList<String> checkList) {
    dialog = CheckTextAutomatonDialog.createCheckTextAutomatonDialog(checkList);
=======
  CheckTextAutomatonDialog newCheckTextAutomatonDialog() {
    if (dialog == null) {
      dialog = CheckTextAutomatonDialog.createCheckTextAutomatonDialog();
    } else {
      dialog.updateDialog();
    }
>>>>>>> Add a new dialog to the check button
    return dialog;
  }
}
