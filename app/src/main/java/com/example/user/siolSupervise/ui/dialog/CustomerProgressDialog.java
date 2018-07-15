package com.example.user.siolSupervise.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.siolSupervise.R;

public class CustomerProgressDialog extends AlertDialog {
    private String _title;
    private DialogBackPressedListener _Listener;

    public interface DialogBackPressedListener {
        void onBackPressedInDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_progress);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView tx_connecting = findViewById(R.id.tx_connecting);

        progressBar.setVisibility(View.VISIBLE);
        tx_connecting.setText(_title);
    }

    @Override
    public void onBackPressed() {
        _Listener.onBackPressedInDialog();
    }

    public CustomerProgressDialog(Context context, String title) {
        super(context);
        _title = title;
    }

    public void setOnBackPressedListener(DialogBackPressedListener listener) {
        _Listener = listener;
    }
}
