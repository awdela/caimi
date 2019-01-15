package com.caimi.util.beans;

public class RespBean {

	private String status;
    private Object msg;

	public RespBean() {

	}

    public RespBean(String status, Object msg) {
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

    public Object getMsg() {
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
