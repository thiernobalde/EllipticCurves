package crypto;

import java.io.Serializable;

import math.Point;

public class Data implements Serializable{

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		byte[] data;
		byte[] rc;
		byte[]sc;
		public byte[] getRc() {
			return rc;
		}

		public void setRc(byte[] rc) {
			this.rc = rc;
		}

		public byte[] getSc() {
			return sc;
		}

		public void setSc(byte[] sc) {
			this.sc = sc;
		}
		Point p;
		public Data(byte[]rc, byte[]sc, Point p)
		{
			this.rc = rc;
			this.sc = sc;
			this.p = p;
		}
		public Data(byte[] data)
		{
			
			this.p = null;
			this.data = data;
		}
		
		public Data(Point p)
		{
			
			this.p = p;
			this.data = null;
		}
		
		public Data(byte[] data, Point p)
		{
			
			this.p = p;
			this.data = data;
		}
		
		public Point getP() {
			return p;
		}

		public void setP(Point p) {
			this.p = p;
		}

		public byte[] getData() {
			return data;
		}
		public void setData(byte[] data) {
			this.data = data;
		}
}
