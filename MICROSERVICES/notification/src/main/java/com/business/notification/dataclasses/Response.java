package com.business.notification.dataclasses;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;
import java.util.List;

@RequestScope
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class Response implements Serializable{

	private static final long serialVersionUID = 1L;

	protected String responseCode;
	protected String responseMessage;
	private List<FieldError> errors;

	public Response(String responseCode, String responseMessage) {
		setResponseCode(responseCode);
		setResponseMessage(responseMessage);
	}
	
	// used in reset password api
	public Response(String responseCode) {
		this.responseCode = responseCode;
	}

	public class FieldError implements Serializable {

		private static final long serialVersionUID = 1L;
		private String fieldName;
		private String fieldValue;
		private String errorMessage;

	}

}
