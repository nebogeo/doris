package foam.nebogeo.doris_evolved;

public class UshahidiApiResponse {
	protected static class Payload {
		protected String domain;
	}

	protected static class Error {
		protected int code;
		protected String message;
	}

	protected Error error;

	public int getErrorCode() {
		return error.code;
	}
}
