package cn.panshi.etf4j.robust;

@SuppressWarnings("serial")
public class EtfRobErr4InvalidTransType extends Exception {
	String error;

	public EtfRobErr4InvalidTransType(String error) {
		super(error);
		this.error = error;
	}

	public String getError() {
		return error;
	}

}
