package com.business.notification.dataclasses;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NotificationRequest {

	private static final long serialVersionUID = 1L;
	private String to;
	private String from;
	private List<String> cc;
	private List<String> bcc;
	private String emailCode;
	private List<InlineContent> inlineContents;
	
	private List<InlineContent> inlineContentsForSubject;
	private String preferredLanguage;
	private String subject;
	private String emailContent;

	private String emailCodeIdentifier;
	private Integer businessEntityId;
}
