package com.example.forfinal.controller;

import com.example.forfinal.dto.BrandDTO;
import com.example.forfinal.entity.Brand;
import com.example.forfinal.service.BrandService;
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
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public Object getAll(@RequestParam(required = false) Integer page,
                         @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Page<Brand> paged = brandService.getAllPaged(PageRequest.of(page, size, Sort.by("id")));
            return paged.map(BrandDTO::new);
        }
        return brandService.getAll().stream().map(BrandDTO::new).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public BrandDTO create(@RequestParam String name, @RequestParam(required = false) String logoUrl) {
        return new BrandDTO(brandService.create(name, logoUrl));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public BrandDTO update(@PathVariable Long id, @RequestParam String name,
                           @RequestParam(required = false) String logoUrl) {
        return new BrandDTO(brandService.update(id, name, logoUrl));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.ok().build();
    }
}
