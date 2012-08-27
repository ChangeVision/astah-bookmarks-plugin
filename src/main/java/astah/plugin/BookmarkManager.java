package astah.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BookmarkManager
 * 
 * @author K.Fujimoto
 * 
 */
public class BookmarkManager {

    private final static BookmarkManager INSTANCE = new BookmarkManager();
    private final String TAG_KEY_FOR_SAVE_PROJECT = "astah.rtc.plugin.bookmarks";
    private List<BookmarkModel> models = new ArrayList<BookmarkModel>();
    private JSONSaveDataConverter converter = new JSONSaveDataConverter();
    private AstahAccessor tagAccessor = new AstahAccessor();

    private boolean isAutoSave = true;

    private BookmarkManager() {
    }

    public static BookmarkManager getInstance() {
        return INSTANCE;
    }

    public void setAutoSave(boolean isAutoSave) {
        this.isAutoSave = isAutoSave;
    }

    public boolean isAutoSave() {
        return isAutoSave;
    }

    public List<BookmarkModel> getBookmarkModels() {
        return Collections.unmodifiableList(models);
    }

    public void addBookmark(BookmarkModel model) {
        if (model != null) {
            models.add(model);
        }

        if (isAutoSave) {
            saveToProject();
        }
    }

    public void removeBookmark(BookmarkModel model) {
        if (model != null) {
            models.remove(model);
        }

        if (isAutoSave) {
            saveToProject();
        }
    }

    public void removeAllBookmarkModels() {
        models.clear();
    }

    public void saveToProject() {
        String json = converter.convertToJSON(models);
        tagAccessor.writeTaggedValue(TAG_KEY_FOR_SAVE_PROJECT, json);
    }

    public void loadFromProject() {
        String savedJson = tagAccessor.readTaggedValue(TAG_KEY_FOR_SAVE_PROJECT);
        models = converter.convertToBookmarkModels(savedJson);
    }

    public void swapBookmark(BookmarkModel selectedBookmark, boolean isUp) {
        int index = models.indexOf(selectedBookmark);
        if (index < 0) {
            return;
        } else if (index <= 0 && isUp == false) {
            return;
        } else if (index >= (models.size() - 1) && isUp == true) {
            return;
        }

        if (isUp) {
            this.models.remove(selectedBookmark);
            this.models.add(index + 1, selectedBookmark);
        } else {
            this.models.remove(selectedBookmark);
            this.models.add(index - 1, selectedBookmark);
        }

        if (isAutoSave) {
            saveToProject();
        }
    }

    public void updateBookmark(BookmarkModel model, String description, String diagramName,
            String presentationsName, String[] classIdList) {
        int index = this.models.indexOf(model);
        if (index < 0) {
            return;
        }

        BookmarkModel editTarget = this.models.get(index);
        if (description != null) {
            editTarget.setDescription(description);
        }
        if (diagramName != null) {
            editTarget.setDiagramName(diagramName);
        }
        if (presentationsName != null) {
            editTarget.setPresentationsName(presentationsName);
        }
        if (classIdList != null) {
            editTarget.setClassIdList(classIdList.clone());
        }

        if (isAutoSave) {
            saveToProject();
        }
    }

    public boolean syncBookmarks() {
        AstahAccessor tagAccesser = new AstahAccessor();
        JSONSaveDataConverter converter = new JSONSaveDataConverter();

        String tagValue = tagAccesser.readTaggedValue(TAG_KEY_FOR_SAVE_PROJECT);
        String json = converter.convertToJSON(models);

        if (!tagValue.equals(json)) {
            loadFromProject();
            return true;
        }

        return false;
    }

}
