/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IPath;

/**
 * This QualifiedNameRequirementVisitor class is used to determine whether the selected nodes under a {@link JdbcDatabase} require
 * at least some of the namespaces to exist in a model, or whether it is possible to remove schemas and/or catalogs from a model.
 * <p>
 * The current policy is that all database objects (regardless type) have to be uniquely identifiable by a (short or
 * fully-qualified) name.
 * </p>
 *
 * @since 8.0
 */
public class QualifiedNameRequirementVisitor implements JdbcNodeVisitor {

    private final boolean includeCatalogs;
    private final boolean includeSchemas;
    private final Map jdbcNodesWithSamePathByPath;
    private final Set pathsWithClashes;

    /**
     * Construct an instance of QualifiedNameCheckVisitor.
     */
    public QualifiedNameRequirementVisitor( final boolean includeCatalogs,
                                            final boolean includeSchemas ) {
        super();
        this.includeCatalogs = includeCatalogs;
        this.includeSchemas = includeSchemas;
        this.jdbcNodesWithSamePathByPath = new HashMap();
        this.pathsWithClashes = new HashSet();
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.JdbcNodeVisitor#visit(org.teiid.designer.jdbc.metadata.JdbcNode)
     */
    @Override
	public boolean visit( final JdbcNode node ) {
        if (node == null || node.getSelectionMode() == JdbcNode.UNSELECTED) {
            return false; // no need to process further ...
        }

        final IPath pathInModel = node.getPathInSource(this.includeCatalogs, this.includeSchemas);
        if (pathInModel == null) {
            return true; // nothing to do with this node, but evaluate children
        }

        // There is a path in the model, so see if something has used it already ...
        List jdbcNodesWithSamePath = (List)jdbcNodesWithSamePathByPath.get(pathInModel);
        if (jdbcNodesWithSamePath == null) {
            jdbcNodesWithSamePath = new ArrayList();
            this.jdbcNodesWithSamePathByPath.put(pathInModel, jdbcNodesWithSamePath);
        }
        // Put the object in the list
        jdbcNodesWithSamePath.add(node);

        // If there is more than one node with the same path, mark it
        if (jdbcNodesWithSamePath.size() > 1) {
            this.pathsWithClashes.add(pathInModel);
        }
        return false;
    }

    /**
     * Return the {@link IPath} instances that reflect more than one model object.
     * 
     * @return the list of IPath instances that are ambiguous; never null
     */
    public Collection getAmbiguousPaths() {
        return this.pathsWithClashes;
    }

    /**
     * Return the list of {@link JdbcNode nodes} that would have the same path within the model.
     * 
     * @param path the path
     * @return the list of JdbcNode instances; never null, but possibly empty
     */
    public List getNodesWithPath( final IPath path ) {
        final List jdbcNodesWithSamePath = (List)jdbcNodesWithSamePathByPath.get(path);
        return (jdbcNodesWithSamePath != null ? new ArrayList(jdbcNodesWithSamePath) : new ArrayList());
    }

}
