package foam.nebogeo.doris_evolved;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.view.View;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

class DorisLobsterAdapter extends ArrayAdapter<HashMap> {

    protected final LayoutInflater inflater;

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public DorisLobsterAdapter(Context context, int textViewResourceId,
                              List<HashMap> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put((String)objects.get(i).get("id"), i);
        }
        inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = inflater.inflate(R.layout.list_map_item, viewGroup, false);
        row.setBackgroundResource(R.drawable.list_rounded_corner);

        Widgets widget = (Widgets) row.getTag();

        if (widget == null) {
            widget = new Widgets(row);
            row.setTag(widget);
        }

        String date = DorisFileUtils
            .formatDate("yyyy_MM_dd_hh_mm_ss",
                        (String) getItem(position).get("date"),
                        "dd/MM/yyyy hh:mm a", null,
                        Locale.US);

        // initialize view with content
        widget.id.setText((String)getItem(position).get("title"));
        widget.date.setText(date);

        return row;
    }

    public class Widgets {
        TextView id;
        TextView date;
        public Widgets(View convertView) {
            id = (TextView) convertView.findViewById(R.id.map_list_id);
            date = (TextView) convertView.findViewById(R.id.map_list_date);
        }
    }


    @Override
        public long getItemId(int position) {
        String item = (String) getItem(position).get("id");
        return mIdMap.get(item);
    }

    @Override
        public boolean hasStableIds() {
        return true;
    }

}
