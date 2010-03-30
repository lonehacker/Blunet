package comm;

import core.Worker;
import java.io.IOException;

import javax.obex.ResponseCodes;

import util.Kits;

public class OBEXSession extends DataStream {

	final static int PACKET_SIZE = 4096;
	final static int WAITING_TIME = 100; // million seconds
	private byte[] buf = new byte[PACKET_SIZE];
	private byte[] connId = new byte[4];
	private int destPacketSize;
	boolean cancelled;
	final static String SESSION_NOT_CONNECTED = "Session is not connected!";
	
	OBEXListener listener;
	
	public OBEXSession(String url, int w) {
		super(url, w);
	}
	
	public void setOBEXListener(OBEXListener listener) {
		this.listener = listener;
	}
	
	/**
	 * switch into the folder named by folderName. 
	 * If this folder doesn't exist, return error
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	public OBEXResponse changeFolder(String folderName) throws IOException {
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		
		if (folderName == null || 
				(folderName != null && (folderName.length() == 0 || folderName.equals(".")))) // change to current dir, do nothing
			return new OBEXResponse(OBEXRequest.SETPATH);
		
		OBEXResponse resp = null;
		try {
			// change path
			OBEXHeaderSet hs = new OBEXHeaderSet();
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.CONNECTION_ID, connId));
			// "/" means root
			if (folderName.equals("..")) {// back one level higher
				resp = setpath(hs, true, false);
			}
			else {
				hs.setHeader(new OBEXHeader(OBEXHeaderSet.NAME, folderName));
				resp = setpath(hs, false, false);
			}
		}
		catch (IOException e) {
			throw e;
		}
		return resp;
	}
	
	/**
	 * switch into the folder named by folderName. 
	 * If this folder doesn't exist, create one
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	public OBEXResponse createAndChangeFolder(String folderName) throws IOException {
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		
		if (folderName == null || 
				(folderName != null && (folderName.length() == 0 || folderName.equals(".")))) // change to current dir, do nothing
			return new OBEXResponse(OBEXRequest.SETPATH);
		
		OBEXResponse resp = null;
		try {
			// change path
			OBEXHeaderSet hs = new OBEXHeaderSet();
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.CONNECTION_ID, connId));
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.NAME, folderName));
			resp = setpath(hs, false, true);
		}
		catch (IOException e) {
			throw e;
		}
		return resp;
	}
	
	public OBEXResponse getFolder(String folderName) throws IOException {
		return getFile(folderName, null, 0);
	}
		
	public OBEXResponse getFile(String fileName, int numOfBytes) throws IOException {
		return getFile(null, fileName, numOfBytes);
	}
	
	public OBEXResponse getFile(String folderName, String fileName, int numOfBytes) throws IOException {
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		cancelled = false;
		OBEXResponse resp = null;
		try {
			// change path
			if (folderName != null) {
				resp = changeFolder(folderName);
				if (resp.getRespCode() != ResponseCodes.OBEX_HTTP_OK)
					return resp;
			}
			
			OBEXHeaderSet hs = new OBEXHeaderSet();
			hs = new OBEXHeaderSet();
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.CONNECTION_ID, connId));
			if (fileName == null)
				hs.setHeader(new OBEXHeader(OBEXHeaderSet.TYPE, "x-obex/folder-listing"));
			else 
				hs.setHeader(new OBEXHeader(OBEXHeaderSet.NAME, fileName));
			resp = get(hs, numOfBytes);
		}
		catch (IOException e) {
			System.err.println("getFolder: " + e.toString());
		}
		catch (Exception e) {
			System.err.println("getFolder: " + e.toString());
		}
		return resp;
	}
	
	// put file under current directory
	public OBEXResponse putFile( 
			String fileName, String fileType, byte[] content) throws IOException {
		
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		cancelled = false;
		OBEXResponse resp = null;
		try {
			// create HeaderSet for PUT
			OBEXHeaderSet hs = new OBEXHeaderSet();
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.CONNECTION_ID, connId));
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.NAME, fileName));
			if (fileType == null || fileType.length() == 0) 
				fileType = "application/octet-stream";
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.TYPE, fileType));
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.LENGTH, content.length));
			resp = put(hs, content);
		}
		catch (IOException e) {
			System.err.println("putFolder: " + e.toString());
		}
		catch (Exception e) {
			System.err.println("putFolder: " + e.toString());
		}
		
		return resp;
	}
		
	public OBEXResponse connect(OBEXHeaderSet hs) 
										throws IOException {
		OBEXRequest request = new OBEXRequest(OBEXRequest.CONNECT);
		request.setHeaderSet(hs);
		byte[] bytes = request.getBytes();

		dos.write(bytes);
		dos.flush();
		Kits.sleep(WAITING_TIME); // wait data ready in serial port

		//take response
		//some FTP implemented additional security which starts
		//to write data to serial port until receiving manual
		//acknowledgement or timeout. To allow cancel the connection
		//while waiting for manual acknowledge, we must unblock the read
		int len = dis.available();
		while (len <= 0) {
			if (cancelled)
				break;
			Kits.sleep(1000);
			len = dis.available();
		}
		if (cancelled) {
			// make up an unacceptable return
			bytes = new byte[7];
			bytes[0] = (byte)0x00; // user defined, indicating cancelling
			bytes[1] = (byte)0x00;
			bytes[2] = (byte)0x07;
			bytes[3] = (byte)0x10;
			bytes[4] = (byte)0x00;
			bytes[5] = (byte)0x3E;
			bytes[6] = (byte)0x80;
		}
		else {
			bytes = readBytes();
		}
		OBEXResponse resp = new OBEXResponse(OBEXRequest.CONNECT, bytes);

        System.out.println(resp.toString());

        if (resp.getRespCode() != ResponseCodes.OBEX_HTTP_OK)
			return resp;
		
		OBEXHeaderSet rhs = resp.getHeaderSet();
		OBEXHeader connIdHeader = rhs.getHeader(OBEXHeaderSet.CONNECTION_ID);
		System.arraycopy(connIdHeader.getHeaderValue(), 0, connId, 0, 4);
		destPacketSize = resp.getMaxPacketSize();
		
		return resp;
	}

	public OBEXResponse put(OBEXHeaderSet hs, byte[] content) throws IOException {
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		
		hs.setHeader(new OBEXHeader(OBEXHeaderSet.BODY, new byte[0]));
		OBEXRequest request = new OBEXRequest(OBEXRequest.PUT);
		request.setHeaderSet(hs);
		
		byte[] bytes = request.getBytes();
		dos.write(bytes);
		dos.flush();
		byte[] resp = readBytes();

		int pos = 0;
		int contentLength = content.length;
		// actual max body size in a packet is
		// max packet size - [op (3bytes) + connectionId (5 byttes) + body header (3 bytes)] 
		int bodyLength = destPacketSize - (3 + 5 + 3);
		// if response code == 0x90, continue
		while (resp[0] == (byte)0x90) {
			if (cancelled) {
				abort();
				break;
			}
			else {
				
				hs = new OBEXHeaderSet();
				hs.setHeader(new OBEXHeader(OBEXHeaderSet.CONNECTION_ID, connId));
				if (pos < contentLength) {
					int blen = contentLength - pos;
					if (blen > bodyLength)
						blen = bodyLength;
					byte[] body = new byte[blen];
					System.arraycopy(content, pos, body, 0, blen);
					pos += blen;
					hs.setHeader(new OBEXHeader(OBEXHeaderSet.BODY, body));
					request = new OBEXRequest(OBEXRequest.PUT);
				}
				else { // do final PUT
					hs.setHeader(new OBEXHeader(OBEXHeaderSet.END_OF_BODY, new byte[0]));
					request = new OBEXRequest(OBEXRequest.FINAL_PUT);
				}
				request.setHeaderSet(hs);
				bytes = request.getBytes();
	
				dos.write(bytes);
				dos.flush();
				Kits.sleep(WAITING_TIME);
				
				resp = readBytes();
	
				if (listener != null)
					listener.progressUpdate(pos, contentLength);
			}
		}
		OBEXResponse response = new OBEXResponse(OBEXRequest.PUT, resp);
		if (listener != null)
			listener.progressCompleted(response);
		return response;
	}

	public OBEXResponse get(OBEXHeaderSet hs) throws IOException {
		return get(hs, 0);
	}
	
	public OBEXResponse get(OBEXHeaderSet hs, int numOfBytes) throws IOException {
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		OBEXRequest request = new OBEXRequest(OBEXRequest.GET);
		request.setHeaderSet(hs);
		request.printBytes();
		
		byte[] bytes = request.getBytes();
		int rcode = ResponseCodes.OBEX_HTTP_OK;
		OBEXResponse response = new OBEXResponse(OBEXRequest.GET);
		byte[] body = new byte[0];
		int pos = 0;
		int contentLength = numOfBytes;
		do {
			if (cancelled) {
				response = abort();
				break;
			}
			else {
				dos.write(bytes);
				dos.flush();
				byte[] resp = readBytes();
				response = new OBEXResponse(OBEXRequest.GET, resp);
				response.printBytes();
	
				rcode = response.getRespCode();
				OBEXHeaderSet h = response.getHeaderSet();
				OBEXHeader lengthHeader = h.getHeader(OBEXHeaderSet.LENGTH);
				if (lengthHeader != null) { // Length only appears once
					byte[] blen = lengthHeader.getHeaderValue();
					int n = blen.length;
					contentLength = 0;
					for (int i = 0; i < n; i++) {
						contentLength |= (blen[i] & 0XFF) << (8 * (n-i-1));
					}
					body = new byte[contentLength];
				}
				
				if (rcode == 0X90 || rcode == 0XA0) {
					byte[] b = null;
					if (rcode == 0X90) {
						OBEXHeader bodyHeader = h.getHeader(OBEXHeaderSet.BODY);
						if (bodyHeader != null)
							b = bodyHeader.getHeaderValue();
					}
					else {
						OBEXHeader endOfBodyHeader = h.getHeader(OBEXHeaderSet.END_OF_BODY);
						if (endOfBodyHeader != null)
							b = endOfBodyHeader.getHeaderValue();
					}
					if (b != null) {
						if (body == null) {// LENGTH header is optional
							body = new byte[b.length];
						}
						else { // make sure body has enough space
							if (body.length < pos + b.length) {
								byte[] tmp = new byte[body.length];
								System.arraycopy(body, 0, tmp, 0, body.length);
								body = new byte[body.length + b.length];
								System.arraycopy(tmp, 0, body, 0, tmp.length);
							}
						}
						System.arraycopy(b, 0, body, pos, b.length);
						pos += b.length;
					}
				}
			}
			if (listener != null)
				listener.progressUpdate(pos, contentLength);
		} while (rcode == 0X90);
		response.setBody(body);
		if (listener != null)
			listener.progressCompleted(response);
		return response;
	}

	public OBEXResponse setpath(OBEXHeaderSet hs, boolean backup, boolean create)
								throws IOException {
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		OBEXRequest request = new OBEXRequest(OBEXRequest.SETPATH);
		request.setSetpathFlag(backup, create);
		request.setHeaderSet(hs);
		byte[] bytes = request.getBytes();

		OBEXResponse response = null;
		dos.write(bytes);
		dos.flush();
		byte[] resp = readBytes();

		response = new OBEXResponse(OBEXRequest.SETPATH, resp);

		return response;
	}

	public void cancel() {
		cancelled = true;
	}
	
	public OBEXResponse abort() throws IOException {
		if (!isConnected())
			new OBEXResponse(OBEXRequest.ABORT);
		
		byte[] resp = null;
		OBEXHeaderSet hs = new OBEXHeaderSet();
		OBEXHeader header = new OBEXHeader(OBEXHeaderSet.CONNECTION_ID, connId);
		hs.setHeader(header);
		
		OBEXRequest request = new OBEXRequest(OBEXRequest.ABORT);
		request.setHeaderSet(hs);
		byte[] bytes = request.getBytes();

		dos.write(bytes);
		dos.flush();

		// take response
		resp = readBytes();
		Kits.sleep(WAITING_TIME);
		OBEXResponse response = new OBEXResponse(OBEXRequest.ABORT, resp);
		cancelled = false;
		return response;
	}

	public OBEXResponse disconnect(OBEXHeaderSet hs) throws IOException {
		if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);

		OBEXRequest request = new OBEXRequest(OBEXRequest.DISCONNECT);
		request.setHeaderSet(hs);
		byte[] bytes = request.getBytes();
		dos.write(bytes);
		dos.flush();
		// take response
		byte[] resp = readBytes();
		Kits.sleep(WAITING_TIME);
		return new OBEXResponse(OBEXRequest.DISCONNECT, resp);
	}

	String replace(String str, String oldSubstr, String newString) {
		int r = this.url.indexOf(oldSubstr);
		if (r >= 0) {
			String a = str.substring(0, r);
			String b = str.substring(r+oldSubstr.length());
			str = a + newString + b;
		}
		return str;
	}

	byte[] readBytes() throws IOException {
		byte[] resp = new byte[0];
		int len = dis.read(buf, 0, PACKET_SIZE);
		if (len >= 3) {
			resp = new byte[len];
			System.arraycopy(buf, 0, resp, 0, len);
			// get packet length
			int pl = ((resp[1]&0XFF)<<8) | (resp[2]&0XFF);
			if (pl == len)
				return resp;
			// increase array size and continue read
			resp = new byte[pl];
			System.arraycopy(buf, 0, resp, 0, len);
			int index = len;
			while (index < pl) {
				Kits.sleep(WAITING_TIME);
				len = dis.read(buf, 0, PACKET_SIZE);
				if (len > 0) {
					System.arraycopy(buf, 0, resp, index, len);
					index += len;
				}
			}
		}
		return resp;
	}

          //New code for CHAT!!!!!!!!!!!

public OBEXResponse sendChatMessage(String chatMessage) throws IOException {
//public void sendChatMessage(String chatMessage) throws IOException {
    System.out.println("Hiya, this is the sexy string you just typed : "+chatMessage);
	if (!isConnected())
			throw new IOException(SESSION_NOT_CONNECTED);
		cancelled = false;
		OBEXResponse resp = null;
        String fileType;
		try {
			// create HeaderSet for sPUT
			OBEXHeaderSet hs = new OBEXHeaderSet();
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.CONNECTION_ID, connId));
			//hs.setHeader(new OBEXHeader(OBEXHeaderSet.NAME, fileName));
			fileType = "application/x-chat";
			hs.setHeader(new OBEXHeader(OBEXHeaderSet.TYPE, fileType));
			//hs.setHeader(new OBEXHeader(OBEXHeaderSet.LENGTH, chatMessage.length));
            resp = put(hs, chatMessage.getBytes());
		}
		catch (IOException e) {
			System.err.println("sendChatMessage: " + e.toString());
		}
		catch (Exception e) {
			System.err.println("sendChatMessage: " + e.toString());
		}

		System.out.print(resp.toString());
        return resp;
	}
	
}
