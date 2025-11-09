package com.business.booking.util.dataclasses;

import com.business.booking.dto.Response;

public class NotificationResponse extends Response {

	public NotificationResponse(Responses response) {
		super(String.valueOf(response));
	}

	private static final long serialVersionUID = 1L;

	public NotificationResponse(String failure, String message) {
	}
}
