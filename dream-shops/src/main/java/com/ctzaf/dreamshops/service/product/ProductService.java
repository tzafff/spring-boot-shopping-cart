package com.ctzaf.dreamshops.service.product;

import com.ctzaf.dreamshops.dto.ImageDto;
import com.ctzaf.dreamshops.dto.ProductDto;
import com.ctzaf.dreamshops.exceptions.ProductNotFoundException;
import com.ctzaf.dreamshops.exceptions.ResourceNotFoundException;
import com.ctzaf.dreamshops.model.Category;
import com.ctzaf.dreamshops.model.Image;
import com.ctzaf.dreamshops.model.Product;
import com.ctzaf.dreamshops.repository.CategoryRepository;
import com.ctzaf.dreamshops.repository.ImageRepository;
import com.ctzaf.dreamshops.repository.ProductRepository;
import com.ctzaf.dreamshops.request.AddProductRequest;
import com.ctzaf.dreamshops.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    /**
     * Add a new product. If the category of the product does not exist in the database
     * then it will be created.
     * @param request the request containing the product details
     * @return the saved product
     */
    @Override
    public Product addProduct(AddProductRequest request) {
        // Check if the category is found in the DB
        // If Yes, set it as the new product category.
        // If No, then save it as a new category.
        // Then set as the new product category.

        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    /**
     * Create a new product object given a request and a category.
     * @param request the request containing the product details
     * @param category the category of the product
     * @return the new product object
     */
    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    /**
     * Retrieves a product by id.
     * @param id the id of the product to be retrieved
     * @return the product with the given id
     * @throws ResourceNotFoundException if no product is found with the given id
     */
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    /**
     * Deletes a product by its id.
     * @param id the id of the product to be deleted
     * @throws ResourceNotFoundException if no product is found with the given id
     */
    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        () -> {throw new ResourceNotFoundException("Product not found");});
    }

    /**
     * Updates an existing product with new details provided in the ProductUpdateRequest.
     *
     * @param product the request containing the new product details
     * @param productId the id of the product to be updated
     * @return the updated product
     * @throws ProductNotFoundException if no product is found with the given id
     */
    @Override
    public Product updateProduct(ProductUpdateRequest product, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, product))
                .map(productRepository::save)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    /**
     * Updates an existing product with new details provided in the ProductUpdateRequest.
     *
     * @param existingProduct the existing product to be updated
     * @param request the request containing the new product details
     * @return the updated product
     */
    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);

        return existingProduct;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return a list of all products in the database
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves all products from a category.
     *
     * @param category the name of the category to retrieve products from
     * @return a list of all products in the given category
     */
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    /**
     * Retrieves all products from a specific brand.
     *
     * @param brand the name of the brand to retrieve products from
     * @return a list of all products in the given brand
     */
    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    /**
     * Retrieves all products from a category and brand.
     * @param category the name of the category to retrieve products from
     * @param brand the name of the brand to retrieve products from
     * @return a list of all products in the given category and brand
     */
    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    /**
     * Retrieves all products with the specified name.
     *
     * @param name the name of the products to retrieve
     * @return a list of products that match the given name
     */
    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * Retrieves all products from a specific brand and with a specific name.
     * @param brand the name of the brand to retrieve products from
     * @param name the name of the products to retrieve
     * @return a list of all products in the given brand with the given name
     */
    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    /**
     * Retrieves the count of products from a specific brand and with a specific name.
     * @param brand the name of the brand to retrieve products from
     * @param name the name of the products to retrieve
     * @return the count of all products in the given brand with the given name
     */
    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    /**
     * Converts a list of Product objects to a list of ProductDto objects.
     *
     * @param products the list of Product objects to be converted
     * @return a list of ProductDto objects converted from the given products
     */
    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    /**
     * Converts a Product object to a ProductDto object.
     * The conversion includes mapping the basic product fields and
     * retrieving associated images to be included in the ProductDto.
     *
     * @param product the Product object to be converted
     * @return the ProductDto object converted from the given Product
     */
    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImages(imageDtos);
        return productDto;
    }
}
