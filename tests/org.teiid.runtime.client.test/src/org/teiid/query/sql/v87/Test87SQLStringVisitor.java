/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v87;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.v86.Test86SQLStringVisitor;

/**
 *
 */
@SuppressWarnings( {"javadoc"} )
public class Test87SQLStringVisitor extends Test86SQLStringVisitor {

    protected Test87SQLStringVisitor(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test87SQLStringVisitor() {
        this(Version.TEIID_8_7);
    }

}
