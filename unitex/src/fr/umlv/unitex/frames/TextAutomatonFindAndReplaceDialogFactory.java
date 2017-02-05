package fr.umlv.unitex.frames;


public class TextAutomatonFindAndReplaceDialogFactory {

    private TextAutomatonFindAndReplaceDialog dialog;

    public TextAutomatonFindAndReplaceDialog newTextAutomatonFindAndReplaceDialog() {
        if (dialog == null) {
            dialog = TextAutomatonFindAndReplaceDialog.createFindAndReplaceDialog();
        } else {
            dialog.updateData();
        }
        return dialog;
    }

    public void update() {
        if (dialog == null) {
            return;
        }
        dialog.clearHighlight();
        dialog.updateData();
    }
}
