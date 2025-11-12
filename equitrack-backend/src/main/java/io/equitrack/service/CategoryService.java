package io.equitrack.service;

import io.equitrack.dto.CategoryDTO;
import io.equitrack.entity.CategoryEntity;
import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * BUSINESS LOGIC LAYER FOR CATEGORY MANAGEMENT
 *
 * This service handles all the rules and workflows for managing transaction categories
 * It sits between controllers (HTTP layer) and repositories (database layer)
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    // Dependency injection - gets current user context
    private final ProfileService profileService;

    // Dependency injection - database operations for categories
    private final CategoryRepository categoryRepository;

    /**
     * CREATE NEW CATEGORY FOR CURRENT USER
     * Business rules:
     * - Category names must be unique per user
     * - Automatically links category to current user
     */
    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        // Get current logged-in user
        ProfileEntity profile = profileService.getCurrentProfile();

        // Check if category name already exists for this user
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new RuntimeException("Category with this name already exist");
        }

        // Convert DTO to Entity and save
        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);

        // Return saved category as DTO
        return toDTO(newCategory);
    }

    /**
     * GET ALL CATEGORIES FOR CURRENT USER
     * Security: Automatically filters by current user
     */
    public List<CategoryDTO> getCategoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());

        // Convert all entities to DTOs for API response
        return categories.stream().map(this::toDTO).toList();
    }

    /**
     * GET CATEGORIES FILTERED BY TYPE (income/expense) FOR CURRENT USER
     */
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    /**
     * UPDATE EXISTING CATEGORY
     * Security: Verifies user owns the category before updating
     */
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO){
        ProfileEntity profile = profileService.getCurrentProfile();

        // Security check - only allow update if user owns this category
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible"));

        // Update fields
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());

        // Save changes
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }

    //--- HELPER METHODS: ENTITY-DTO CONVERSION ---

    /**
     * CONVERT DTO TO ENTITY - For saving to database
     */
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)          // Link to current user
                .type(categoryDTO.getType())
                .build();
    }

    /**
     * CONVERT ENTITY TO DTO - For API responses
     */
    private CategoryDTO toDTO(CategoryEntity entity){
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId(): null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();
    }
}