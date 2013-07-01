/**
 * at.mkweb.android.simpleshoppinglist.Item
 * 
 * LICENSE:
 *
 * This file is part of SimpleShoppingList, an Android app to create very simple shopping lists (http://android.mk-web.at/app/simpleshoppinglist.html).
 *
 * SimpleShoppingList is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * SimpleShoppingList is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with software.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @author Mario Klug <mario.klug@mk-web.at>
 * @package at.mkweb.android.simpleshoppinglist
 * 
 * @license http://www.gnu.org/licenses/gpl.html
 */

package at.sourcefactory.android.simpleshoppinglist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import at.mkweb.android.simpleshoppinglist.R;

public class Item extends TableRow implements OnClickListener, OnLongClickListener {

	private SimpleShoppingList context;
	SQLiteDatabase db;
	
	TextView textView;
	
	String name;
	boolean active, sort;
	int id, category_id, sort_nr;
	
	public Item(SimpleShoppingList context) {
		super(context);
		
		this.context = context;
		this.db = ((SQLiteDatabase) Registry.get(Registry.DATABASE));
	    
		setGravity(Gravity.CENTER_VERTICAL);
		
	    setClickable(true);
	    setOnLongClickListener(this);
	    
	    setOnClickListener(this);
	}

	public void setId(int id) {
		
		this.id = id;
	}

	public void setCategoryId(int id) {
		
		this.category_id = id;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public void setActive(boolean active) {
		
		this.active = active;
	}

	public void setSort(boolean sort) {
		
		this.sort = sort;
	}
	
	public void setSortNr(int nr) {
		
		this.sort_nr = nr;
	}
	
	public void create() {
	
		removeAllViews();
		addViews();
	}
	
	@SuppressWarnings("deprecation")
	private void addViews() {
		
		textView = new TextView(context);
	    
		Log.d("debug", "Item.java - addView() - setId(1000000 + " + this.id + ")");
		textView.setText(name);
		textView.setId(1000000 + this.id);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(26);
		
		if(textView.getText().length() >= 19) {
			textView.setTextSize(22);
		}
		
		if(textView.getText().length() >= 23) {
			textView.setTextSize(19);
		}
		
		addView(textView);
		
	    if(active == true && sort == false) {
	    
	    	textView.setPaintFlags(textView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    	
	    	Button renameButton = new Button(context);
	    	renameButton.setText(" ");
	    	renameButton.setTextSize(8);
	    	renameButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_menu_edit));
	    	renameButton.setPaintFlags(renameButton.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    	renameButton.setGravity(FOCUS_RIGHT);
	    	
	    	renameButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					showRenameItemDialog();
				}
			});
	    	
