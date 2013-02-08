/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact.teiid;

import org.komodo.repository.artifact.Artifact;

/**
 * A data source artifact.
 */
public interface SourceArtifact extends Artifact {

    /**
     * A relationship between a source artifact and its schema/model artifact.
     */
    public static final RelationshipType SCHEMA_RELATIONSHIP = new RelationshipType() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.RelationshipType#getId()
         */
        @Override
        public String getId() {
            return "SourceSchema"; //$NON-NLS-1$
        }

    };

    /**
     * The artifact type for a schema data source artifact.
     */
    public static final Type TYPE = new Type() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.Type#getId()
         */
        @Override
        public String getId() {
            return "TeiidSource"; //$NON-NLS-1$
        }

    };

}