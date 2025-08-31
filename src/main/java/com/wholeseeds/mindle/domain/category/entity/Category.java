package com.wholeseeds.mindle.domain.category.entity;

import java.util.ArrayList;
import java.util.List;

import com.wholeseeds.mindle.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "category",
	indexes = {
		@Index(name = "idx_category_parent", columnList = "parent_id"),
		@Index(name = "idx_category_parent_id", columnList = "parent_id,id")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

	@Column(length = 100, nullable = false, unique = true)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	// Self-referencing parent
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Category parent;

	// Children collection (bidirectional)
	@Builder.Default
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Category> children = new ArrayList<>();

	// Convenience methods to keep both sides in sync
	public void addChild(Category child) {
		this.children.add(child);
		child.parent = this;
	}

	public void removeChild(Category child) {
		this.children.remove(child);
		child.parent = null;
	}
}
