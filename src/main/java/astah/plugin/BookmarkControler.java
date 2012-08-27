package astah.plugin;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.presentation.IPresentation;

public class BookmarkControler {

    private IBookmarkView view;

    public BookmarkControler(IBookmarkView view) {
        this.view = view;
    }

    public void addBookmark(IPresentation[] selectionPresentation) {
        BookmarkModel model = createBookmarkModel(selectionPresentation);
        if (model != null) {
            BookmarkManager.getInstance().addBookmark(model);

            view.update(model, true);
        }
    }

    private BookmarkModel createBookmarkModel(IPresentation[] selectingPresentations) {

        if (selectingPresentations == null || selectingPresentations.length == 0) {
            return null;
        }

        BookmarkModel ret = new BookmarkModel();
        {
            List<String> classIds = new ArrayList<String>();
            for (int i = 0; i < selectingPresentations.length; i++) {
                if (ret.getDiagramName() == null && selectingPresentations[i].getDiagram() != null) {
                    ret.setDiagramName(selectingPresentations[i].getDiagram().getName());
                }
                if (ret.getPresentationsName() == null
                        && selectingPresentations[i].getLabel() != null
                        && !selectingPresentations[i].getLabel().equals("")) {
                    ret.setPresentationsName(selectingPresentations[i].getLabel());
                }
                classIds.add(selectingPresentations[i].getID());
            }
            ret.setClassIdList(classIds.toArray(new String[0]));
            if (ret.getClassIdList().length > 1) {
                ret.setPresentationsName(ret.getPresentationsName() + " + etc");
            }
            ret.setDescription(ret.getDiagramName() + " - " + ret.getPresentationsName());
        }

        return ret;
    }

    public void removeBookmark(List models) {
        BookmarkManager manager = BookmarkManager.getInstance();
        for (Object model : models) {
            if (model instanceof BookmarkModel) {
                manager.removeBookmark((BookmarkModel) model);
            }
        }

        view.update();
    }

    public void swapBookmark(Object selectedBookmark, boolean isUp) {
        if (selectedBookmark instanceof BookmarkModel) {
            BookmarkModel model = (BookmarkModel) selectedBookmark;
            BookmarkManager manager = BookmarkManager.getInstance();
            manager.swapBookmark(model, isUp);

            view.update(model, false);
        }
    }

    public String[] getClassIdList(Object selectionBookmark) {
        if (selectionBookmark instanceof BookmarkModel) {
            return ((BookmarkModel) selectionBookmark).getClassIdList();
        }
        return null;
    }

    public void updateBookmark(Object selectedBookmark, String description) {
        if (selectedBookmark instanceof BookmarkModel) {
            BookmarkModel model = (BookmarkModel) selectedBookmark;
            BookmarkManager manager = BookmarkManager.getInstance();
            manager.updateBookmark(model, description, null, null, null);
            view.update(model, false);
        }
    }

    public void openBookmarks() {
        BookmarkManager.getInstance().loadFromProject();
        view.update();
    }

    public void closeBookmark() {
        BookmarkManager.getInstance().removeAllBookmarkModels();
        view.update();
    }

    public void updateBookmarksWithoutDescription(List<BookmarkModel> models) {
        if (models == null || models.size() == 0) {
            return;
        }

        AstahAccessor astah = new AstahAccessor();
        BookmarkManager manager = BookmarkManager.getInstance();
        boolean isAutoSave = manager.isAutoSave();

        try {
            if (isAutoSave) {
                manager.setAutoSave(false);
            }
            
            for (BookmarkModel model : models) {
                String[] classId = getClassIdList(model);
    
                if (classId == null || classId.length == 0) {
                    manager.removeBookmark(model);
                    continue;
                }
    
                IPresentation[] presentations = astah.getPresentation(classId);
                if (presentations == null || presentations.length == 0) {
                    manager.removeBookmark(model);
                    continue;
                }
    
                BookmarkModel modelNow = createBookmarkModel(presentations);
                manager.updateBookmark(model, null, modelNow.getDiagramName(),
                        modelNow.getPresentationsName(), modelNow.getClassIdList());
            }
    
            if (isAutoSave) {
                manager.saveToProject();
                manager.setAutoSave(isAutoSave);
            }
            
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            manager.setAutoSave(isAutoSave);
        }

        view.update();
    }

    public void syncBookmarks() {
        BookmarkManager manager = BookmarkManager.getInstance();
        if (manager.syncBookmarks()) {
            view.update();
        }
    }
}
