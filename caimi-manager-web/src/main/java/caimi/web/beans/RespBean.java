package caimi.web.beans;

public class RespBean {
	
	private String status;
	private String msg;
	
	public RespBean() {
		
	}
	
	public RespBean(String status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "RespBean [status=" + status + ", msg=" + msg + "]";
	}
}
