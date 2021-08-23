package com.hardware.store.repository.specification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchCriteria {

    private String key;

    private SearchOperation searchOperation;

    private Object value;
}
