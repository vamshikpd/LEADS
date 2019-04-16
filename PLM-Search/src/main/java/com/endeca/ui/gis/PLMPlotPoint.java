package com.endeca.ui.gis;

public class PLMPlotPoint extends PlotPoint {
	String desc;
	boolean containsMultipleParolees = false;

	public void setDescription(String newDesc) {
		this.desc = newDesc;
	}

	public String getDescription() {
		return this.desc;
	}
	
	public void setMultipleParoleesFlag(boolean flag){
		this.containsMultipleParolees = flag;
	}
	
	public boolean containsMultipleParolees(){
		return this.containsMultipleParolees;
	}
}
