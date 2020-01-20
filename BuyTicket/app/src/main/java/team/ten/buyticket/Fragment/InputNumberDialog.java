package team.ten.buyticket.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import team.ten.buyticket.R;

public class InputNumberDialog extends AlertDialog.Builder{


    public interface InputNumberDialogListener{
        public abstract void onOK(String number);
        public abstract void onCancel(String number);
    }

    private EditText mNumberEdit;

    public InputNumberDialog(final Activity activity, final InputNumberDialogListener listener, int maxNum) {
        super( new ContextThemeWrapper(activity, R.style.AppTheme) );

        @SuppressLint("InflateParams") // It's OK to use NULL in an AlertDialog it seems...
                View dialogLayout = LayoutInflater.from(activity).inflate(R.layout.layout_number_picker, null);

        setView(dialogLayout);

        TextView rangeLabel = (TextView)dialogLayout.findViewById(R.id.numberLabel);
        rangeLabel.setText("請輸入 1～"+ String.valueOf(maxNum) +" 之間的數字");

        mNumberEdit = dialogLayout.findViewById(R.id.numberEdit);

        setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if( listener != null )
                    listener.onOK(String.valueOf(mNumberEdit.getText()));

            }
        });

        setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if( listener != null )
                    listener.onCancel(String.valueOf(mNumberEdit.getText()));
            }
        });


        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

    }

    public InputNumberDialog setNumber(String number){
        mNumberEdit.setText( number );
        mNumberEdit.setSelection(mNumberEdit.getText().length());
        return this;
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();
        Window window = dialog.getWindow();
        if( window != null )
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }

}
