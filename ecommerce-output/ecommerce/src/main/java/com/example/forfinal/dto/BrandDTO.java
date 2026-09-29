package com.example.forfinal.dto;

import com.example.forfinal.entity.Brand;

public class BrandDTO {
    private Long id;
    private String name;
    private String logoUrl;

    public BrandDTO() {}

    public BrandDTO(Brand b) {
        this.id = b.getId();
        this.name = b.getName();
        this.logoUrl = b.getLogoUrl();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLogoUrl() { return logoUrl; }
}
