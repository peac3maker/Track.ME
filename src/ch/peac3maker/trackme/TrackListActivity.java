package ch.peac3maker.trackme;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TrackListActivity extends ListActivity {	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.list_item);    	
    	//---use the LocationManager class to obtain GPS locations---
    	GPointDataSource source = new GPointDataSource(this);
    	source.open();
    	List<Track> track = source.GetListOfTracks();
    	ArrayAdapter<Track> adapter = new ArrayAdapter<Track>(this,
				android.R.layout.simple_list_item_1, track);      	
    	setListAdapter(adapter);
    	
    	 ListView lv = getListView();
    	  lv.setTextFilterEnabled(true);

    	  lv.setOnItemClickListener(new OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> parent, View view,
    	        int position, long id) {
    	      // When clicked, show a toast with the TextView text
    	      //Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
    	          //Toast.LENGTH_SHORT).show();    	    	
    	    	Track t = (Track)parent.getItemAtPosition(position);
    	    	Intent intent = new Intent();
    	    	intent.putExtra("Selected_Track", t.id);
    	    			setResult(RESULT_OK, intent);
    	    			finish();
    	    }
    	  });

    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}	
}
