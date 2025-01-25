package com.ctzaf.dreamshops.service.category;

import com.ctzaf.dreamshops.exceptions.AlreadyExistsException;
import com.ctzaf.dreamshops.exceptions.ProductNotFoundException;
import com.ctzaf.dreamshops.exceptions.ResourceNotFoundException;
import com.ctzaf.dreamshops.model.Category;
import com.ctzaf.dreamshops.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return the category with the specified ID
     * @throws ResourceNotFoundException if no category is found with the given ID
     */
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    /**
     * Retrieves a category by its name.
     *
     * @param name the name of the category to retrieve
     * @return the category with the specified name
     * @throws ResourceNotFoundException if no category is found with the given name
     */
    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Adds a new category to the repository.
     *
     * @param category the category to add
     * @return the added category
     * @throws AlreadyExistsException if a category with the same name already exists
     */
    @Override
    public Category addCategory(Category category) {
            return Optional.of(category).filter(c -> !categoryRepository.existsByName(c.getName()))
                    .map(categoryRepository::save)
                    .orElseThrow(() -> new AlreadyExistsException(category.getName()+" already exists"));
    }

    /**
     * Updates an existing category with new information.
     *
     * @param category the new category information to update
     * @param id the ID of the category to update
     * @return the updated category
     * @throws ResourceNotFoundException if no category is found with the given ID
     */
    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id)).map(oldCategory -> {
            oldCategory.setName(category.getName());
            return categoryRepository.save(oldCategory);
        }).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    /**
     * Retrieves all categories from the repository.
     *
     * @return a list of all categories
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

/**
 * Deletes a category by its ID.
 *
 * @param id the ID of the category to delete
 * @throws ResourceNotFoundException if no category is found with the given ID
 */
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete,
                        () -> {
                    throw new ResourceNotFoundException("Category not found");
                });
    }
}
