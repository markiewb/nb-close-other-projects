/**
 * Copyright 2013 markiewb
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.markiew.netbeans.plugin.actions.closedocuments;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import javax.swing.Icon;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class DocumentManager {

    public static DocumentManager getInstance() {
        return new DocumentManager();
    }

    private DocumentManager() {
    }

    /**
     *
     * @param projectToMatch
     * @param onlyMatching if true, it returns only {@link TopComponent}s which
     * are from the same project; if false, it returns only
     * {@link TopComponent}s which are not from the given project
     * @return
     */
    public Collection<TopComponent> getDocumentsForProject(Project projectToMatch, boolean onlyMatching) {

        if (projectToMatch == null) {
            return Collections.emptyList();
        }

        final WindowManager wm = WindowManager.getDefault();
        final LinkedHashSet<TopComponent> result = new LinkedHashSet<TopComponent>();
        for (TopComponent tc : getCurrentEditors()) {
            if (!wm.isEditorTopComponent(tc)) {
                continue;
            }
            DataObject dob = tc.getLookup().lookup(DataObject.class);
            if (dob != null) {
                Project projectFromFile = FileOwnerQuery.getOwner(dob.getPrimaryFile());
                final boolean sameProject = projectToMatch.equals(projectFromFile);
                if ((onlyMatching && sameProject) || (!onlyMatching && !sameProject)) {
                    result.add(tc);
                }
            } else {
                if (onlyMatching) {
                    //NOP
                } else {
                    //close others also closes documents without projects
                    //for example diff windows will be closed
                    result.add(tc);
                }
            }
        }
        return result;
    }

    public Collection<TopComponent> getUnchangedDocuments() {

        Image emptyImage = ImageUtilities.icon2Image(new EmptyIcon());
        final WindowManager wm = WindowManager.getDefault();
        final LinkedHashSet<TopComponent> result = new LinkedHashSet<TopComponent>();
        for (TopComponent tc : getCurrentEditors()) {
            if (!wm.isEditorTopComponent(tc)) {
                continue;
            }

            //check for the format of an unsaved file
            boolean isUnsaved = null != tc.getLookup().lookup(SaveCookie.class);
            if (isUnsaved) {
                continue;
            }

            DataObject dob = tc.getLookup().lookup(DataObject.class);
            if (dob != null) {
                try {
                    final FileObject file = dob.getPrimaryFile();
                    FileSystem fileSystem = file.getFileSystem();
                    if (fileSystem.getStatus() instanceof FileSystem.HtmlStatus) {
                        FileSystem.HtmlStatus status = (FileSystem.HtmlStatus) fileSystem.getStatus();

                        //HACK B: There is a change if the icon is annotated
                        Image annotateIcon = status.annotateIcon(emptyImage, 0, new HashSet<FileObject>(Arrays.asList(file)));
                        boolean isUnchanged = annotateIcon == emptyImage;
//                        System.out.println(String.format("%s html for '%s' image=%s %s", !isUnchanged, html, annotateIcon, file));

                        if (isUnchanged) {
                            result.add(tc);
                        }
                    } else {
                        //could not determine status, keep this document
                    }
                } catch (FileStateInvalidException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                //close diff windows too
                result.add(tc);
            }
        }
        return result;
    }

    private Collection<TopComponent> getCurrentEditors() {
        final ArrayList<TopComponent> result = new ArrayList<TopComponent>();
        final WindowManager wm = WindowManager.getDefault();
        for (Mode mode : wm.getModes()) {
            if (wm.isEditorMode(mode)) {
                result.addAll(Arrays.asList(wm.getOpenedTopComponents(mode)));
            }
        }
        return result;
    }

    public final class EmptyIcon implements Icon {

        private int height;
        private int width;

        public EmptyIcon() {
            this(1, 1);
        }

        public EmptyIcon(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
        }

    }
}
