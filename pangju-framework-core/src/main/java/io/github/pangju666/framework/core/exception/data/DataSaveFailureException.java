package io.github.pangju666.framework.core.exception.data;

public class DataSaveFailureException extends DataAccessException {
	public DataSaveFailureException() {
		super("数据保存失败");
	}

	public DataSaveFailureException(String message) {
		super(message);
	}

	public DataSaveFailureException(String message, Throwable cause) {
		super(message, cause);
	}
}