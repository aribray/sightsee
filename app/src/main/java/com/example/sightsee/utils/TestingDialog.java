package com.example.sightsee.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sightsee.R;

/**
 * Created by aryo on 13/6/16.
 */
public class TestingDialog extends DialogFragment {

//    private View v;
//    private TextView label;
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
//        this.v = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_test, null);
//        this.label = (TextView) this.v.findViewById(R.id.textview_label);
//        builder.setView(this.v);
//        return builder.create();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        this.v = inflater.inflate(R.layout.dialog_test, null);
//        this.label = (TextView) this.v.findViewById(R.id.textview_label);
//        return v;
//    }
//
//    public void text(String text) {
//        if (this.label != null)
//            this.label.setText(text);
//    }
}