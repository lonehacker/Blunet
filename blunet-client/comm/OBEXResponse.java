package comm;

/*
00 (0X00) null terminated Unicode text, length prefixed with 2 byte unsigned integer
01 (0X40) byte sequence, length prefixed with 2 byte unsigned integer
10 (0X80) 1 byte quantity
11 (0XC0) 4 byte quantity – transmitted in network byte order (high byte first)
*/

public class OBEXResponse {

	byte opCode;
	byte respCode;
	int len;
	byte OBEXVersion;
	byte flag;
	int maxPacketSize;
	byte[] body;
	OBEXHeaderSet hs;
	
	public OBEXResponse(byte opCode) {
		this.opCode = opCode;
		respCode = (byte)0xA0;
	}
	
	public OBEXHeaderSet getHeaderSet() {
		return hs;
	}
	
	public OBEXResponse(byte opCode, byte[] packet) {
		this.opCode = opCode;
		if (packet == null)
			return;
		
		respCode = packet[0];
		len = ((packet[1]&0XFF)<<8) | (packet[2]&0XFF);
		int offset = 3;
		if (opCode == OBEXRequest.CONNECT){
			OBEXVersion = packet[3];
			flag = packet[4];
			maxPacketSize = ((packet[5]&0XFF)<<8) | (packet[6]&0XFF);
			offset = 7;
		}
		
		hs = new OBEXHeaderSet();
		// retrieve headers and body
		int plen = packet.length;
		while (offset < plen) {
			byte hdcode = packet[offset];
			int d = hdcode & 0xFF; // convert it as an unsigned byte
			d &= 0XC0;
//			11 (0XC0) 4 byte quantity – transmitted in network byte order (high byte first)
			if (d == 0XC0) {
				byte[] cbuf = new byte[4];
				System.arraycopy(packet, offset+1, cbuf, 0, 4);
				OBEXHeader header = new OBEXHeader(hdcode, cbuf);
				hs.setHeader(header);
				offset += 5; //header byte + 4 bytes for length
			}
//			00 (0X00) null terminated Unicode text, length prefixed with 2 byte unsigned integer
//			01 (0X40) byte sequence, length prefixed with 2 byte unsigned integer
			if (d == 0X00 || d == 0X40) {
				// 2 bytes of len
				int hdlen = ((packet[offset+1]&0XFF)<<8) | (packet[offset+2]&0XFF);
				byte[] cbuf = new byte[hdlen-3];// excluding header byte and two bytes for length
				System.out.println("packet length = " + packet.length + ", header length=" + hdlen);
				System.arraycopy(packet, offset+3, cbuf, 0, hdlen-3);
				OBEXHeader header = new OBEXHeader(hdcode, cbuf);
				hs.setHeader(header);
				offset += hdlen;
			}
		}
	}

	public byte[] getBytes() {
		if (len == 0)
			return new byte[0];
		
		byte[] bytes = new byte[len];
		bytes[0] = respCode;
		bytes[1] = (byte)(len >> 8);
		bytes[2] = (byte)(len);
		
		int offset = 3;
		if (opCode == OBEXRequest.CONNECT) {
			bytes[3] = OBEXVersion;
			bytes[4] = flag;
			bytes[5] = (byte)(maxPacketSize>>>8); // byte 5 and 6 - MAX Packet size
			bytes[6] = (byte)maxPacketSize;
			offset = 7;
		}
		if (hs != null) 
			System.arraycopy(hs.getBytes(), 0, bytes, offset, hs.size());
		
		return bytes;
	}
	
	public byte[] getBody() {
		return body;
	}

	public byte getFlag() {
		return flag;
	}

	public int getLen() {
		return len;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public byte getOBEXVersion() {
		return OBEXVersion;
	}

	public int getRespCode() {
		return respCode&0XFF;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public void printBytes() {
//		byte[] b = getBytes();
//		for (int i = 0; i < b.length; i++)
//			System.out.format("%02X ", b[i]);
//		System.out.println();
	}

	public byte getOpCode() {
		return opCode;
	}
}
