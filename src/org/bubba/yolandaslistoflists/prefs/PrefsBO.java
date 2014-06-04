package org.bubba.yolandaslistoflists.prefs;

import java.io.Serializable;

public class PrefsBO implements Serializable
{
	private static final long serialVersionUID = -5585655096772543186L;
	public static String ALPHA_SORT_ORDER = "alpha";
	public static String DRAG_SORT_ORDER = "drag";
	private String oneListSort = ALPHA_SORT_ORDER;
	private int displayFont = 0;
	
	public String getOneListSort()
	{
		return oneListSort;
	}
	public void setOneListSort(String oneListSort)
	{
		this.oneListSort = oneListSort;
	}
	public int getDisplayFont()
	{
		return displayFont;
	}
	public void setDisplayFont(int displayFont)
	{
		this.displayFont = displayFont;
	}
	
	public String toString()
	{
		return "oneListSort: '" + getOneListSort() + "'; displayFont: '" + getDisplayFont() + "'";
	}
}