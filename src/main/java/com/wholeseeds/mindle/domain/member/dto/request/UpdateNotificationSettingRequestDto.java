package com.wholeseeds.mindle.domain.member.dto.request;

import com.wholeseeds.mindle.domain.member.enums.NotificationType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateNotificationSettingRequestDto {

	@NotNull
	private NotificationType type;

	@NotNull
	private Boolean enabled;
}
