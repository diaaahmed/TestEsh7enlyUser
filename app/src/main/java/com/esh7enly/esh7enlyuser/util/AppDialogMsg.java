package com.esh7enly.esh7enlyuser.util;

import android.app.Activity;
import android.app.Dialog;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.esh7enly.esh7enlyuser.R;


public class AppDialogMsg {

    public TextView msgTextView, titleTextView, tvProgressPercent,tv_title_qty;
    private ProgressBar progressBar;
    public Button okButton, cancelButton;
    private Dialog dialog;
    private View view;
    private boolean cancelable;
    private EditText edtAmount;
    private String amount;

    public AppDialogMsg(Activity activity, Boolean cancelable) {

        this.dialog = new Dialog(activity);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.cancelable = cancelable;
    }

    private WindowManager.LayoutParams getLayoutParams(@NonNull Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }

    public void show() {
        if (dialog != null) {
            this.dialog.show();
        }
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void cancel() {
        if (dialog != null) {
            this.dialog.dismiss();
        }
    }

    public Dialog showConfirmDialog(String message, String okTitle, String cancelTitle) {
        this.dialog.setContentView(R.layout.dialog_message);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(dialog.getContext().getString(R.string.confirm));
        msgTextView.setText(message);
        okButton.setText(okTitle);
        cancelButton.setText(cancelTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> AppDialogMsg.this.cancel());
        }
        return dialog;
    }

