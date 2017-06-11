/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

/**
 *
 * @author Rob
 */
public class ObjectURL {
    private String URL;
	private int ActualContentLength;
	private String SegmentWARC;
	private int Offset;
		

	public int getOffset() {
		return Offset;
	}

	public void setOffset(int offset) {
		Offset = offset;
	}

	public String getSegmentWARC() {
		return SegmentWARC;
	}

	public void setSegmentWARC(String segmentWARC) {
		SegmentWARC = segmentWARC;
	}

	public int getActualContentLength() {
		return ActualContentLength;
	}

	public void setActualContentLength(int actualContentLength) {
		ActualContentLength = actualContentLength;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
}
