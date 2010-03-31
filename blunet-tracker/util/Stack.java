package util;

public class Stack {
	private int depth;
	private int sp = -1;
	private Object[] stack;
	
	public Stack(int depth) {
		this.depth = depth;
		reset();
	}
	
	public void reset() {
		this.stack = new Object[depth];
		this.sp = -1;
	}
	
	public int size() {
		return sp+1;
	}
	
	public boolean push(Object obj) {
		if (sp < depth - 1) {
			sp++;
			stack[sp] = obj;
			return true;
		}
		return false;
	}
	
	public Object pop() {
		if (sp >= 0) { 
			Object obj = stack[sp];
			stack[sp] = null;
			sp--;
			return obj;
		}
		else {
			return null;
		}
	}

	public Object[] getAll() {
		int len = size();
		Object[] all = new Object[len];
		for (int i = 0; i < len; i++) {
			all[i] = stack[i];
		}
		return all;
	}
	
	public Object peek() {
		if (sp >= 0) { 
			Object obj = stack[sp];
			return obj;
		}
		else {
			return null;
		}
	}

}
