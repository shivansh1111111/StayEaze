package com.business.notification.dataclasses;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.Serializable;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class InlineContent implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Size(min=3,max=60)
	private String contentId;
	
	@NotNull
	@Size(min=3,max=10)
	private String resourceType;
	
	@Size(min=2,max=60)
	private String resourceValue;

	public InlineContent(@NotNull @Size(min = 3, max = 60) String contentId, @NotNull @Size(min = 3, max = 10) String resourceType, @NotNull @Size(min = 2, max = 60) String resourceValue) {
		this.contentId = contentId;
		this.resourceType = resourceType;
		this.resourceValue = resourceValue;
	}

	public InlineContent(@NotNull @Size(min = 3, max = 60) String contentId, @NotNull @Size(min = 3, max = 10) String resourceType, File object) {
		this.contentId = contentId;
		this.resourceType = resourceType;
		this.object = object;
	}

	private File object;
}
