package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dtos.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.mappers.ProductMapper;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductRepository repository;
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
        Page<Product> products =  repository.findAll(pageRequest);
        return products.map(ProductDTO::new);

    }
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> optionalProduct = repository.findById(id);
        Product product = optionalProduct.orElseThrow(() -> new ResourceNotFoundException("Entidade Não encontrada"));
        return new ProductDTO(product,product.getCategories());
    }
    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product product = productMapper.copyDtoToEntity(dto,new Product());
        product = repository.save(product);
        return  new ProductDTO(product);
    }
    @Transactional
    public ProductDTO update(Long id,ProductDTO dto) {
        try {
            Product product = repository.getReferenceById(id);
            productMapper.copyDtoToEntity(dto,product);
            repository.save(product);
            return new ProductDTO(product);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id not found");
        }
        }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Recurso Nao Encontrado");
        }
        try{
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException(""" 
                    Falha na integridade referencial,
                    existem dados que referenciam este recurso
                    delete os antes de prosseguir
                    """
                            );
        }
//        Product product = repository.getReferenceById(id);
    }
}
