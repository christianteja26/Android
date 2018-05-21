package id.ac.binus.boarderlognightmonitoring.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import id.ac.binus.boarderlognightmonitoring.R;
import id.ac.binus.boarderlognightmonitoring.model.Reason;

/**
 * Created by CT on 07-Apr-17.
 */

public class ReasonAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    private ArrayList<Reason> reasonList;
    private Context mContext;
    private int selectedIndex = 0;
    private EditText txtOther;
    private OnSelectedRadio mDelegate;
    public String otherReason;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && selectedIndex < reasonList.size()) {
            mDelegate.onSelected(reasonList.get(selectedIndex));
        }else if(isChecked && selectedIndex == reasonList.size()){
            mDelegate.onFinishEditing();
        }
    }

    public interface OnSelectedRadio{
        void onSelected(Reason reason);
        void onFinishEditing();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getOtherReason() {
        return txtOther.getText().toString();
    }

    public ReasonAdapter(ArrayList<Reason> reasonList, Context context,OnSelectedRadio delegate) {
        this.reasonList = reasonList;
        this.mContext = context;
        this.mDelegate = delegate;
    }
    @Override
    public int getCount() {
        return reasonList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return reasonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(position == reasonList.size()) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.other_list_item, parent, false);
            final TextView rbReasonName = (TextView) convertView.findViewById(R.id.rbReasonName);
            this.txtOther= (EditText) convertView.findViewById(R.id.otherReason);
            rbReasonName.setText(mContext.getString(R.string.other));
            txtOther.setEnabled(false);
            txtOther.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    otherReason = s.toString();
                    selectedIndex = reasonList.size();
                }
            });
        }
        else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            TextView rbReasonName = (TextView) convertView.findViewById(R.id.rbReasonName);
            Reason reason = reasonList.get(position);
            rbReasonName.setText(reason.getReasonName());
        }
        final RadioButton rbReason = (RadioButton) convertView.findViewById(R.id.rbReasonName);
        rbReason.setOnCheckedChangeListener(this);

//      function RICKY
        /*if(position == reasonList.size()){
            txtOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rbReason.setChecked(true);
                }
            });
        }*/

        if (selectedIndex == position && position == reasonList.size())
        {
            txtOther.setEnabled(true);
        }
        if(selectedIndex == position)
            rbReason.setChecked(true);
        else {
            rbReason.setChecked(false);
            rbReason.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    selectedIndex = position;

                ReasonAdapter.this.notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }

}
