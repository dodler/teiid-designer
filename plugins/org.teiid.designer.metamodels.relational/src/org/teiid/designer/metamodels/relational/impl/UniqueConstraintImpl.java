/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.UniqueConstraint;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Unique Constraint</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.impl.UniqueConstraintImpl#getTable <em>Table</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 *
 * @since 8.0
 */
public class UniqueConstraintImpl extends UniqueKeyImpl implements UniqueConstraint {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected UniqueConstraintImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getUniqueConstraint();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public BaseTable getTable() {
        if (eContainerFeatureID != RelationalPackage.UNIQUE_CONSTRAINT__TABLE) return null;
        return (BaseTable)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
	public void setTable(BaseTable newTable) {
        if (newTable != eContainer || (eContainerFeatureID != RelationalPackage.UNIQUE_CONSTRAINT__TABLE && newTable != null)) {
            if (EcoreUtil.isAncestor(this, newTable))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newTable != null)
                msgs = ((InternalEObject)newTable).eInverseAdd(this, RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS, BaseTable.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newTable, RelationalPackage.UNIQUE_CONSTRAINT__TABLE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.UNIQUE_CONSTRAINT__TABLE, newTable, newTable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationalPackage.UNIQUE_CONSTRAINT__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.UNIQUE_CONSTRAINT__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicAdd(otherEnd, msgs);
                case RelationalPackage.UNIQUE_CONSTRAINT__TABLE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.UNIQUE_CONSTRAINT__TABLE, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationalPackage.UNIQUE_CONSTRAINT__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.UNIQUE_CONSTRAINT__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicRemove(otherEnd, msgs);
                case RelationalPackage.UNIQUE_CONSTRAINT__TABLE:
                    return eBasicSetContainer(null, RelationalPackage.UNIQUE_CONSTRAINT__TABLE, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case RelationalPackage.UNIQUE_CONSTRAINT__TABLE:
                    return eContainer.eInverseRemove(this, RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS, BaseTable.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME:
                return getName();
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.UNIQUE_CONSTRAINT__COLUMNS:
                return getColumns();
            case RelationalPackage.UNIQUE_CONSTRAINT__FOREIGN_KEYS:
                return getForeignKeys();
            case RelationalPackage.UNIQUE_CONSTRAINT__TABLE:
                return getTable();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__FOREIGN_KEYS:
                getForeignKeys().clear();
                getForeignKeys().addAll((Collection)newValue);
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__TABLE:
                setTable((BaseTable)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__COLUMNS:
                getColumns().clear();
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__FOREIGN_KEYS:
                getForeignKeys().clear();
                return;
            case RelationalPackage.UNIQUE_CONSTRAINT__TABLE:
                setTable((BaseTable)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.UNIQUE_CONSTRAINT__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.UNIQUE_CONSTRAINT__COLUMNS:
                return columns != null && !columns.isEmpty();
            case RelationalPackage.UNIQUE_CONSTRAINT__FOREIGN_KEYS:
                return foreignKeys != null && !foreignKeys.isEmpty();
            case RelationalPackage.UNIQUE_CONSTRAINT__TABLE:
                return getTable() != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //UniqueConstraintImpl
