package com.hanson.pintu.activity.pojo;

import java.io.Serializable;

public class PlayParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	private String endDes;
	private boolean bGame;
	public String getEndDes() {
		return endDes;
	}
	public void setEndDes(String endDes) {
		this.endDes = endDes;
	}
	public boolean isbGame() {
		return bGame;
	}
	public void setbGame(boolean bGame) {
		this.bGame = bGame;
	}
}
