package com.example.forfinal.controller;

import com.example.forfinal.dto.CategoryDTO;
import com.example.forfinal.entity.Category;
import com.example.forfinal.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Object getAll(@RequestParam(required = false) Integer page,
                         @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Page<Category> paged = categoryService.getAllPaged(PageRequest.of(page, size, Sort.by("id")));
            return paged.map(CategoryDTO::new);
        }
        return categoryService.getAll().stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/roots")
    public List<CategoryDTO> getRoots() {
        return categoryService.getRootCategories().stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryDTO getById(@PathVariable Long id) {
        return new CategoryDTO(categoryService.getById(id));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public CategoryDTO create(@RequestParam String name, @RequestParam String slug,
                              @RequestParam(required = false) Long parentId) {
        return new CategoryDTO(categoryService.create(name, slug, parentId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public CategoryDTO update(@PathVariable Long id, @RequestParam String name,
                              @RequestParam String slug, @RequestParam(required = false) Long parentId) {
        return new CategoryDTO(categoryService.update(id, name, slug, parentId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
