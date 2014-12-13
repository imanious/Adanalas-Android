package ir.abplus.adanalas.Timeline;

public class FilterMenuItem
{
	
	String header;
	boolean isChecked = false;
	String text;
	boolean isRadioButton;
	int resourceID;
	

	public FilterMenuItem(String header, boolean isChecked, String text, boolean isRadioButton, int resourceID)
	{
		this.header = header;
		this.isChecked = isChecked;
		this.text = text;
		this.isRadioButton = isRadioButton;
		this.resourceID = resourceID;
	}
}