	    	addView(renameButton);
			
	    } else if (sort == true) {
			
	    	textView.setPaintFlags(textView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    	
	    	Button upButton = new Button(context);
	    	upButton.setText(" ");
	    	upButton.setTextSize(8);
	    	upButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.sort_up));
	    	upButton.setPaintFlags(upButton.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    	upButton.setGravity(FOCUS_RIGHT);
	    	
	    	upButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					String sql;
					
					if(sort_nr >= 1) {
						
						int new_nr = sort_nr - 1;
						
						sql = "SELECT `id` FROM `sort` WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) new_nr;
						Log.d("sql", sql);
						Cursor c = db.rawQuery(sql, null);
						
						if(c.getCount() > 0) {
							
							sql = "UPDATE `sort` SET `sort_nr` = 9999 WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) new_nr;
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
							
							sql = "UPDATE `sort` SET `sort_nr` = " + new_nr + " WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) sort_nr;
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
							
							sql = "UPDATE `sort` SET `sort_nr` = " + sort_nr + " WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = 9999";
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
						} else {
							
							sql = "UPDATE `sort` SET `sort_nr` = " + new_nr + " WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) sort_nr;
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
						}
						
						context.updateList();
					}
				}
			});
	    	
	    	addView(upButton);
	    	
	    	Button downButton = new Button(context);
	    	downButton.setText(" ");
	    	downButton.setTextSize(8);
	    	downButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.sort_down));
	    	downButton.setPaintFlags(downButton.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    	downButton.setGravity(FOCUS_RIGHT);
	    	
	    	downButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					int max = 0;
					
					String sql;
					
					sql = "SELECT MAX(`sort_nr`) AS max_nr FROM `sort` WHERE `category_id` = " + (int) category_id;
					Log.d("sql", sql);
					Cursor c = db.rawQuery(sql, null);
					
					Log.d("count", "" + c.getCount());
					
					if(c.getCount() > 0) {
						
						c.moveToFirst();
						
						max = new Integer(c.getString(c.getColumnIndex("max_nr")));
						Log.d("max sort_nr", "" + max);
					}
					Log.d("max_sort_nr", "after");
					
					int new_nr = sort_nr + 1;
					
					if(max > 0 && new_nr <= max) {
						
						sql = "SELECT `id` FROM `sort` WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) new_nr;
						Log.d("sql", sql);
						c = db.rawQuery(sql, null);
						
						if(c.getCount() > 0) {
							
							sql = "UPDATE `sort` SET `sort_nr` = 9999 WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) new_nr;
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
							
							sql = "UPDATE `sort` SET `sort_nr` = " + new_nr + " WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) sort_nr;
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
							
							sql = "UPDATE `sort` SET `sort_nr` = " + sort_nr + " WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = 9999";
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
						} else {
							
							sql = "UPDATE `sort` SET `sort_nr` = " + new_nr + " WHERE `category_id` = " + (int) category_id + " AND `sort_nr` = " + (int) sort_nr;
							Log.d("sql", name + " - " + sql);
							db.execSQL(sql);
						}
						
						context.updateList();
					}
				}
			});
	    	
	    	addView(downButton);
	    	
	    } else {
	    	
	    	textView.setTextColor(Color.GRAY);
	    	textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	    	
	    	Button removeButton = new Button(context);
	    	removeButton.setText(" ");
	    	removeButton.setTextSize(8);
	    	removeButton.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.ic_delete));
	    	removeButton.setPaintFlags(removeButton.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
	    	removeButton.setGravity(FOCUS_RIGHT);
	    	
	    	removeButton.setOnClickListener(this);
			
			addView(removeButton);
	    }
	}

	@Override
	public void onClick(View v) {
		
		if(v.getClass() == Button.class) {
			
			showRemoveDialog();
		} else {
		
			removeAllViews();
			
			if(active) {
				db.execSQL("UPDATE items SET active = 0 WHERE id = '" + this.id + "';");
				active = false;
				addViews();
			} else {
				db.execSQL("UPDATE items SET active = 1 WHERE id = '" + this.id + "';");
				active = true;
				addViews();
			}
		}
		
		context.updateList();
	}
	
	@Override
	public int getId() {
		
		return this.id;
	}
	
	private void showRemoveDialog() {
		
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setPositiveButton(((SimpleShoppingList) context).getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Log.d("Item.java", "Removing " + getId());
				db.execSQL("DELETE FROM items WHERE id = '" + getId() + "';");
				context.updateList();
				dialog.cancel();
				
				db.execSQL("DELETE FROM `sort` WHERE item_id = " + getId() + ";");
				context.updateList();
				dialog.cancel();
				
			}
		});
		b.setNegativeButton(context.getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		});
		
		AlertDialog d = b.create();
		d.setTitle(context.getText(R.string.dialog_remove_title));
		d.setMessage(context.getText(R.string.dialog_remove_message));
		
		d.show();
	}

	@Override
	public boolean onLongClick(View v) {
		
		if(v.getClass() == getClass()) {
			
			showRemoveDialog();
			
			Vibrator vr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			vr.vibrate(30);
		}
		return true;
	}

    public void showRenameItemDialog() {
    	
    	final EditText et = new EditText(context);
    	et.setText(name);
		
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setView(et);
		b.setPositiveButton(context.getText(R.string.button_ok), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String value = et.getText().toString().trim();
				
				if(value.length() > 0) {
					
					Toast.makeText(context, context.getText(R.string.renamed_to) + ": " + value, Toast.LENGTH_LONG).show();
					
					db.execSQL("UPDATE items SET name = '" + value + "' WHERE id = " + getId());
					context.updateList();
				} else {
					
					Toast.makeText(context, context.getText(R.string.err_elem_not_entered).toString(), Toast.LENGTH_LONG).show();
				}
				dialog.cancel();
			}
		});
		
		b.setNegativeButton(context.getText(R.string.button_cancel), new AlertDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				dialog.cancel();
			}
		}); 
		
		AlertDialog dialog = b.create();
		
		dialog.setTitle(context.getText(R.string.dialog_rename_item_title));
		dialog.setMessage(context.getText(R.string.dialog_rename_item_message));
		
		dialog.show();
    }
}
