/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bubba.yolandaslistoflists.dragndrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bubba.yolandaslistoflists.sql.ListOfListsDataSource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public final class DragNDropAdapter extends BaseAdapter implements RemoveListener, DropListener{

	private int[] mIds;
    private int[] mLayouts;
    private String[] itemNames;
    private LayoutInflater mInflater;
//    private ArrayList<String> mContent;
    private ArrayList<HashMap<String, String>> mContent;
	private ListOfListsDataSource datasource;

//    public DragNDropAdapter(Context context, ArrayList<String> content) {
//        init(context,new int[]{android.R.layout.simple_list_item_1},new int[]{android.R.id.text1}, content);
//    }
    
//    public DragNDropAdapter(Context context, int[] itemLayouts, int[] itemIDs, ArrayList<String> content)
//    {
//    	init(context,itemLayouts,itemIDs, content);
//    }
    
    public DragNDropAdapter(Context context, 
					    	int[] itemLayouts, 
					    	String[] itemNames, 
					    	int[] itemIDs, 
					    	ArrayList<HashMap<String, String>> content)
    {
    	init(context,itemLayouts, itemNames, itemIDs, content);
    }

//    public DragNDropAdapter(DragNDropListActivity context, int[] itemLayouts,
//			String[] strings, 
//			int[] itemIDs,
//			ArrayList<HashMap<String, String>> mylist)
//	{
//    	init(context,itemLayouts, itemNames, itemIDs, content);
//	}

	private void init(Context context, 
			    	int[] layouts,
			    	String[] itemNames,  
			    	int[] ids, 
			    	ArrayList<HashMap<String, String>> content)
    {
    	// Cache the LayoutInflate to avoid asking for a new one each time.
    	mInflater = LayoutInflater.from(context);
    	mIds = ids;
    	this.itemNames = itemNames;
    	mLayouts = layouts;
    	mContent = content; datasource = new ListOfListsDataSource(context);
		datasource.open();
    }

//    private void init(Context context, 
//    	int[] layouts,
//    	int[] ids, 
//    	ArrayList<String> content)
//    {
//    	// Cache the LayoutInflate to avoid asking for a new one each time.
//    	mInflater = LayoutInflater.from(context);
//    	mIds = ids;
//    	mLayouts = layouts;
//    	mContent = content;
//    }
    
    /**
     * The number of items in the list
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount()
    {
        return mContent.size();
    }

    /**
     * Since the data comes from an array, just returning the index is
     * sufficient to get at the data. If we were using a more complex data
     * structure, we would return whatever object represents one row in the
     * list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public HashMap<String, String> getItem(int position) {
        return mContent.get(position);
    }

    /**
     * Use the array index as a unique id.
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;
        ViewHolder holder2;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null)
        {
            convertView = mInflater.inflate(mLayouts[0], null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(mIds[0]);

            holder2 = new ViewHolder();
            holder2.text = (TextView) convertView.findViewById(mIds[1]);

            convertView.setTag(holder);
        }
        else
        {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
            holder2 = new ViewHolder();
            holder2.text = (TextView) convertView.findViewById(mIds[1]);
        }

        // Bind the data efficiently with the holder.
        HashMap<String, String> hashMap = mContent.get(position);
		String key = itemNames[0];
		String text = hashMap.get(key);
		holder.text.setText(text);

//        HashMap<String, String> hashMap2 = mContent.get(position);
		String key2 = itemNames[1];
		String text2 = hashMap.get(key2);
		holder2.text.setText(text2);

        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }

	public void onRemove(int which) {
		if (which < 0 || which > mContent.size()) return;		
		mContent.remove(which);
	}

	public void onDrop(int from, int to, String listNameToShow)
	{
		HashMap<String, String> fromMap = mContent.get(from);
		HashMap<String, String> toMap = mContent.get(to);
		mContent.remove(from);
		mContent.add(to,fromMap);
//		datasource.updateSortByThisNumber(listNameToShow, fromMap.get("oneItem"), to);
//		datasource.updateSortByThisNumber(listNameToShow, toMap.get("oneItem"), from);
		int counter = 0;
		
		for (Iterator iterator = mContent.iterator(); iterator.hasNext();)
		{
			HashMap<String,String> map = (HashMap) iterator.next();
			
			String item = map.get("oneItem");
			String qty = map.get("oneQuantity");
			
			datasource.updateSortByThisNumber(listNameToShow, item, counter);
			counter +=1;			
		}
	}
}