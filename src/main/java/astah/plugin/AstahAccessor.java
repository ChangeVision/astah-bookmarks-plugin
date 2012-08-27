package astah.plugin;

import java.util.ArrayList;
import java.util.HashSet;

import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ITaggedValue;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;

public class AstahAccessor {

    public void writeTaggedValue(String tagKey, String json) {
        try {
            TransactionManager.beginTransaction();

            ITaggedValue tag = getTaggedValue(tagKey, true);
            tag.setValue(json);

            TransactionManager.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            if (TransactionManager.isInTransaction()) {
                TransactionManager.abortTransaction();
            }
        }
    }

    public String readTaggedValue(String tagKey) {
        String ret = "";
        try {
            ITaggedValue tag = getTaggedValue(tagKey, false);
            if (tag != null) {
                ret = tag.getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private ITaggedValue getTaggedValue(String tagKey, boolean isCreateIfNotExist) {
        ITaggedValue ret = null;
        boolean isInTransaction = TransactionManager.isInTransaction();

        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            IModel project = projectAccessor.getProject();
            for (ITaggedValue ite : project.getTaggedValues()) {
                if (ite.getKey().equalsIgnoreCase(tagKey)) {
                    ret = ite;
                    break;
                }
            }

            if (ret == null && isCreateIfNotExist) {

                if (!isInTransaction)
                    TransactionManager.beginTransaction();

                ret = projectAccessor.getModelEditorFactory().getBasicModelEditor()
                        .createTaggedValue(project, tagKey, "");

                if (!isInTransaction)
                    TransactionManager.endTransaction();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            if (!isInTransaction && TransactionManager.isInTransaction()) {
                TransactionManager.abortTransaction();
            }
        }

        return ret;
    }

    public IPresentation[] getSelectingPresentation() {
        IPresentation[] ret = null;
        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            ret = projectAccessor.getViewManager().getDiagramViewManager()
                    .getSelectedPresentations();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean selectPresentation(String[] classIdList) {

        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            IPresentation[] selectPresentation = getPresentation(classIdList);

            if (selectPresentation != null && selectPresentation.length > 0) {
                IDiagramViewManager dvm = projectAccessor.getViewManager().getDiagramViewManager();
                dvm.open(selectPresentation[0].getDiagram());
                dvm.showInDiagramEditor(selectPresentation[0]);
                dvm.select(selectPresentation);
            } else {
                // not exist presentation
                return false;
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return true;
    }

    public IPresentation[] getPresentation(String[] classIdList) {

        if (classIdList == null) {
            return null;
        }

        final HashSet<String> presentationMap = new HashSet<String>();
        final ArrayList<IPresentation> selectPresentation = new ArrayList<IPresentation>();

        for (int i = 0; i < classIdList.length; i++) {
            presentationMap.add(classIdList[i]);
        }

        try {
            ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
            projectAccessor.findElements(new ModelFinder() {
                @Override
                public boolean isTarget(INamedElement arg0) {
                    boolean ret = false;
                    try {
                        for (IPresentation pre : arg0.getPresentations()) {
                            if (presentationMap.contains(pre.getID()) && !selectPresentation.contains(pre)) {
                                selectPresentation.add(pre);
                                ret = true;
                                break;
                            }
                        }
                    } catch (InvalidUsingException e) {
                        e.printStackTrace();
                    }
                    return ret;
                }
            });

            if (selectPresentation.size() == 0) {
                // not exist presentation
                return null;
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }

        return selectPresentation.toArray(new IPresentation[0]);
    }
}
