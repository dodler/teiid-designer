/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;

/**
 * PrimaryKeyAspect
 *
 * @since 8.0
 */
public class PrimaryKeyAspect extends UniqueKeyAspect {
    
    /**
     * Construct an instance of PrimaryKeyAspect.
     * @param entity
     */
    public PrimaryKeyAspect(MetamodelEntity entity){
        super(entity);
    }    
}
