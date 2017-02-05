package fr.umlv.unitex.frames;


public class TextAutomatonTagFilterDialogFactory {

    private TextAutomatonTagFilterDialog dialog;

    public TextAutomatonTagFilterDialog newTextAutomatonTagFilterDialog() {
        if (dialog == null) {
            dialog = TextAutomatonTagFilterDialog.createFindAndReplaceDialog();
        }
        return dialog;
    }
}
