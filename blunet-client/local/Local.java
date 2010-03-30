package local;

public class Local {

	static String local = "en";
	
	public static void setLocal(String l) {
		local = l;
	}
	
	public static String getLocal() {
		return local;
	}

	public static String get(String key) {
		if (local.equalsIgnoreCase("cn")) {
			return LabelsCN.get(key);
		}
		return LabelsEN.get(key);
	}
}
