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
 * An import VDB artifact.
 */
public interface EntryArtifact extends Artifact {

    /**
     * The artifact type for a import VDB artifact.
     */
    public static final Type TYPE = new Type() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.Type#getId()
         */
        @Override
        public String getId() {
            return "TeiidVdbEntry"; //$NON-NLS-1$
        }

    };

}