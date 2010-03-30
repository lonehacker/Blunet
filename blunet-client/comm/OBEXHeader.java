package comm;

public class OBEXHeader {
	byte headerCode = (byte)0x00;
	byte[] headerValue;
	
	public OBEXHeader(byte headerCode, String val) {
		this.headerCode = headerCode;
		int code = headerCode;
		int d = code & 0x00C0;
//		00 (0X00) null terminated Unicode text, length prefixed with 2 byte unsigned integer
		if (d == 0x00) {
			byte[] bytes = null;
			char[] ac = val.toCharArray();
			bytes = new byte[ac.length * 2 + 2];
			for (int i = 0; i < ac.length; i++) {
				bytes[i*2] = (byte)(ac[i] >> 8);
				bytes[i*2+1] = (byte)ac[i];
			}
			// add ending 0x00 for string
			bytes[ac.length*2] = (byte)0x00;
			bytes[ac.length*2+1] = (byte)0x00;
			headerValue = bytes;
		}
		else {
			byte[] b = val.getBytes();
			// add ending 0x00 for string
			headerValue = new byte[b.length + 1];
			System.arraycopy(b, 0, headerValue, 0, b.length);
			headerValue[b.length] = (byte)0x00;
		}
	}
	
	public OBEXHeader(byte headerCode, byte[] val) {
		this.headerCode = headerCode;
		this.headerValue = val;
	}
	
	public OBEXHeader(byte headerCode, int val) {
		this.headerCode = headerCode;
		byte[] bytes = new byte[4];
		bytes[0] = (byte)(val >> 24);
		bytes[1] = (byte)(val >> 16);
		bytes[2] = (byte)(val >> 8);
		bytes[3] = (byte)val;
		this.headerValue = bytes;
	}
	
	public byte[] getBytes() {
		int code = headerCode;
		int d = code & 0x00C0;
		byte[] bytes = null;
//		11 (0XC0) 4 byte quantity – transmitted in network byte order (high byte first)
		if (d == 0X00C0) {
			bytes = new byte[headerValue.length + 1];
			bytes[0] = headerCode;
			System.arraycopy(headerValue, 0, bytes, 1, headerValue.length);
		}
//		00 (0X00) null terminated Unicode text, length prefixed with 2 byte unsigned integer
//		01 (0X40) byte sequence, length prefixed with 2 byte unsigned integer
		if (d == 0X0000 || d == 0X0040) {
			int plen = headerValue.length + 3;
			bytes = new byte[plen];
			bytes[0] = headerCode;
			bytes[1] = (byte)(plen >> 8);
			bytes[2] = (byte)plen;
			System.arraycopy(headerValue, 0, bytes, 3, headerValue.length);
		}
		return bytes;
	}

	public int size() {
		int code = headerCode;
		int d = code & 0x00C0;
//		11 (0XC0) 4 byte quantity – transmitted in network byte order (high byte first)
		if (d == 0X00C0) {
			return headerValue.length + 1;
		}
//		00 (0X00) null terminated Unicode text, length prefixed with 2 byte unsigned integer
//		01 (0X40) byte sequence, length prefixed with 2 byte unsigned integer
		if (d == 0X0000 || d == 0X0040) {
			return headerValue.length + 3;
		}
		return 0;
	}
	public byte getHeaderCode() {
		return headerCode;
	}

	public void setHeaderCode(byte headerCode) {
		this.headerCode = headerCode;
	}

	public byte[] getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(byte[] headerValue) {
		this.headerValue = headerValue;
	}
}
