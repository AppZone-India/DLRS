package com.sim2dial.dialer;

public enum FragmentsAvailable
{
	UNKNOW, DIALER, HISTORY,CREDIT_HISTORY, HISTORY_DETAIL, CONTACTS, CONTACT, EDIT_CONTACT, ABOUT, ABOUT_INSTEAD_OF_SETTINGS, ABOUT_INSTEAD_OF_CHAT, ACCOUNT_SETTINGS, SETTINGS, M_SETTINGS, CHATLIST, CHAT, SHOW_COUNTRY, NEWS, SHOW_STATES, SHOW_CITIES;

	public boolean shouldAddToBackStack()
	{
		return true;
	}

	public boolean shouldAnimate()
	{
		return true;
	}

	public boolean isRightOf(FragmentsAvailable fragment)
	{
		switch (this)
		{
			case HISTORY:
				return fragment == UNKNOW;

			case HISTORY_DETAIL:
				return HISTORY.isRightOf(fragment) || fragment == HISTORY;

			case CONTACTS:
				return HISTORY_DETAIL.isRightOf(fragment) || fragment == HISTORY_DETAIL;

			case CONTACT:
				return CONTACTS.isRightOf(fragment) || fragment == CONTACTS;

			case EDIT_CONTACT:
				return CONTACT.isRightOf(fragment) || fragment == CONTACT;

			case DIALER:
				return EDIT_CONTACT.isRightOf(fragment) || fragment == EDIT_CONTACT;
			case CREDIT_HISTORY:
				return DIALER.isRightOf(fragment) || fragment == DIALER;

			case ABOUT_INSTEAD_OF_CHAT:
			case CHATLIST:
				return DIALER.isRightOf(fragment) || fragment == DIALER;

			case CHAT:
				return CHATLIST.isRightOf(fragment) || fragment == CHATLIST;

			case ABOUT_INSTEAD_OF_SETTINGS:
			case SETTINGS:
				return CHATLIST.isRightOf(fragment) || fragment == CHATLIST || fragment == FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT;

			case ABOUT:
			case ACCOUNT_SETTINGS:
				return SETTINGS.isRightOf(fragment) || fragment == SETTINGS;

			default:
				return false;
		}
	}

	public boolean shouldAddItselfToTheRightOf(FragmentsAvailable fragment)
	{
		switch (this)
		{
			case HISTORY_DETAIL:
				return fragment == HISTORY;

			case CONTACT:
				return fragment == CONTACTS;

			case EDIT_CONTACT:
				return fragment == CONTACT || fragment == CONTACTS;

			case CHAT:
				return fragment == CHATLIST;

			default:
				return false;
		}
	}
}
