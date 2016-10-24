package com.hanson.pintu.data.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class Setting implements Serializable {
	private static final long serialVersionUID = 1L;
	private String mode = "easy";
	private String screen = "vertical";
	private int numRow = 4;
	private int numColoum = 4;
	private boolean bSound = true;
	private HistoryRecord historyRecord = null;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public int getNumRow() {
		return numRow;
	}

	public void setNumRow(int numRow) {
		this.numRow = numRow;
	}

	public int getNumColoum() {
		return numColoum;
	}

	public void setNumColoum(int numColoum) {
		this.numColoum = numColoum;
	}

	public boolean isbSound() {
		return bSound;
	}

	public void setbSound(boolean bSound) {
		this.bSound = bSound;
	}

	public HistoryRecord getHistoryRecord() {
		if(historyRecord == null)
			historyRecord = new HistoryRecord();
		return historyRecord;
	}

	public void setHistoryRecord(HistoryRecord historyRecord) {
		this.historyRecord = historyRecord;
	}

	public class HistoryRecord implements Serializable {
		private static final long serialVersionUID = 2L;
		private int autoIndex = -1;
		private long id = -1;
		private ArrayList<int[]> record = null;

		public int getAutoIndex() {
			return autoIndex;
		}

		public void setAutoIndex(int autoIndex) {
			this.autoIndex = autoIndex;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public ArrayList<int[]> getRecord() {
			return record;
		}

		public void setRecord(ArrayList<int[]> record) {
			this.record = record;
		}
	}
}