    public void showInfoDialog(String message, String okTitle) {
        this.dialog.setContentView(R.layout.dialog_alert);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        view.setBackgroundColor(dialog.getContext().getResources().getColor(R.color.grey_500));

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(dialog.getContext().getString(R.string.info));
        msgTextView.setText(message);
        okButton.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));


            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> AppDialogMsg.this.cancel());
        }

    }

    public void showSuccessDialog(String message, String okTitle, final CallBack callBack) {
        this.dialog.setContentView(R.layout.dialog_alert);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);

        titleTextView.setTextColor(dialog.getContext().getResources().getColor(R.color.green_800));

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(dialog.getContext().getString(R.string.success));
        msgTextView.setText(message);
        okButton.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onClick());
        }
    }

    // Success Dialog with On click event
    public Dialog showSuccessDialogWithAction(String title, String message,
                                              String okTitle, String cancelTitle,
                                              final CallBack callBack) {
        this.dialog.setContentView(R.layout.dialog_message);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);


        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);

        titleTextView.setText(title);
        msgTextView.setText(message);
        okButton.setText(okTitle);
        cancelButton.setText(cancelTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onClick());
            cancelButton.setOnClickListener(view -> cancel());
        }
        return dialog;
    }

    // Success Dialog with On click event
    public Dialog showSuccessDialogWithAction(String title, String message,
                                              String okTitle, String cancelTitle,
                                              final CallBack2 callBack) {

        this.dialog.setContentView(R.layout.dialog_message);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);

        titleTextView.setText(title);
        msgTextView.setText(message);
        okButton.setText(okTitle);
        cancelButton.setText(cancelTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onOkClick());
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onCancelClick();
                    cancel();
                }
            });
        }
        return dialog;
    }

    // Success Dialog with On click event
    public Dialog showSuccessDialogWithActionAndBulkCards(String title, String message,
                                                          String okTitle, String cancelTitle,
                                                          String quantity,
                                                          final CallBackAmount callBack) {
        this.dialog.setContentView(R.layout.dialog_bulk);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        tv_title_qty = dialog.findViewById(R.id.tv_title_qty);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);
        edtAmount = dialog.findViewById(R.id.edt_amount);
        edtAmount.setEnabled(false);
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        titleTextView.setText(title);
        tv_title_qty.setText(quantity);
        msgTextView.setText(message);
        edtAmount.setText("1");
        okButton.setText(okTitle);
        cancelButton.setText(cancelTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view ->
                    {
                        if (!edtAmount.getText().toString().isEmpty()) {
                            callBack.onClick(edtAmount.getText().toString());
                        } else {
                            edtAmount.setText("1");
                            callBack.onClick(edtAmount.getText().toString());
                        }

                    }
            );
            cancelButton.setOnClickListener(view -> cancel());
        }
        return dialog;
    }

    public void showDialogEditAmount(String title, String message, String okTitle, String cancelTitle,
                                     String amount, final CallBackAmount callBack) {

        this.dialog.setContentView(R.layout.dialog_amount);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        edtAmount = dialog.findViewById(R.id.edt_amount);
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);

        titleTextView.setText(title);
        msgTextView.setText(message);
        edtAmount.setText(amount);
        okButton.setText(okTitle);
        cancelButton.setText(cancelTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onClick(edtAmount.getText().toString()));
            cancelButton.setOnClickListener(view -> cancel());
        }

    }

    public void showDialogMultiRecharge(String title, String message, String okTitle, String cancelTitle,
                                        String amount, final CallBackAmount callBack) {

        this.dialog.setContentView(R.layout.dialog_amount);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        edtAmount = dialog.findViewById(R.id.edt_amount);
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);

        titleTextView.setText(title);
        msgTextView.setText(message);
        edtAmount.setText(amount);
        okButton.setText(okTitle);
        cancelButton.setText(cancelTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onClick(edtAmount.getText().toString()));
            cancelButton.setOnClickListener(view -> cancel());
        }

    }

    public Dialog showErrorDialog(String message, String okTitle) {

        this.dialog.setContentView(R.layout.dialog_alert);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);

        titleTextView.setTextColor(dialog.getContext().getResources().getColor(R.color.red_900));

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(dialog.getContext().getString(R.string.error));
        msgTextView.setText(message);
        okButton.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            this.dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> AppDialogMsg.this.cancel());
        }

        return dialog;
    }

    public Dialog showErrorDialogWithAction(String message, String okTitle, final CallBack callBack) {

        this.dialog.setContentView(R.layout.dialog_alert);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);

        titleTextView.setTextColor(dialog.getContext().getResources().getColor(R.color.red_900));

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(dialog.getContext().getString(R.string.error));
        msgTextView.setText(message);
        msgTextView.setMovementMethod(new ScrollingMovementMethod());
        okButton.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            this.dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onClick());
        }
        return dialog;
    }

    public Dialog showWarningDialogWithAction(String message, String okTitle, final CallBack callBack) {

        this.dialog.setContentView(R.layout.dialog_alert);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        titleTextView.setTextColor(dialog.getContext().getResources().getColor(R.color.red_900));

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(dialog.getContext().getString(R.string.warning));
        msgTextView.setText(message);
        msgTextView.setMovementMethod(new ScrollingMovementMethod());
        okButton.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            this.dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onClick());
        }
        return dialog;
    }


    public void showWarningDialog(String message, String okTitle) {
        this.dialog.setContentView(R.layout.dialog_alert);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        titleTextView.setTextColor(dialog.getContext().getResources().getColor(R.color.lime_700));

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(dialog.getContext().getString(R.string.warning));
        msgTextView.setText(message);
        okButton.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> AppDialogMsg.this.cancel());
        }
    }

    // Dialog with On click event
    public Dialog showDialogDownloadXlsWithAction(String title, String message, String okTitle, String cancelTitle,
                                                  final DownloadFileCallBack callBack) {

        this.dialog.setContentView(R.layout.dialog_download);
        view = dialog.findViewById(R.id.dialogTitleView);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);

        tvProgressPercent = dialog.findViewById(R.id.tv_progress_percent);
        progressBar = dialog.findViewById(R.id.progress_bar);
        // hide percent until download start
        tvProgressPercent.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okButton = dialog.findViewById(R.id.dialogOkButton);
        cancelButton = dialog.findViewById(R.id.dialogCancelButton);

        titleTextView.setText(title);
        msgTextView.setText(message);
        okButton.setText(okTitle);
        cancelButton.setText(cancelTitle);

        msgTextView.setTextColor(dialog.getContext().getResources().getColor(R.color.blue_800));
        msgTextView.setOnClickListener(view -> {
            callBack.onDownload(tvProgressPercent, progressBar);
            // show percent when download start
            tvProgressPercent.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okButton.setOnClickListener(view -> callBack.onClick());
            cancelButton.setOnClickListener(view -> cancel());
        }
        return dialog;
    }


    public interface CallBack {
        void onClick();
    }

    public interface CallBack2 {

        void onOkClick();

        void onCancelClick();

    }

    public interface DownloadFileCallBack {

        void onClick();

        void onDownload(TextView tvProgressPercent, ProgressBar progressBar);

    }

    public interface CallBackAmount {
        void onClick(String str);

    }
}
