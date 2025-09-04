package com.wholeseeds.mindle.domain.moderation.entity;

import com.wholeseeds.mindle.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "profanity",
	uniqueConstraints = @UniqueConstraint(name = "uk_profanity_word", columnNames = "word"),
	indexes = @Index(name = "idx_profanity_word", columnList = "word")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Profanity extends BaseEntity {

	@Column(name = "word", length = 100, nullable = false)
	private String word;

	public static Profanity of(String word) {
		return Profanity.builder().word(word).build();
	}
}
