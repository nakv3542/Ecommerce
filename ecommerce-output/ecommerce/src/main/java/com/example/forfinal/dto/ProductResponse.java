package com.example.forfinal.dto;

import com.example.forfinal.entity.*;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private double price;
    private Double discountPrice;
    private int stock;
    private String sku;
    private boolean featured;
    private double averageRating;
    private int reviewCount;
    private BrandDTO brand;
    private List<CategoryDTO> categories;
    private List<String> images;

    public ProductResponse() {}

    public ProductResponse(Product p) {
        this.id = p.getId();
        this.name = p.getName();
        this.description = p.getDescription();
        this.price = p.getPrice();
        this.discountPrice = p.getDiscountPrice() > 0 ? p.getDiscountPrice() : null;
        this.stock = p.getStock();
        this.sku = p.getSku();
        this.featured = p.isFeatured();
        this.averageRating = p.getAverageRating();
        this.reviewCount = p.getReviewCount();
        if (p.getBrand() != null) this.brand = new BrandDTO(p.getBrand());
        this.categories = p.getCategories().stream().map(CategoryDTO::new).collect(Collectors.toList());
        this.images = p.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList());
        if (this.images.isEmpty() && p.getImages().isEmpty()) {
            this.images = List.of("https://picsum.photos/seed/" + p.getName().replaceAll("\\s+", "") + "/400/400");
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public Double getDiscountPrice() { return discountPrice; }
    public int getStock() { return stock; }
    public String getSku() { return sku; }
    public boolean isFeatured() { return featured; }
    public double getAverageRating() { return averageRating; }
    public int getReviewCount() { return reviewCount; }
    public BrandDTO getBrand() { return brand; }
    public List<CategoryDTO> getCategories() { return categories; }
    public List<String> getImages() { return images; }
}
