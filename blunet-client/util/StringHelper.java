package util;

import java.util.Vector;

public class StringHelper {

	public static String[] split(String str, String sep) {
		Vector v = new Vector();

		int q = 0;
		int r = str.indexOf(sep);
		while (r >= 0) {
			v.addElement(str.substring(q,r));
			q = r+sep.length();
			r = str.indexOf(sep, q);
		}
		v.addElement(str.substring(q));
		String[] rep = new String[v.size()];
		int i = 0;
		while (!v.isEmpty()){
			rep[i++] = (String) v.elementAt(0);
			v.removeElementAt(0);
		}
		return rep;
	}

	public static String[] split(String str, char sep) {
		Vector v = new Vector();

		int q = 0;
		int r = str.indexOf(sep);
		while (r >= 0) {
			v.addElement(str.substring(q,r));
			q = r+1;
			r = str.indexOf(sep, q);
		}
		v.addElement(str.substring(q));
		String[] rep = new String[v.size()];
		int i = 0;
		while (!v.isEmpty()){
			rep[i++] = (String) v.elementAt(0);
			v.removeElementAt(0);
		}
		return rep;
	}

}
