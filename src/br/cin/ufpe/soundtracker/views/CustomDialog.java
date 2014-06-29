package br.cin.ufpe.soundtracker.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.WindowManager.LayoutParams;

public class CustomDialog {

    private String key;
    private ProgressDialog dialog;

    public CustomDialog(Activity activity) {
        this.dialog = new ProgressDialog(activity);
        this.dialog.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        this.dialog.setTitle("");
        this.dialog.setMessage("Loading ...");
        this.dialog.setIndeterminate(true);

        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
            }
        });
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
            }
        });
        //
    }

    public void show() {
        this.show("");
    }

    public void show(String key) {
        this.key = key;
        if (!this.dialog.isShowing()) {
            this.dialog.show();
            this.dialog.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void dismiss(String key) {
        if (key.equals(this.key)) {
            this.dialog.dismiss();
        }
    }

}
