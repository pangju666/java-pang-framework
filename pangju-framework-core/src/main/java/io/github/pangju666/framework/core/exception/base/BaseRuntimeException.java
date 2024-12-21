package io.github.pangju666.framework.core.exception.base;

import io.github.pangju666.framework.core.lang.pool.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public abstract class BaseRuntimeException extends RuntimeException {
	private int code;
	private int httpStatus;

	protected BaseRuntimeException(String message) {
		super(message);
		this.code = Constants.BASE_ERROR_RESPONSE_CODE;
		this.httpStatus = Constants.OK_HTTP_STATUS_CODE;
	}

	protected BaseRuntimeException(int code, String message) {
		super(message);
		this.code = code;
		this.httpStatus = Constants.OK_HTTP_STATUS_CODE;
	}

	protected BaseRuntimeException(String message, Throwable cause) {
		super(message, cause);
		this.code = Constants.BASE_ERROR_RESPONSE_CODE;
		this.httpStatus = Constants.OK_HTTP_STATUS_CODE;
	}

	protected BaseRuntimeException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.httpStatus = Constants.OK_HTTP_STATUS_CODE;
	}

	public int getCode() {
		return code;
	}

	protected void setCode(int code) {
		this.code = code;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	protected void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public void log(Logger logger) {
		logger.error(this.getMessage(), this);
	}

	public void log(Logger logger, Level level) {
		logger.atLevel(level)
			.setCause(this)
			.log(this.getMessage());
	}

	public Throwable getRootCause() {
		Throwable rootCause = null;
		Throwable cause = this.getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return (rootCause != null ? rootCause : this);
	}

	public Throwable getMostSpecificCause() {
		return ObjectUtils.defaultIfNull(getRootCause(), this);
	}

	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause == this) {
			return false;
		}
		if (cause instanceof BaseRuntimeException e) {
			return e.contains(exType);
		} else {
			while (cause != null) {
				if (exType.isInstance(cause)) {
					return true;
				}
				if (cause.getCause() == cause) {
					break;
				}
				cause = cause.getCause();
			}
			return false;
		}
	}
}