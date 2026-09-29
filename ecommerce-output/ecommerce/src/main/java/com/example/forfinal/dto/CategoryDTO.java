package com.example.forfinal.dto;

import com.example.forfinal.entity.Category;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private List<CategoryDTO> children;

    public CategoryDTO() {}

    public CategoryDTO(Category c) {
        this.id = c.getId();
        this.name = c.getName();
        this.slug = c.getSlug();
        if (c.getParent() != null) this.parentId = c.getParent().getId();
        if (c.getChildren() != null && !c.getChildren().isEmpty()) {
            this.children = c.getChildren().stream().map(CategoryDTO::new).collect(Collectors.toList());
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public Long getParentId() { return parentId; }
    public List<CategoryDTO> getChildren() { return children; }
}
