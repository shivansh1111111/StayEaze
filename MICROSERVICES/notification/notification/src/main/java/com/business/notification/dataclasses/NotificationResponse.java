package com.business.notification.dataclasses;

public class NotificationResponse extends Response {

	public NotificationResponse(Responses response) {
		super(String.valueOf(response));
	}

	private static final long serialVersionUID = 1L;

}
