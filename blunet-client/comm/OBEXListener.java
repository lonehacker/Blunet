package comm;

public interface OBEXListener {
	public void progressUpdate(int part, int total);
	public void progressCompleted(Object obj);
}
