package com.example.forfinal.service;

import com.example.forfinal.dto.ProductResponse;
import com.example.forfinal.entity.Brand;
import com.example.forfinal.entity.Category;
import com.example.forfinal.entity.Product;
import com.example.forfinal.entity.ProductImage;
import com.example.forfinal.repository.BrandRepository;
import com.example.forfinal.repository.CategoryRepository;
import com.example.forfinal.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Transactional
    public ProductResponse addProduct(Product product) {
        if (product.getBrand() != null && product.getBrand().getId() != null) {
            Brand brand = brandRepository.findById(product.getBrand().getId()).orElse(null);
            product.setBrand(brand);
        }
        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
            List<Category> managedCategories = new ArrayList<>();
            for (Category cat : product.getCategories()) {
                if (cat.getId() != null) {
                    categoryRepository.findById(cat.getId()).ifPresent(managedCategories::add);
                }
            }
            product.setCategories(managedCategories);
        }
        return new ProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setDiscountPrice(productDetails.getDiscountPrice());
        product.setStock(productDetails.getStock());
        product.setSku(productDetails.getSku());
        product.setFeatured(productDetails.isFeatured());

        if (productDetails.getBrand() != null && productDetails.getBrand().getId() != null) {
            Brand brand = brandRepository.findById(productDetails.getBrand().getId()).orElse(null);
            product.setBrand(brand);
        }

        if (productDetails.getCategories() != null && !productDetails.getCategories().isEmpty()) {
            List<Category> managedCategories = new ArrayList<>();
            for (Category cat : productDetails.getCategories()) {
                if (cat.getId() != null) {
                    categoryRepository.findById(cat.getId()).ifPresent(managedCategories::add);
                }
            }
            if (!managedCategories.isEmpty()) {
                product.setCategories(managedCategories);
            }
        }

        return new ProductResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(ProductResponse::new).collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        return new ProductResponse(productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    public Page<ProductResponse> getProducts(int page, int size, String sortBy) {
        return productRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy)))
                .map(ProductResponse::new);
    }

    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ProductResponse::new).collect(Collectors.toList());
    }

    public Page<ProductResponse> filterProducts(String keyword, Long categoryId, Long brandId,
                                                  Double minPrice, Double maxPrice, Double minRating,
                                                  int page, int size, String sortBy) {
        Sort sort = Sort.by("id");
        if (sortBy.equals("price_asc")) sort = Sort.by("price").ascending();
        else if (sortBy.equals("price_desc")) sort = Sort.by("price").descending();
        else if (sortBy.equals("rating")) sort = Sort.by("averageRating").descending();
        else if (sortBy.equals("newest")) sort = Sort.by("createdAt").descending();
        return productRepository.filterProducts(keyword, categoryId, brandId, minPrice, maxPrice, minRating,
                PageRequest.of(page, size, sort)).map(ProductResponse::new);
    }

    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue(PageRequest.of(0, 8)).stream()
                .map(ProductResponse::new).collect(Collectors.toList());
    }

    public List<ProductResponse> getTopRated() {
        return productRepository.findTop10ByOrderByAverageRatingDesc(PageRequest.of(0, 8)).stream()
                .map(ProductResponse::new).collect(Collectors.toList());
    }

    public List<ProductResponse> getNewArrivals() {
        return productRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(ProductResponse::new).collect(Collectors.toList());
    }

    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategoriesId(categoryId, PageRequest.of(0, 20)).stream()
                .map(ProductResponse::new).collect(Collectors.toList());
    }

    public ProductResponse addImageToProduct(Long productId, String imageUrl, boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductImage image = new ProductImage(imageUrl, isPrimary);
        image.setProduct(product);
        product.getImages().add(image);
        return new ProductResponse(productRepository.save(product));
    }
}
