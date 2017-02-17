package com.zh.radio.autotest;

interface ATRadio{

	/**
	 * @param band: am/fm
	 * @return
	 */
	boolean setBand(String band);
	
	/**
	 * @param freq as 87.5
	 * @return
	 */
	boolean setFreq(String freq);

}