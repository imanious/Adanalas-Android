package ir.abplus.adanalas.Timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.Charts.ChartActivity;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class FilterMenuAdapter extends SectionedBaseAdapter
{
	private static final int RADIO_BUTTON = 0;
	private static final int CHECK_BOX = 1;
	private ViewHolder holder;
	private HeaderViewHolder headerHolder;
	private LayoutInflater inflater;
	private FilterMenuItem[][] items;
	private SectionPosition radioButtonSelectedPosition = new SectionPosition();
	private ChartActivity parentActivity;
	
	public FilterMenuAdapter(Context context, ChartActivity parentActivity)
	{
		this.parentActivity = parentActivity;
		inflater = LayoutInflater.from(context);
	}

	public void setItems(ArrayList<FilterMenuItem> items)
	{
		ArrayList<ArrayList<FilterMenuItem>> tmp = new ArrayList<ArrayList<FilterMenuItem>>();
		int size = items.size();
		String tmpHeader = "";
		int l = 0;
		List<FilterMenuItem> current = null;
		for(int i = 0; i < size; i++)
		{
			FilterMenuItem cur = items.get(i);
			if(!tmpHeader.equals(cur.header))
			{
				tmp.add(new ArrayList<FilterMenuItem>());
				current = tmp.get(l);
				l++;
			}
			current.add(cur);
		}

		this.items = new FilterMenuItem[tmp.size()][];
		size = tmp.size();
		int rSize = 0;
		for(int i = 0; i < size; i++)
		{
			current = tmp.get(i);
			rSize = tmp.get(i).size();
			this.items[i] = new FilterMenuItem[rSize];
			for(int j = 0; j < rSize; j++)
			{
				this.items[i][j] = current.get(j);
				if(this.items[i][j].isRadioButton && this.items[i][j].isChecked)
				{
					radioButtonSelectedPosition.section = i;
					radioButtonSelectedPosition.position = j;
				}
			}
		}
	}

	@Override
	public Object getItem(int section, int position)
	{
		return items[section][position];
	}

	@Override
	public long getItemId(int section, int position)
	{
		return 100*section+position;
	}

	@Override
	public int getSectionCount()
	{
		return items.length;
	}

	@Override
	public int getCountForSection(int section)
	{
		return items[section].length;
	}

	@Override
	public int getItemViewType(int section, int position)
	{
		return items[section][position].isRadioButton? RADIO_BUTTON: CHECK_BOX;
	}
	
	@Override
	public View getItemView(int section, int position, View convertView, ViewGroup parent)
	{
//        System.out.println(convertView.getTag().getClass().toString());
//		if(convertView == null || convertView.getTag().getClass().toString().equals("ir.abplus.adanalas.armin.FilterMenuAdapter$HeaderViewHolder"))
//		{
			holder = new ViewHolder();
			
			switch(getItemViewType(section, position))
			{
			case RADIO_BUTTON:
				convertView = inflater.inflate(R.layout.radiobutton_menu_item, parent, false);
				holder.radioButton = (RadioButton) convertView.findViewById(R.id.side_menu_radiobutton);
				break;
			case CHECK_BOX:
				convertView = inflater.inflate(R.layout.checkbox_menu_item, parent, false);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.side_menu_checkbox);
				holder.img = (ImageView) convertView.findViewById(R.id.side_menu_icon);
				break;
			}
			
			convertView.setTag(holder);
//		}
//		else
//		{
//			holder = (ViewHolder) convertView.getTag();
//		}

		switch(getItemViewType(section, position))
		{
		case RADIO_BUTTON:
			holder.radioButton.setTypeface(TimelineActivity.persianTypeface);
			holder.radioButton.setChecked(samePosition(section, position, radioButtonSelectedPosition));
			holder.radioButton.setTag(new SectionPosition(section, position));
			holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println();
                    radioButtonSelectedPosition = (SectionPosition) v.getTag();
                    notifyDataSetInvalidated();
                    if (parentActivity != null)
                        parentActivity.setFromDateTextview();
                }
            });
			holder.radioButton.setText(items[section][position].text);
			break;
		case CHECK_BOX:
			holder.img.setImageResource(items[section][position].resourceID);
			holder.img.setTag(new SectionPosition(section, position));
			holder.img.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					SectionPosition sp = (SectionPosition) v.getTag();
					items[sp.section][sp.position].isChecked = !items[sp.section][sp.position].isChecked;
					notifyDataSetInvalidated();
				}
			});
			holder.checkBox.setTypeface(TimelineActivity.persianTypeface);
			holder.checkBox.setChecked(items[section][position].isChecked);
			holder.checkBox.setText(items[section][position].text);
			holder.checkBox.setTag(new SectionPosition(section, position));
			holder.checkBox.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					SectionPosition sp = (SectionPosition) v.getTag();
					items[sp.section][sp.position].isChecked = !items[sp.section][sp.position].isChecked;
				}
			});
			break;
		}

		return convertView;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent)
	{
//        if(convertView == null)
//		{
			headerHolder = new HeaderViewHolder();
			convertView = inflater.inflate(R.layout.header_menu, parent, false);
			headerHolder.text= (TextView) convertView.findViewById(R.id.side_menu_header);
			convertView.setTag(headerHolder);
//		}
//		else
//		{
//			headerHolder = (HeaderViewHolder) convertView.getTag();
//		}

		headerHolder.text.setTypeface(TimelineActivity.persianTypeface);
		headerHolder.text.setText(items[section][0].header);

		return convertView;
	}
	
	public int getRadioSelected()
	{
		return radioButtonSelectedPosition.position;
	}
    public void setRadioSelected(){
       for(int i=0;i<items.length;i++)
           for(int j=0;j<items[i].length;j++)
               if(this.items[i][j].isRadioButton && this.items[i][j].isChecked)
               {
                   radioButtonSelectedPosition.section = i;
                   radioButtonSelectedPosition.position = j;
               }
    }
	
	public boolean[] getAccountsSelection()
	{
		boolean[] a = new boolean[getCountForSection(0)];
		for(int i = 0; i < a.length; i++)
			a[i] = items[0][i].isChecked;
		
		return a;
	}

    public void setAccountsSelection(boolean[] input)
    {
        for(int i = 0; i < input.length; i++)
        {if(input[i])
        items[0][i].isChecked=true;
        }
    }

    public String getSelectedAccountString(int position)
    {
        return items[0][position].text;
    }



	class ViewHolder
	{
		ImageView img;
		CheckBox checkBox;
		RadioButton radioButton;
	}
	
	class HeaderViewHolder
	{
		TextView text;
	}
	
	class SectionPosition
	{
		int section;
		int position;

		public SectionPosition()
		{
		}
		
		public SectionPosition(int section, int position)
		{
			this.section = section;
			this.position = position;
		}
	}
	
	private boolean samePosition(int s1, int p1, SectionPosition sp)
	{
		return s1 == sp.section && p1 == sp.position;
	}
}
