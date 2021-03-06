package org.teiid.designer.relational.ui.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.model.RelationalTable;
import org.teiid.designer.relational.ui.Messages;
import org.teiid.designer.relational.ui.UiConstants;
import org.teiid.designer.relational.ui.UiPlugin;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;
import org.teiid.designer.ui.common.UILabelUtil;
import org.teiid.designer.ui.common.UiLabelConstants;
import org.teiid.designer.ui.common.eventsupport.IDialogStatusListener;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.ClosedProjectFilter;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelWorkspaceDialog;
import org.teiid.designer.ui.viewsupport.SingleProjectFilter;

public class RelationalViewEditorPanel  extends RelationalEditorPanel implements RelationalConstants {	
	private List<String> MULTIPLICITY_LIST;

	private TabItem generalPropertiesTab;
	private TabItem columnsTab;
	
	// table property widgets
	private Button materializedCB, supportsUpdateCB, isSystemTableCB;
	private Button findTableReferenceButton;
	private Label materializedTableLabel;
	private Text cardinalityText, materializedTableText;
	
	// column widgets
	private Button addColumnButton, editColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
	private TableViewerBuilder columnsViewer;

	private boolean validationPerformed = false;

	/**
	 * @param parent the parent panel
	 * @param dialogModel dialog model
	 * @param statusListener the status listener
	 */
	public RelationalViewEditorPanel(Composite parent, RelationalDialogModel dialogModel, IDialogStatusListener statusListener) {
		super(parent, dialogModel, statusListener);
		MULTIPLICITY_LIST = new ArrayList<String>();
		for( String str : MULTIPLICITY.AS_ARRAY ) {
			MULTIPLICITY_LIST.add(str);
		}
	}

	@Override
	protected RelationalTable getRelationalReference() {
	    return (RelationalTable) super.getRelationalReference();
	}

	@Override
	protected void createPanel(Composite parent) {
		createNameGroup(parent);

		TabFolder tabFolder = createTabFolder(parent);
		createGeneralPropertiesTab(tabFolder);
		createColumnsTab(tabFolder);
	}

