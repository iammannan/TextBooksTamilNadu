package mannan.textbookstamilnadu;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>>data;
    private static LayoutInflater inflater=null;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.listview_single, null);

        TextView BookName = (TextView)vi.findViewById(R.id.list_bookname); // title
        TextView BookSize = (TextView)vi.findViewById(R.id.list_booksize); // artist name

        HashMap<String, String> bookDeta = new HashMap<String, String>();
        bookDeta = data.get(position);
        Log.d(TAG, "getView: "+position);
        // Setting all values in listview

        BookName.setText(bookDeta.get(MainActivity.KEY_BOOKNAME));
        BookSize.setText("Size : "+bookDeta.get(MainActivity.KEY_BOOKSIZE));
        return vi;
    }
}