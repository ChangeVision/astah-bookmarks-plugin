package astah.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;

public class BookmarkTabView extends JPanel implements IPluginExtraTabView, ProjectEventListener,
        ActionListener, IBookmarkView {

    private static final long serialVersionUID = 4238629225058482667L;

    private static final int COLUMN_DESCRIPTION = 0;
    private static final int COLUMN_DIAGRAM_NAME = 1;
    private static final int COLUMN_PRESENTATION_NAME = 2;
    private static final int COLUMN_MODEL = 3;
    private static final int ROW_HEIGHT = 22;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scroller;
    private JButton buttonAddBookmarks;
    private JButton buttonRemoveBookmarks;
    private JButton buttonRefreshTable;
    private JButton buttonSwapUpBookmark;
    private JButton buttonSwapDownBookmark;

    /*
     * Constructor
     */
    public BookmarkTabView() {
        initComponents();
        addProjectEventListener();
    }

    /*
     * Member　Function
     */
    private void initComponents() {
        initTable();

        initButton();

        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout());

        add(this.scroller, BorderLayout.CENTER);

        {
            JToolBar bar = new JToolBar();
            bar.add(buttonRefreshTable);
            bar.add(buttonAddBookmarks);
            bar.add(buttonRemoveBookmarks);

            add(bar, BorderLayout.NORTH);
        }

        {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1));
            panel.add(buttonSwapUpBookmark);
            panel.add(buttonSwapDownBookmark);

            add(panel, BorderLayout.WEST);
        }
    }

    private void initButton() {

        buttonAddBookmarks = new JButton();
        buttonAddBookmarks.setIcon(getIcon("images/tag_blue_add.png"));
        buttonAddBookmarks.setToolTipText("Add bookmark");
        buttonAddBookmarks.addActionListener(this);

        buttonRemoveBookmarks = new JButton();
        buttonRemoveBookmarks.setIcon(getIcon("images/tag_blue_delete.png"));
        buttonRemoveBookmarks.setToolTipText("Remove bookmark");
        buttonRemoveBookmarks.addActionListener(this);

        buttonRefreshTable = new JButton();
        buttonRefreshTable.setIcon(getIcon("images/table_refresh.png"));
        buttonRefreshTable.setToolTipText("Refresh");
        buttonRefreshTable.addActionListener(this);

        buttonSwapUpBookmark = new JButton("▲");
        buttonSwapUpBookmark.addActionListener(this);

        buttonSwapDownBookmark = new JButton("▼");
        buttonSwapDownBookmark.addActionListener(this);
    }

    private ImageIcon getIcon(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        }
        return null;
    }

    private void initTable() {
        tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 4018704917650343203L;

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == COLUMN_DESCRIPTION)
                    return true;
                return false;
            }
        };

        tableModel.addColumn("Description");
        tableModel.addColumn("Diagram");
        tableModel.addColumn("Presentation");
        tableModel.addColumn("BookmarkModel");

        table = new JTable(tableModel);
        table.setRowHeight(ROW_HEIGHT);
        {
            TableColumn column = table.getColumnModel().getColumn(COLUMN_MODEL);
            column.setMinWidth(0);
            column.setMaxWidth(0);
            column.setPreferredWidth(0);
        }
        table.setSurrendersFocusOnKeystroke(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickMouseTable(e);
            }
        });

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyPressedTable(e);
            }
        });

        table.getCellEditor(0, COLUMN_DESCRIPTION).addCellEditorListener(new CellEditorListener() {

            @Override
            public void editingStopped(ChangeEvent e) {
                editingStoppedTable(e);
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        });

        this.scroller = new JScrollPane(table);
    }

    private void addProjectEventListener() {
        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            projectAccessor.addProjectEventListener(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        table.removeEditor();
        table.clearSelection();
        table.repaint();
    }

    private void addBookmarks() {
        AstahAccessor pAccesser = new AstahAccessor();
        IPresentation[] selections = pAccesser.getSelectingPresentation();
        if (selections == null || selections.length == 0) {
            return;
        }

        BookmarkControler controler = new BookmarkControler(this);
        controler.addBookmark(selections);
    }

    private void removeBookmarks() {
        if (table.getSelectedRowCount() <= 0) {
            return;
        }

        List models = new ArrayList();
        for (int idx : table.getSelectedRows()) {
            models.add(this.tableModel.getValueAt(idx, COLUMN_MODEL));
        }

        BookmarkControler controler = new BookmarkControler(this);
        controler.removeBookmark(models);
    }

    private void RefreshTable() {
        List models = new ArrayList();
        for (int i = 0; i < table.getRowCount(); i++) {
            models.add(this.tableModel.getValueAt(i, COLUMN_MODEL));
        }

        BookmarkControler controler = new BookmarkControler(this);
        controler.updateBookmarksWithoutDescription(models);
    }

    private void updateBookmark() {
        BookmarkControler controler = new BookmarkControler(this);
        Object selectedBookmark = getSelectedBookmark();
        int rowIndex = getRowIndexByModel(selectedBookmark);

        controler.updateBookmark(selectedBookmark,
                this.table.getValueAt(rowIndex, COLUMN_DESCRIPTION).toString());
    }

    private void swapBookmark(boolean isTowardDown) {
        BookmarkControler controler = new BookmarkControler(this);
        controler.swapBookmark(getSelectedBookmark(), isTowardDown);
    }

    private Object getSelectedBookmark() {
        if (this.table.getSelectedRowCount() == 0) {
            return null;
        }
        return this.table.getValueAt(this.table.getSelectedRow(), COLUMN_MODEL);
    }

    private int getRowIndexByModel(Object selectedModel) {
        for (int i = 0; i < this.table.getRowCount(); i++) {
            if (this.table.getValueAt(i, COLUMN_MODEL).equals(selectedModel)) {
                return i;
            }
        }
        return -1;
    }

    private void changeSelectBookmark(boolean isTowardDown) {
        if (this.table.getSelectedRowCount() == 0) {
            return;
        } else if (isTowardDown && (this.table.getSelectedRow() >= this.table.getRowCount() - 1)) {
            return;
        } else if (!isTowardDown && (this.table.getSelectedRow() <= 0)) {
            return;
        }

        if (isTowardDown) {
            this.table.setRowSelectionInterval(this.table.getSelectedRow() + 1,
                    this.table.getSelectedRow() + 1);
        } else {
            this.table.setRowSelectionInterval(this.table.getSelectedRow() - 1,
                    this.table.getSelectedRow() - 1);
        }

        selectPresentation();
    }

    private void showSelectedRow() {
        this.table.scrollRectToVisible(this.table.getCellRect(this.table.getSelectedRow(),
                COLUMN_DESCRIPTION, true));
    }

    private void selectPresentation() {
        this.selectPresentation(getSelectedBookmark());
    }

    private void selectPresentation(Object selectedBookmark) {

        BookmarkControler controler = new BookmarkControler(this);
        String[] classIdList = controler.getClassIdList(selectedBookmark);

        AstahAccessor pAccesser = new AstahAccessor();
        if (!pAccesser.selectPresentation(classIdList)) {
            JOptionPane.showMessageDialog(this, "Not Found!");
        }
    }

    private void loadBookmarks() {
        BookmarkControler controler = new BookmarkControler(this);
        controler.openBookmarks();
    }

    private void closeBookmarks() {
        BookmarkControler controler = new BookmarkControler(this);
        controler.closeBookmark();
    }

    /*
     * Event
     */
    private void clickButtonAddBookmarks() {
        this.addBookmarks();
    }

    private void clickButtonRemoveBookmarks() {
        this.removeBookmarks();
    }

    private void clickButtonRefreshTable() {
        this.RefreshTable();
    }

    private void clickButtonSwapUp() {
        this.swapBookmark(false);
    }

    private void clickButtonSwapDown() {
        this.swapBookmark(true);
    }

    private void clickMouseTable(MouseEvent e) {
        if (this.table == null || e.getButton() != MouseEvent.BUTTON1)
            return;

        Point pt = e.getPoint();
        int idx = this.table.rowAtPoint(pt);
        selectPresentation(this.table.getModel().getValueAt(idx, COLUMN_MODEL));
        this.table.requestFocus();
    }

    private void keyPressedTable(KeyEvent e) {
        switch (e.getKeyCode()) {

        case KeyEvent.VK_DOWN:
            if (e.isControlDown()) {
                swapBookmark(true);
            } else {
                changeSelectBookmark(true);
                this.table.requestFocus();
            }
            showSelectedRow();
            e.consume();
            break;

        case KeyEvent.VK_UP:
            if (e.isControlDown()) {
                swapBookmark(false);
            } else {
                changeSelectBookmark(false);
                this.table.requestFocus();
            }
            showSelectedRow();
            e.consume();
            break;

        case KeyEvent.VK_ENTER:
            if (this.table.getSelectedRowCount() > 0) {
                this.table.editCellAt(this.table.getSelectedRow(), COLUMN_DESCRIPTION);
                table.getEditorComponent().requestFocus();
                ((JTextField) table.getEditorComponent()).selectAll();
                e.consume();
            }
            break;

        default:
            break;
        }
    }

    private void editingStoppedTable(ChangeEvent e) {
        this.updateBookmark();
    }

    /*
     * Override
     */
    @Override
    public void projectChanged(ProjectEvent arg0) {
        BookmarkControler controler = new BookmarkControler(this);
        controler.syncBookmarks();
    }

    @Override
    public void projectClosed(ProjectEvent arg0) {
        this.closeBookmarks();
    }

    @Override
    public void projectOpened(ProjectEvent arg0) {
        this.loadBookmarks();
    }

    @Override
    public void activated() {
    }

    @Override
    public void addSelectionListener(ISelectionListener arg0) {
    }

    @Override
    public void deactivated() {
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getDescription() {
        return "Bookmarks";
    }

    @Override
    public String getTitle() {
        return "Bookmarks";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(buttonAddBookmarks)) {
            this.clickButtonAddBookmarks();
        } else if (e.getSource().equals(buttonRemoveBookmarks)) {
            this.clickButtonRemoveBookmarks();
        } else if (e.getSource().equals(buttonRefreshTable)) {
            this.clickButtonRefreshTable();
        } else if (e.getSource().equals(buttonSwapUpBookmark)) {
            this.clickButtonSwapUp();
        } else if (e.getSource().equals(buttonSwapDownBookmark)) {
            this.clickButtonSwapDown();
        }
    }

    @Override
    public void update() {
        BookmarkManager manager = BookmarkManager.getInstance();
        List<BookmarkModel> models = manager.getBookmarkModels();

        this.tableModel.setRowCount(models.size());

        for (int i = 0; i < models.size(); i++) {
            BookmarkModel model = models.get(i);
            this.table.setValueAt(model.getDescription(), i, COLUMN_DESCRIPTION);
            this.table.setValueAt(model.getDiagramName(), i, COLUMN_DIAGRAM_NAME);
            this.table.setValueAt(model.getPresentationsName(), i, COLUMN_PRESENTATION_NAME);
            this.table.setValueAt(model, i, COLUMN_MODEL);
        }

        this.refreshTable();
    }

    @Override
    public void update(BookmarkModel selectedModel, boolean isEditing) {
        update();

        if (selectedModel == null) {
            return;
        }

        int selectedRowIndex = getRowIndexByModel(selectedModel);
        if (selectedRowIndex < 0) {
            return;
        }

        this.table.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
        showSelectedRow();
        if (isEditing) {
            this.table.editCellAt(this.table.getSelectedRow(), COLUMN_DESCRIPTION);
            table.getEditorComponent().requestFocus();
            ((JTextField) table.getEditorComponent()).selectAll();
        }
    }

}