	private void createGeneralPropertiesTab(TabFolder folderParent) {
        // build the SELECT tab
		Composite thePanel = createPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.PROPERTIES));
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	
	private void createColumnsTab(TabFolder folderParent) {
        Composite thePanel = createColumnTableGroup(folderParent);

        this.columnsTab = new TabItem(folderParent, SWT.NONE);
        this.columnsTab.setControl(thePanel);
        this.columnsTab.setText(Messages.columnsLabel);
        this.columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, ModelType.PHYSICAL, Status.OK_STATUS));
	}
	
	
	@Override
	protected void synchronizeExtendedUI() {

	    synchronizePropertiesTab();
		synchronizeColumnsTab();


	}

	/**
     * Properties Tab
     */
    private void synchronizePropertiesTab() {
        if (generalPropertiesTab == null)
            return;

		if( WidgetUtil.widgetValueChanged(this.cardinalityText, getRelationalReference().getCardinality()) ) {
			this.cardinalityText.setText(Integer.toString(getRelationalReference().getCardinality()));
		}

		if (this.materializedCB != null) {
            boolean isMaterialized = getRelationalReference().isMaterialized();
            if (WidgetUtil.widgetValueChanged(materializedCB, isMaterialized)) {
                this.materializedCB.setSelection(isMaterialized);
            }
            this.materializedTableText.setEnabled(isMaterialized);
            this.findTableReferenceButton.setEnabled(isMaterialized);

            if (WidgetUtil.widgetValueChanged(materializedCB, getRelationalReference().getSupportsUpdate())) {
                this.supportsUpdateCB.setSelection(getRelationalReference().getSupportsUpdate());
            }
		}

		if( WidgetUtil.widgetValueChanged(isSystemTableCB, getRelationalReference().isSystem())) {
			this.isSystemTableCB.setSelection(getRelationalReference().isSystem());
		}
		generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, getRelationalReference().getModelType(), Status.OK_STATUS));
    }

    private void synchronizeColumnsTab() {
        /*
		 * Columns Tab
		 */
    	this.columnsViewer.getTable().removeAll();
    	IStatus maxStatus = Status.OK_STATUS;
        for( RelationalColumn row : getRelationalReference().getColumns() ) {
        	if( row.getStatus().getSeverity() > maxStatus.getSeverity() ) {
        		maxStatus = row.getStatus();
        	}
        	this.columnsViewer.add(row);
        }
        columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, getRelationalReference().getModelType(), maxStatus));
    }

	private Composite createPropertiesPanel(Composite parent) {
		Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

		Composite cardinalityPanel = new Composite(thePanel, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(cardinalityPanel);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(cardinalityPanel);

        Label label = new Label(cardinalityPanel, SWT.NONE);
        label.setText(Messages.cardinalityLabel);
        
        this.cardinalityText =  new Text(cardinalityPanel, SWT.BORDER | SWT.SINGLE);
        this.cardinalityText.setData("cardinalityText");
        this.cardinalityText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.cardinalityText);
        this.cardinalityText.addModifyListener(new ModifyListener() {
    		@Override
			public void modifyText( final ModifyEvent event ) {
    			if( isSynchronizing() )
    			    return;

    			if( !cardinalityText.getText().isEmpty()) {
    			    try {	
    			        int value = Integer.parseInt(cardinalityText.getText());
    			        getRelationalReference().setCardinality(value);
    			        handleInfoChanged();
    			    } catch (NumberFormatException ex) {
    			        MessageDialog.openError(
    			                                getShell(), 
    			                                Messages.cardinalityErrorTitle, 
    			                                Messages.cardinalityMustBeAnInteger);
	        				    return;
    			    }
    			}
    		}
        });

        Composite checkButtonPanel = new Composite(thePanel, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(checkButtonPanel);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(checkButtonPanel);

        this.supportsUpdateCB = new Button(checkButtonPanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.supportsUpdateCB);
        this.supportsUpdateCB.setText(Messages.supportsUpdateLabel);
        this.supportsUpdateCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setSupportsUpdate(supportsUpdateCB.getSelection());
                handleInfoChanged();
            }
        });

        this.isSystemTableCB = new Button(checkButtonPanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.isSystemTableCB);
        this.isSystemTableCB.setText(Messages.systemTableLabel);
        this.isSystemTableCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
            	getRelationalReference().setSystem(isSystemTableCB.getSelection());
                handleInfoChanged();
            }
        });

        this.materializedCB = new Button(checkButtonPanel, SWT.CHECK | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).applyTo(this.materializedCB);
        this.materializedCB.setText(Messages.materializedLabel);
        this.materializedCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                getRelationalReference().setMaterialized(materializedCB.getSelection());
                if (!materializedCB.getSelection()) {
                    getRelationalReference().setMaterializedTable(null);
                }
                handleInfoChanged();
            }
        });

        Composite materializedPanel = new Composite(thePanel, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(materializedPanel);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(materializedPanel);

        materializedTableLabel = new Label(materializedPanel, SWT.NONE | SWT.RIGHT);
        materializedTableLabel.setText(Messages.tableReferenceLabel);
        GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(materializedTableLabel);

        this.materializedTableText = new Text(materializedPanel, SWT.BORDER | SWT.SINGLE);
        this.materializedTableText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.materializedTableText);
        this.materializedTableText.setEditable(false);

        this.findTableReferenceButton = new Button(materializedPanel, SWT.PUSH);
        this.findTableReferenceButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ELIPSIS));
        GridDataFactory.fillDefaults().hint(30, SWT.DEFAULT).applyTo(this.findTableReferenceButton);
        this.findTableReferenceButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	handleBrowseWorkspaceForMatTablePressed();
            }
        });

        createDescriptionPanel(thePanel);

        return thePanel;
	}

	private Composite createColumnTableGroup(Composite parent) {
		  	
	    Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(thePanel);

        Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 5);
        GridLayoutFactory.fillDefaults().numColumns(5).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

    	addColumnButton = new Button(buttonPanel, SWT.PUSH);
    	addColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.ADD));
    	GridDataFactory.fillDefaults().applyTo(addColumnButton);
    	addColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
	    		getRelationalReference().createColumn();
				handleInfoChanged();
				setColumnButtonsState();
			}
    		
		});
    	
    	editColumnButton = new Button(buttonPanel, SWT.PUSH);
    	editColumnButton.setText(Messages.Edit);
    	GridDataFactory.fillDefaults().applyTo(editColumnButton);
    	editColumnButton.setEnabled(false);
    	editColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn column = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						column =  (RelationalColumn) obj;
						break;
					}
				}
				if( column != null ) {
					EditColumnDialog dialog = new EditColumnDialog(getShell(), column);
					dialog.open();
					handleInfoChanged();
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	deleteColumnButton = new Button(buttonPanel, SWT.PUSH);
    	deleteColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.DELETE));
    	GridDataFactory.fillDefaults().applyTo(deleteColumnButton);
    	deleteColumnButton.setEnabled(false);
    	deleteColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn column = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						column =  (RelationalColumn) obj;
						break;
					}
				}
				if( column != null ) {
					getRelationalReference().removeColumn(column);
					handleInfoChanged();
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	upColumnButton = new Button(buttonPanel, SWT.PUSH);
    	upColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_UP));
    	GridDataFactory.fillDefaults().applyTo(upColumnButton);
    	upColumnButton.setEnabled(false);
    	upColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn info = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						info =  (RelationalColumn) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = columnsViewer.getTable().getSelectionIndex();
					getRelationalReference().moveColumnUp(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex-1);
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	downColumnButton = new Button(buttonPanel, SWT.PUSH);
    	downColumnButton.setText(UILabelUtil.getLabel(UiLabelConstants.LABEL_IDS.MOVE_DOWN));
    	GridDataFactory.fillDefaults().applyTo(downColumnButton);
    	downColumnButton.setEnabled(false);
    	downColumnButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationalColumn info = null;
				
				IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
				for( Object obj : selection.toArray()) {
					if( obj instanceof RelationalColumn ) {
						info =  (RelationalColumn) obj;
						break;
					}
				}
				if( info != null ) {
					int selectedIndex = columnsViewer.getTable().getSelectionIndex();
					getRelationalReference().moveColumnDown(info);
					handleInfoChanged();
					columnsViewer.getTable().select(selectedIndex+1);
				}
				setColumnButtonsState();
			}
    		
		});
    	
    	this.columnsViewer = new TableViewerBuilder(thePanel, (SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER));
    	this.columnsViewer.getTable().setData("view columns viewer table");

        // create columns
        TableViewerColumn column = this.columnsViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(Messages.columnNameLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(0));

        column = this.columnsViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(Messages.dataTypeLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        
        column = this.columnsViewer.createColumn(SWT.LEFT, 30, 40, true);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(2));

        if( getRelationalReference() != null ) {
	        for( RelationalColumn row : getRelationalReference().getColumns() ) {
	        	this.columnsViewer.add(row);
	        }
        }
        
        this.columnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setColumnButtonsState();
			}
		});
        
        this.columnsViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				Object[] objs = sel.toArray();
				if( objs.length == 1 && objs[0] instanceof RelationalColumn) {
					EditColumnDialog dialog = new EditColumnDialog(getShell(), (RelationalColumn)objs[0]);
					dialog.open();
					handleInfoChanged();
				}
			}
		});
        
        return thePanel;
    }
	
	private void setColumnButtonsState() {
		IStructuredSelection selection = (IStructuredSelection)this.columnsViewer.getSelection();
		boolean enable = selection != null && !selection.isEmpty();
		deleteColumnButton.setEnabled(enable);
		editColumnButton.setEnabled(enable);
		if( enable ) {
			Object[] objs = selection.toArray();
			RelationalColumn columnInfo = (RelationalColumn)objs[0];
			upColumnButton.setEnabled(getRelationalReference().canMoveColumnUp(columnInfo));
			downColumnButton.setEnabled(getRelationalReference().canMoveColumnDown(columnInfo));
		} else {
			upColumnButton.setEnabled(false);
			downColumnButton.setEnabled(false);
		}
	}

	@Override
	protected void validate() {
		getRelationalReference().validate();
		
		setCanFinish(getRelationalReference().nameIsValid());
		
		IStatus currentStatus = getRelationalReference().getStatus();
		if( currentStatus.isOK() ) {
			setStatus(Status.OK_STATUS);
		} else {
			setStatus(currentStatus);
		}
		validationPerformed = true;
	}
	
	@Override
	public boolean canFinish() {
		// check if procedure name is not-null
		if( !validationPerformed && this.getRelationalReference().getName() != null ) {
			return true;
		} else if( validationPerformed) {
			return this.getRelationalReference().getName() != null;
		}
		
		return false;
	}
	
	private void handleBrowseWorkspaceForMatTablePressed() {
		ModelWorkspaceDialog sdDialog = createTableOrViewSelector();

		// add filters
		sdDialog.addFilter(new ClosedProjectFilter());
		Properties props = new Properties();
		DesignerPropertiesUtil.setProjectName(props, getModelFile().getProject().getName());
		sdDialog.addFilter(new SingleProjectFilter(props));
		
		sdDialog.open();

        if (sdDialog.getReturnCode() == Window.OK) {
            Object[] selections = sdDialog.getResult();
            // should be single selection
            EObject matTableOrView = (EObject)selections[0];
            String name = ModelerCore.getModelEditor().getName(matTableOrView);
            this.materializedTableText.setText(name);
            getRelationalReference().setMaterializedTable(name);
            ModelResource mr = ModelUtilities.getModelResource(matTableOrView);
            try {
				getRelationalReference().setMaterializedTableModelPath(mr.getCorrespondingResource().getFullPath().toString());
			} catch (ModelWorkspaceException e) {
				e.printStackTrace();
			}
            validate();
        }

	}
    
	private ModelWorkspaceDialog createTableOrViewSelector() {
		
		ModelWorkspaceDialog result = new ModelWorkspaceDialog(getShell(), null,
				new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());

		String title = "Select referenced table or view"; //$NON-NLS-1$
		String message = "Select referenced materialized table or view"; //$NON-NLS-1$
		result.setTitle(title);
		result.setMessage(message);
		result.setAllowMultiple(false);

		result.setInput(ModelerCore.getWorkspace().getRoot());

		result.setValidator(new ISelectionStatusValidator() {
			@Override
			public IStatus validate(Object[] selection) {
				if (selection == null || selection.length == 0
						|| selection[0] == null
						|| (!(selection[0] instanceof Table) && !(selection[0] instanceof View)) ) {
					String msg = "Selection is not a table or view"; //$NON-NLS-1$
					return new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR,msg);
				}
				return new StatusInfo(UiConstants.PLUGIN_ID);
			}
		});


		return result;
	}

	class ColumnDataLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public ColumnDataLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof RelationalColumn ) {
				switch (this.columnNumber) {
					case 0: {
						return ((RelationalColumn)element).getName();
					}
					case 1: {
						return ((RelationalColumn)element).getDatatype();
					}
					case 2: {
						return Integer.toString(((RelationalColumn)element).getLength());
					}
				}
			}
			return EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			switch (this.columnNumber) {
			case 0: {
				return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
			}
			case 1: {
				return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
			}
		}
		return "unknown tooltip"; //$NON-NLS-1$
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == 0 ) {
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			}
			return null;
		}
		
	}
}
