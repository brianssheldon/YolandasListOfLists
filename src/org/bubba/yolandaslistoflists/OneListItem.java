package org.bubba.yolandaslistoflists;

public class OneListItem
{
	private long id;
	private String listName;
	private String item;
	private int quantity;
	private int deletedNumber;
	
	public String getListName()
	{
		return listName;
	}

	public void setListName(String listName)
	{
		this.listName = listName;
	}

	public String getItem()
	{
		return item;
	}

	public void setItem(String item)
	{
		this.item = item;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString()
	{
		return listName;
	}

	public String myToString()
	{
		return "id '" + id 
				+ "'  listName '" + listName 
				+ "'  item '" + item 
				+ "'  qty '" + quantity
				+ "'  deleteNumber '" + deletedNumber + "'";
	}

	public int getDeletedNumber()
	{
		return deletedNumber;
	}

	public void setDeletedNumber(int deletedNumber)
	{
		this.deletedNumber = deletedNumber;
	}
}