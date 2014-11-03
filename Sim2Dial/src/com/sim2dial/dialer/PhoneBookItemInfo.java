package com.sim2dial.dialer;

public class PhoneBookItemInfo
{
	public String	name	= "";
	public String	contactID	= "";
	public String getContactID()
	{
		return contactID;
	}
	public void setContactID(String contactID)
	{
		this.contactID = contactID;
	}
	public String	Number	= "";
	public Object		userData;
	public String	isStred	= "";
	public Object getUserData()
	{
		return userData;
	}
	public void setUserData(Object userData)
	{
		this.userData = userData;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getNumber()
	{
		return Number;
	}
	public void setNumber(String number)
	{
		Number = number;
	}
	public String getIsStred()
	{
		return isStred;
	}
	public void setIsStred(String isStred)
	{
		this.isStred = isStred;
	}

}
