package com.hardware.store.repository.specification;

import com.hardware.store.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
@Builder
@Data
public class ProductSpecification implements Specification<Product> {

    private SearchCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        SearchOperation searchOperation = searchCriteria.getSearchOperation();
        String searchKey = searchCriteria.getKey();
        Object searchValue = searchCriteria.getValue();
        switch(searchOperation) {
            case EQUAL:
                return criteriaBuilder.equal(root.get(searchKey), searchValue);
            case LIKE:
                if(searchValue instanceof String) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get(searchKey)), "%" + ((String)searchValue).toLowerCase() + "%");
                }
        }
        throw new UnsupportedOperationException("Invalid search criteria specified! Please implement it if required");
    }
}
