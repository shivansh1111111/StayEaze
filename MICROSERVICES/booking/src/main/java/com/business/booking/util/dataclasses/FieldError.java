package com.business.booking.util.dataclasses;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class FieldError implements Serializable{

	private static final long serialVersionUID = 1L;
	private String fieldName;
	private String fieldValue;
	private String errorMessage;

}
