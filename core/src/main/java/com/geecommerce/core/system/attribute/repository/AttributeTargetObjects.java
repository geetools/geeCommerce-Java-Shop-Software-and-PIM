package com.geecommerce.core.system.attribute.repository;

import com.geecommerce.core.service.AttributeSupport;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;

public interface AttributeTargetObjects extends Repository {
    AttributeTargetObject havingCode(String targetObjectCode);

    AttributeTargetObject forType(Class<? extends AttributeSupport> modelInterfaceFQN);
}
