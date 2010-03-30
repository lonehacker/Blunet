package comm;

public class OBEXRequest {

	public final static byte UNKNOWN = (byte)0x00;
	public final static byte CONNECT = (byte)0x80;
	public final static byte DISCONNECT = (byte)0x81;
	public final static byte PUT = (byte)0x02;
	public final static byte FINAL_PUT = (byte)0x82;
	public final static byte GET = (byte)0x83;
	public final static byte SETPATH = (byte)0x85;
	public final static byte ABORT = (byte)0x8FF;

	byte opCode;
	byte setpathFlag = (byte)0x02; // default: backup level - false, Don't create - true
	byte OBEXVersion = (byte)0x10;
	int maxPacketSize = OBEXSession.PACKET_SIZE ; //8192;
	
	OBEXHeaderSet headerSet;
	
	public OBEXRequest(byte opCode) {
		this.opCode = opCode;
	}

	public void setHeaderSet(OBEXHeaderSet headerSet) {
		this.headerSet = headerSet;
	}

	public byte[] getBytes() {
		int offset = 3;
		int len = 0;
		if (headerSet != null)
			len = headerSet.size();
		int size = len;
		if (opCode == CONNECT) {
			size += 7;
		}
		else if (opCode == SETPATH) {
			size += 5;
		}
		else
			size += 3;
		
		byte[] bytes = new byte[size];
		bytes[0] = opCode;
		bytes[1] = (byte)(size >> 8);
		bytes[2] = (byte)(size);
		if (opCode == CONNECT) {
			bytes[3] = OBEXVersion;
			bytes[4] = (byte)0x00; // Flag
			bytes[5] = (byte)(maxPacketSize>>>8); // byte 5 and 6 - MAX Packet size
			bytes[6] = (byte)maxPacketSize;
			offset = 7;
		}
		else if (opCode == SETPATH) {
			bytes[3] = setpathFlag; // flag
			bytes[4] = (byte)0x00; // constant
			offset = 5;
		}
		if (headerSet != null)
			System.arraycopy(headerSet.getBytes(), 0, bytes, offset, len);
		return bytes;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	// Flags take two bits
	// [Backup level|Don't Create]
	public void setSetpathFlag(boolean backup, boolean create) {
		if (!backup && !create)
			setpathFlag = (byte)0x02;
		else if (backup && !create)
			setpathFlag = (byte)0x03;
		else if (!backup && create)
			setpathFlag = (byte)0x00;
		else
			setpathFlag = (byte)0x01;
	}
	
	public byte getSetpathFlag() {
		return setpathFlag;
	}

	public OBEXHeaderSet getHeaderSet() {
		return headerSet;
	}

	public void printBytes() {
//		byte[] b = getBytes();
//		for (int i = 0; i < b.length; i++)
//			System.out.format("%02X ", b[i]);
//		System.out.println();
	}
}
