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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(
        category = "Editor",
        id = "closedocuments.CloseUnmodifiedFilesAction"
        )
@ActionRegistration(
        displayName = "#CTL_CloseUnmodifiedFilesAction"
        )
@ActionReferences({
    @ActionReference(path = "Editors/TabActions", position = 0 , separatorBefore = -50),
})

@Messages("CTL_CloseUnmodifiedFilesAction=Close Unmodified Files")
public final class CloseUnmodifiedFilesAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (TopComponent tc : DocumentManager.getInstance().getUnchangedDocuments()) {
            tc.close();
        }
    }

}