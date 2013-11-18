package at.sourcefactory.android.simpleshoppinglist;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity {
	
	SQLiteDatabase db;
	
	private int activeCategory = 0;
	LinearLayout linear;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		
		if(savedInstanceState != null) {
			activeCategory = new Integer(savedInstanceState.getString("category_id"));
		} else {
			Bundle extras = getIntent().getExtras();
			if(extras != null) {
				String category_id = extras.getString("category_id");
				if(category_id != null) {
					activeCategory = new Integer(category_id);
				}
			}
		}
		
		if(activeCategory == 0) {
			
			Log.w("WARNING", "No active ID found");
		}
		
		db = (SQLiteDatabase) Registry.get(Registry.DATABASE);
		
		drawOptions();
	}
	
	private void drawOptions() {
		
		Cursor c = db.rawQuery("SELECT id, key, value FROM settings", null);
		
		linear = ((LinearLayout) findViewById(R.id.ContentLinearLayout));
		linear.removeAllViews();
		
		if(c.getCount() > 0) {
			
			c.moveToFirst();
			do {
				
				final int settingId	= c.getInt(c.getColumnIndex("id"));
				String key 		= c.getString(c.getColumnIndex("key"));
				String v 		= c.getString(c.getColumnIndex("value"));
				int value 		= c.getInt(c.getColumnIndex("value"));
				Log.d("currentValue", key);
				Log.d("currentValue int", "" + v);
				
				final String name = getString(getResources().getIdentifier("setting_" + key, "string", "at.sourcefactory.android.simpleshoppinglist"));
				Log.d("name", name);
				
				final TableRow tr = new TableRow(this);
				
				TextView tv = new TextView(this);
				
				tv.setText(name);
				tv.setTextColor(Color.BLACK);
				tv.setTextSize(16);
			    
				tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
				tv.setPadding(4, 0, 0, 0);
				tv.setId(settingId);
		    	
				CheckBox cb = new CheckBox(this);
				cb.setId(settingId * 1000);
				cb.setChecked(value == 1 ? true : false);
				
				tv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						View cb = tr.findViewById(v.getId() * 1000);
						((CheckBox)cb).toggle();
					}
				});
				
				final Settings instance = this;
				
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						ContentValues cv = new ContentValues();
						cv.put("value", isChecked);
						cv.put("id", settingId);
						db.update("settings", cv, "id = " + settingId, null);
						
						Toast t = Toast.makeText(instance, name + " " + (isChecked ? getText(R.string.enabled) : getString(R.string.disabled)), Toast.LENGTH_SHORT);
						t.show();
					}
				});
				
				tr.addView(tv);
				tr.addView(cb);
				
				linear.addView(tr);
				
			} while(c.moveToNext());
			
			c.close();
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_settings, menu);
        
    	return true;
    }
    
    public boolean onOptionsItemSelected (MenuItem item){

    	if(item.getItemId() == R.id.menu_back) {

    		setResult(activeCategory);
    		finish();
    	}

    	return false;
    }
}
