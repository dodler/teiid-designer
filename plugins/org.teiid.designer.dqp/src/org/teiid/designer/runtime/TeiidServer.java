/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;
import static org.teiid.designer.runtime.DqpPlugin.Util;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.connection.IPasswordProvider;
import org.teiid.designer.runtime.impl.ExecutionAdmin;
import org.teiid.designer.vdb.Vdb;


/**
 *
 *
 * @since 8.0
 */
public class TeiidServer implements HostProvider, IExecutionAdmin {

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    /**
     * @param thisObj an object being compared (may be <code>null</code>)
     * @param thatObj the other object being compared (may be <code>null</code>)
     * @return <code>true</code> if both objects are <code>null</code> or both are not <code>null</code> and equal
     */
    private static boolean equivalent( Object thisObj,
                                       Object thatObj ) {
        // true if both objects are null
        if (thisObj == null) {
            return (thatObj == null);
        }

        if (thatObj == null) return false;
        return thisObj.equals(thatObj);
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    protected IExecutionAdmin admin;

    /**
     * The object that will fire the events.
     */
    protected final EventManager eventManager;

    /**
     * The Teiid JDBC connection info object
     */
    private TeiidJdbcInfo teiidJdbcInfo;

    /**
     * The Teiid Admin connection info object
     */
    private TeiidAdminInfo teiidAdminInfo;

    private String connectionError;

    /**
     * An optional property that can be used for display purposes. May be <code>null</code>.
     */
    private String customLabel;

    /**
     * The host this server connects to (never empty or <code>null</code>).
     */
    private String host;

    /**
     * The parent {@link IServer} of this teiid server
     */
    private IServer parentServer;

    /**
     * Internal flag to ensure {@link #notifyRefresh()} does not
     * send any signals to listeners. Should always be called in pairs,
     * ie. turn off -> do work -> turn on
     */
    private boolean notifyListeners = true;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs on new <code>Server</code>.
     * 
     * @param host the server host (<code>null</code> or empty if default host should be used)
     * @param adminInfo the server admin connection properties (never <code>null</code>)
     * @param jdbcInfo the server JDBC connection properties (never <code>null</code>)
     * @param eventManager the event manager (never <code>null</code>)
     * @param parentServer the parent {@link IServer} (never <code>null</code>)
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    public TeiidServer( String host,
                   TeiidAdminInfo adminInfo,
                   TeiidJdbcInfo jdbcInfo,
                   EventManager eventManager,
                   IServer parentServer) {
        CoreArgCheck.isNotNull(adminInfo, "adminInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(jdbcInfo, "jdbcInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(eventManager, "eventManager"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(parentServer, "parentServer"); //$NON-NLS-1$

        this.host = host;
        
        this.teiidAdminInfo = adminInfo;
        this.teiidAdminInfo.setHostProvider(this);
        
        this.teiidJdbcInfo = jdbcInfo;
        this.teiidJdbcInfo.setHostProvider(this);
        
        this.eventManager = eventManager;
        this.parentServer = parentServer;
        
        if (parentServer.getServerState() != IServer.STATE_STARTED)
            disconnect();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    @Override
    public void disconnect() {
        if (this.admin != null) {
            this.admin.disconnect();
            this.admin = null;
        }
        
        notifyRefresh();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TeiidServer other = (TeiidServer)obj;
        if (this.admin == null) {
            if (other.admin != null) return false;
        } else if (!this.admin.equals(other.admin)) return false;
        if (this.eventManager == null) {
            if (other.eventManager != null) return false;
        } else if (!this.eventManager.equals(other.eventManager)) return false;
        if (this.host == null) {
            if (other.host != null) return false;
        } else if (!this.host.equals(other.host)) return false;
        if (this.parentServer == null) {
            if (other.parentServer != null) return false;
        } else if (!this.parentServer.equals(other.parentServer)) return false;
        if (this.teiidAdminInfo == null) {
            if (other.teiidAdminInfo != null) return false;
        } else if (!this.teiidAdminInfo.equals(other.teiidAdminInfo)) return false;
        if (this.teiidJdbcInfo == null) {
            if (other.teiidJdbcInfo != null) return false;
        } else if (!this.teiidJdbcInfo.equals(other.teiidJdbcInfo)) return false;
        return true;
    }

    /**
     * Connect to this {@link TeiidServer}
     * 
     * @throws Exception
     */
    public void connect() throws Exception {
        if (! isParentConnected()) {
            throw new Exception(DqpPlugin.Util.getString("jbossServerNotStartedMessage")); //$NON-NLS-1$
        }
        
        if (this.admin == null) {
            
            this.admin = new ExecutionAdmin(this);
            this.admin.connect();
            notifyRefresh();
        }
    }
    
    /**
     * Disconnect then connect to this server. This is preferable to 
     * calling {@link #disconnect()} and {@link #connect()} separately
     * since it only notifies at the end of the reconnection.
     */
    public void reconnect() {
        try {
            // Call disconnect() first to clear out Server & admin caches
            notifyListeners = false;
            disconnect();
            notifyListeners = true;
            
            if (isParentConnected()) {
                // Refresh is implied in the getting of the admin object since it will
                // automatically load and refresh.
                connect();
            }
            
            setConnectionError(null);
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
            String msg = DqpPlugin.Util.getString("serverReconnectErrorMsg", this); //$NON-NLS-1$
            setConnectionError(msg);
        }
    }

    public TeiidAdminInfo getTeiidAdminInfo() {
        return teiidAdminInfo;
    }

    public TeiidJdbcInfo getTeiidJdbcInfo() {
        return teiidJdbcInfo;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    /**
     * An appropriate name for this teiid server
     * 
     * @return {@link #getCustomLabel()} if available otherwise {@link #getUrl()}
     */
    public String getDisplayName() {
        return getCustomLabel() != null ? getCustomLabel() : getUrl();
    }

    /**
     * @return the host URL (never <code>null</code>)
     */
    public String getUrl() {
        return getTeiidAdminInfo().getUrl();
    }

    /**
     * @return the custom label or <code>null</code> if not being used
     */
    public String getCustomLabel() {
        return this.customLabel;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.HostProvider#getHost()
     */
    @Override
    public String getHost() {
        if (this.host == null) {
            return HostProvider.DEFAULT_HOST;
        }

        return this.host;
    }
    
    /**
     * @return the parentServer
     */
    public IServer getParent() {
        return this.parentServer;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.admin == null) ? 0 : this.admin.hashCode());
        result = prime * result + ((this.eventManager == null) ? 0 : this.eventManager.hashCode());
        result = prime * result + ((this.host == null) ? 0 : this.host.hashCode());
        result = prime * result + ((this.parentServer == null) ? 0 : this.parentServer.hashCode());
        result = prime * result + ((this.teiidAdminInfo == null) ? 0 : this.teiidAdminInfo.hashCode());
        result = prime * result + ((this.teiidJdbcInfo == null) ? 0 : this.teiidJdbcInfo.hashCode());
        return result;
    }

    /**
     * A server has the same identifying properties if their URL and user matches.
     * 
     * @param otherServer the server whose key is being compared (never <code>null</code>)
     * @return <code>true</code> if the servers have the same key
     * @throws IllegalArgumentException if the argument is <code>null</code>
     */
    public boolean hasSameKey( TeiidServer otherServer ) {
        CoreArgCheck.isNotNull(otherServer, "otherServer"); //$NON-NLS-1$
        return (equivalent(getUrl(), otherServer.getUrl()) && equivalent(getTeiidAdminInfo().getUsername(),
                                                                         otherServer.getTeiidAdminInfo().getUsername()));
    }

    /**
     * @return <code>true</code> if a connection to this server exists and is working
     */
    public boolean isConnected() {
        if (! isParentConnected() && this.admin == null) {
            return false;
        }
        return ping().isOK();
    }

    /**
     * Return whether parent server is connected.
     * 
     * @return true is started, otherwise false
     */
    public boolean isParentConnected() {
        return this.parentServer != null && this.parentServer.getServerState() == IServer.STATE_STARTED;
    }
    
    /**
     * Attempts to establish communication with the specified server.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    public IStatus ping() {
        String msg = Util.getString("cannotConnectToServer", getTeiidAdminInfo().getUsername()); //$NON-NLS-1$
        
        try {
            if (! isParentConnected() && this.admin == null)
                throw new Exception(msg);
            
            admin.ping(PingType.ADMIN);
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }

        return Status.OK_STATUS;
    }

    public void notifyRefresh() {
        if (! notifyListeners)
            return;
        
        if (this.admin != null) {
            getEventManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
        } else {
            DqpPlugin.getInstance().getServerManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
        }
    }

    public String getConnectionError() {
        return connectionError;
    }

    public void setConnectionError( String connectionError ) {
        this.connectionError = connectionError;
    }

    /**
     * @param customLabel the new custom label or <code>null</code> or empty if the custom label is not being used
     */
    public void setCustomLabel( String customLabel ) {
        this.customLabel = StringUtilities.isEmpty(customLabel) ? null : customLabel;
    }

    /**
     * Attempts to establish communication with the specified server for testing purposes only.
     * 
     * This results in the connection being closed.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    public IStatus testPing() {
        try {
            connect();
            ping();
            disconnect();
            this.admin = null;
        } catch (Exception e) {
            String msg = Util.getString("cannotConnectToServer", this); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }

        return Status.OK_STATUS;
    }
    
    /**
     * Test the jdbc connection
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * 
     * @return status as to the ping's success
     */
    public IStatus testJDBCPing(String host, String port, String username, String password) {
        try {
            connect();
            admin.ping(PingType.JDBC);
            disconnect();
            this.admin = null;
        } catch (Exception e) {
            String msg = Util.getString("cannotConnectToServer", this); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }

        return Status.OK_STATUS;
    }
    
    public String getVdbDataSourceConnectionUrl(String vdbName) {
    	String host = this.teiidAdminInfo.getHostProvider().getHost();
		String port = this.teiidAdminInfo.getPort();
		return "jdbc:teiid:" + vdbName + "@mm://"+host+':'+port;  //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public IStatus createVdbDataSource(String vdbName, String displayName, String jndiName) {
    	Properties props = new Properties();
		String username = this.teiidJdbcInfo.getUsername();
		String password = this.teiidJdbcInfo.getPassword();
		if( username != null ) {
			props.put("user-name", username); //$NON-NLS-1$
		}
		if( password != null ) {
			props.put("password", password); //$NON-NLS-1$
		}
		
		props.put("driver-class", "org.teiid.jdbc.TeiidDriver"); //$NON-NLS-1$ //$NON-NLS-2$
		props.put("connection-url", getVdbDataSourceConnectionUrl(vdbName)); //$NON-NLS-1$
    	
    	try {
			connect();
			admin.getOrCreateDataSource(displayName, jndiName, "connector-jdbc", props); //$NON-NLS-1$
		} catch (Exception ex) {
			String msg = "Error creating data source for VDB " + vdbName; //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
		}
		
		return Status.OK_STATUS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String txt = Util.getString("serverToStringWithNoCustomLabel", getUrl(), getTeiidAdminInfo().getUsername()); //$NON-NLS-1$
        
        if (this.customLabel != null) {
            txt = Util.getString("serverToStringWithCustomLabel", this.customLabel, txt); //$NON-NLS-1$
        }

        return txt;
    }
    
    @Override
    public boolean dataSourceExists(String name) throws Exception {
        connect();
        return admin.dataSourceExists(name);
    }

    @Override
    public void deleteDataSource(String jndiName) throws Exception {
        connect();
        admin.deleteDataSource(jndiName);
    }

    @Override
    public void deployVdb(IFile vdbFile) throws Exception {
        connect();
        admin.deployVdb(vdbFile);
    }

    @Override
    public void deployVdb(Vdb vdb) throws Exception {
        connect();
        admin.deployVdb(vdb);
    }

    @Override
    public TeiidDataSource getDataSource(String name) throws Exception {
        connect();
        return admin.getDataSource(name);
    }

    @Override
    public Collection<TeiidDataSource> getDataSources() throws Exception {
        connect();
        return admin.getDataSources();
    }

    @Override
    public Set<String> getDataSourceTypeNames() throws Exception {
        connect();
        return admin.getDataSourceTypeNames();
    }

    @Override
    public TeiidDataSource getOrCreateDataSource(IFile model,
                                                 String jndiName,
                                                 boolean previewVdb,
                                                 IPasswordProvider passwordProvider) throws Exception {

        connect();
        return admin.getOrCreateDataSource(model, jndiName, previewVdb, passwordProvider);
    }

    @Override
    public TeiidDataSource getOrCreateDataSource(String displayName,
                                                 String jndiName,
                                                 String typeName,
                                                 Properties properties) throws Exception {
        connect();
        return admin.getOrCreateDataSource(displayName, jndiName, typeName, properties);
    }

    @Override
    public ITeiidTranslator getTranslator(String name) throws Exception {
        connect();
        return admin.getTranslator(name);
    }

    @Override
    public Collection<ITeiidTranslator> getTranslators() throws Exception {
        connect();
        return admin.getTranslators();
    }

    @Override
    public Collection<ITeiidVdb> getVdbs() throws Exception {
        connect();
        return admin.getVdbs();
    }
    
    @Override
    public ITeiidVdb getVdb(String name) throws Exception {
        connect();
        return admin.getVdb(name);
    }
    
    @Override
    public boolean hasVdb(String name) throws Exception {
        connect();
        return admin.hasVdb(name);
    }

    @Override
    public boolean isVdbActive(String vdbName) throws Exception {
        connect();
        return admin.isVdbActive(vdbName);
    }

    @Override
    public boolean isVdbLoading(String vdbName) throws Exception {
        connect();
        return admin.isVdbLoading(vdbName);
    }

    @Override
    public boolean hasVdbFailed(String vdbName) throws Exception {
        connect();
        return admin.hasVdbFailed(vdbName);
    }

    @Override
    public boolean wasVdbRemoved(String vdbName) throws Exception {
        connect();
        return admin.wasVdbRemoved(vdbName);
    }
    
    @Override
    public List<String> retrieveVdbValidityErrors(String vdbName) throws Exception {
        connect();
        return admin.retrieveVdbValidityErrors(vdbName);
    }

    @Override
    public void undeployVdb(String vdbName) throws Exception {
        connect();
        admin.undeployVdb(vdbName);
    }

    @Override
    public IStatus ping(PingType pingType) throws Exception {
        connect();
        return admin.ping(pingType);
    }
    
    @Override
    public String getAdminDriverPath() throws Exception {
        connect();
        return admin.getAdminDriverPath();
    }
}
