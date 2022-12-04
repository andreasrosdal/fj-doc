package org.fugerit.java.doc.lib.simpletable.model;

import java.util.ArrayList;
import java.util.List;

import org.fugerit.java.core.lang.helpers.BooleanUtils;

public class SimpleRow {


	private List<SimpleCell> cells;

	private String head;
	
	public List<SimpleCell> getCells() {
		return cells;
	}
	
	public void addCell( String content ) {
		this.getCells().add( new SimpleCell(content) );
	}
	
	public SimpleRow() {
		this.cells = new ArrayList<SimpleCell>();
		this.head = BooleanUtils.BOOLEAN_FALSE;
	}
	
	public SimpleRow(String head) {
		this();
		this.head = head;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}
	
	
}
