package com.example.user.soil_supervise_kotlin.OtherClass;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.soil_supervise_kotlin.R;

public class CustomerProgressDialog extends AlertDialog
{
    private String _title;
    private DialogBackPressedListener _Listener;

    public CustomerProgressDialog(Context context, String title)
    {
        super(context);
        _title = title;
    }

    public interface DialogBackPressedListener
    {
        void onBackPressedInDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.dialog_progress);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView tx_connecting = findViewById(R.id.tx_connecting);

        progressBar.setVisibility(View.VISIBLE);
        tx_connecting.setText(_title);
    }

    public void setOnBackPressedListener(DialogBackPressedListener listener)
    {
        _Listener = listener;
    }

    @Override
    public void onBackPressed()
    {
        _Listener.onBackPressedInDialog();
    }
}
