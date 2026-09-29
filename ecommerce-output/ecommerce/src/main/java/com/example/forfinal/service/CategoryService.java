package com.example.forfinal.service;

import com.example.forfinal.entity.Category;
import com.example.forfinal.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Page<Category> getAllPaged(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Category create(String name, String slug, Long parentId) {
        Category cat = new Category();
        cat.setName(name);
        cat.setSlug(slug);
        if (parentId != null) {
            cat.setParent(getById(parentId));
        }
        return categoryRepository.save(cat);
    }

    public Category update(Long id, String name, String slug, Long parentId) {
        Category cat = getById(id);
        cat.setName(name);
        cat.setSlug(slug);
        if (parentId != null) {
            cat.setParent(getById(parentId));
        }
        return categoryRepository.save(cat);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
