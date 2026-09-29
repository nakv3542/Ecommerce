package com.example.forfinal.service;

import com.example.forfinal.entity.Brand;
import com.example.forfinal.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAll() {
        return brandRepository.findAll();
    }

    public Page<Brand> getAllPaged(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    public Brand getById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    public Brand create(String name, String logoUrl) {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setLogoUrl(logoUrl);
        return brandRepository.save(brand);
    }

    public Brand update(Long id, String name, String logoUrl) {
        Brand brand = getById(id);
        brand.setName(name);
        brand.setLogoUrl(logoUrl);
        return brandRepository.save(brand);
    }

    public void delete(Long id) {
        brandRepository.deleteById(id);
    }
}
