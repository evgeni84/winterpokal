package de.ea.winterpokal.utils.nav;

public interface NavDrawerItem {
	public int getId();

	public String getLabel();

	public int getType();

	public boolean isEnabled();

	public boolean updateActionBarTitle();
}
