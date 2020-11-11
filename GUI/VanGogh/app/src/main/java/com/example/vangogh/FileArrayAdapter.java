package com.example.vangogh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

<<<<<<< HEAD
<<<<<<< HEAD
/**
 * Class used for displaying the Files into the FileManager View.
 */
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
=======
>>>>>>> 0ce85476119f84991042c85c05a9056e632a04ec
public class FileArrayAdapter  extends ArrayAdapter<String>
{
    private final Context context;
    private final ArrayList<String> values;

    public FileArrayAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.files_view, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.files_view, parent, false);

        // Displaying a textview
        TextView textView = (TextView) rowView.findViewById(R.id.file_name);
        textView.setText(values.get(position));


        return rowView;
    }
}
