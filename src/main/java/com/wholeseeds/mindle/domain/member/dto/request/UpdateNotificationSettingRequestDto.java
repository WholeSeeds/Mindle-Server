package com.wholeseeds.mindle.domain.member.dto.request;

import com.wholeseeds.mindle.domain.member.enums.NotificationType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationSettingRequestDto {

	@NotNull
	private NotificationType type;

	@NotNull
	private Boolean enabled;
}
