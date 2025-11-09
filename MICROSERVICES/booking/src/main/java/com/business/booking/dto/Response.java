package com.business.booking.dto;

import com.business.booking.util.dataclasses.FieldError;
import com.business.booking.util.dataclasses.Responses;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;
import java.util.List;

@ToString
@RequestScope
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class Response implements Serializable{

	private static final long serialVersionUID = 1L;
	protected String responseCode;

	protected String responseMessage;

	@JsonIgnore
	protected transient Object[] messageArguments;

	private List<FieldError> errors;

	public Response(String responseCode, String responseMessage) {
		setResponseCode(responseCode);
		setResponseMessage(responseMessage);
	}
	
	public Response(Responses response) {
		this(response.getResponseCode(),response.getResponseMessage());
	}

	// used in reset password api
	public Response(String responseCode) {
		this.responseCode = responseCode;
	}
	
	public void setResponse(Responses response){
		setResponseCode(response.getResponseCode());
		setResponseMessage(response.getResponseMessage());
	}

	public void setResponse(Responses response,Object[] messageArguments){
		setResponseCode(response.getResponseCode());
		setResponseMessage(response.getResponseMessage());
		setMessageArguments(messageArguments);
	}
}
