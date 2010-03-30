package comm;

public class OBEXHeaderSet {
	
	final public static int UNKNOWN_INDEX = 0;
	final public static int COUNT_INDEX = 1;
	final public static int NAME_INDEX = 2;
	final public static int TYPE_INDEX = 3;
	final public static int LENGTH_INDEX = 4;
	final public static int TIME_INDEX = 5;
	final public static int DESCRIPTION_INDEX = 6;
	final public static int TARGET_INDEX = 7;
	final public static int HTTP_INDEX = 8;
	final public static int BODY_INDEX = 9;
	final public static int END_OF_BODY_INDEX = 10;
	final public static int WHO_INDEX = 11;
	final public static int CONNECTION_ID_INDEX = 12;
	final public static int APP_PARAMETERS_INDEX = 13;
	final public static int AUTH_CHALLENG_INDEX = 14;
	final public static int AUTH_RESPONSE_INDEX = 15;
	final public static int HEADER_SIZE = 16;

	final public static byte UNKNOWN = (byte)0x00;
	final public static byte COUNT = (byte)0xC0;
	final public static byte NAME = (byte)0x01;
	final public static byte TYPE = (byte)0x42;
	final public static byte LENGTH = (byte)0xC3;
	final public static byte TIME = (byte)0x44;
	final public static byte DESCRIPTION = (byte)0x05;
	final public static byte TARGET = (byte)0x46;
	final public static byte HTTP = (byte)0x47;
	final public static byte BODY = (byte)0x48;
	final public static byte END_OF_BODY = (byte)0x49;
	final public static byte WHO = (byte)0x4A;
	final public static byte CONNECTION_ID = (byte)0xCB;
	final public static byte APP_PARAMETERS = (byte)0x4C;
	final public static byte AUTH_CHALLENG = (byte)0x4D;
	final public static byte AUTH_RESPONSE = (byte)0x4E;

	final public static byte[] HEADER_BYTE = {
		UNKNOWN, 
		COUNT, NAME, TYPE, LENGTH, TIME,
		DESCRIPTION, TARGET, HTTP, BODY, END_OF_BODY,
		WHO, CONNECTION_ID, APP_PARAMETERS, AUTH_CHALLENG, AUTH_RESPONSE};

	private OBEXHeader[] headers = new OBEXHeader[HEADER_SIZE];
	private int[] order = new int[HEADER_SIZE]; // use to record order that Header is added
	private int headerCount = 0;

	public OBEXHeaderSet() {
		for (int i = 0; i < HEADER_SIZE; i++)
			order[i] = -1;
	}

	public void setHeader(OBEXHeader header) {
		int idx = getHeaderIndex(header.getHeaderCode());
		int i = 0;
		for (; i < headerCount; i++)
			if (order[i] == idx)
				break;
		if (i == headerCount)
			order[headerCount++] = idx;
		headers[idx] = header;
	}
	
	public OBEXHeader[] getHeaders() {
		return headers;
	}

	public OBEXHeader getHeader(byte header) {
		return headers[getHeaderIndex(header)];
	}

	public OBEXHeader getHeader(int headerIndex) {
		return headers[headerIndex];
	}

	public int size() {
		int len = 0;
		for (int i = 1; i < HEADER_SIZE; i++) {
			if (headers[i] != null)
				len += headers[i].size();
		}
		return len;
	}
	
	public byte[] getBytes() {
		int len = size();
		byte[] bytes = new byte[len];
		int offset = 0;
		for (int i = 0; i < headerCount; i++) {
			int j = order[i];
			if (headers[j] != null) {
				int hsize = headers[j].size();
				System.arraycopy(headers[j].getBytes(), 0, bytes, offset, hsize);
				offset += hsize;
			}
		}
		return bytes;
	}
	
	static public int getHeaderIndex(byte header) {
		if (header == UNKNOWN) return UNKNOWN_INDEX;
		else if (header == COUNT) return COUNT_INDEX;
		else if (header == NAME) return NAME_INDEX;
		else if (header == TYPE) return TYPE_INDEX;
		else if (header == LENGTH) return LENGTH_INDEX;
		else if (header == TIME) return TIME_INDEX;
		else if (header == DESCRIPTION) return DESCRIPTION_INDEX;
		else if (header == TARGET) return TARGET_INDEX;
		else if (header == HTTP) return HTTP_INDEX;
		else if (header == BODY) return BODY_INDEX;
		else if (header == END_OF_BODY) return END_OF_BODY_INDEX;
		else if (header == WHO) return WHO_INDEX;
		else if (header == CONNECTION_ID) return CONNECTION_ID_INDEX;
		else if (header == APP_PARAMETERS) return APP_PARAMETERS_INDEX;
		else if (header == AUTH_CHALLENG) return AUTH_CHALLENG_INDEX;
		else if (header == AUTH_RESPONSE) return AUTH_RESPONSE_INDEX;
		else return UNKNOWN_INDEX;
	}

}
