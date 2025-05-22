package com.devsuperior.dscatalog.mappers;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    public Product copyDtoToEntity (ProductDTO dto, Product product){
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setDate(dto.getDate());
        product.setImgUrl(dto.getImgUrl());
        product.setPrice(dto.getPrice());

        product.getCategories().clear();
        for(CategoryDTO categoryDTO : dto.getCategories()){
            Category category = categoryRepository.getReferenceById(categoryDTO.getId());
            product.getCategories().add(category);
        }
        return product;
    }
}
