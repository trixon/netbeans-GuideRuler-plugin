/*
 * Copyright 2023 Patrik Karlström.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.scior.mapton.tender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import se.trixon.almond.util.Dict;

@ActionID(
        category = "Tools",
        id = "se.scior.mapton.tender.TenderAction"
)
@ActionRegistration(
        displayName = "#CTL_TenderAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools/Scior", position = 0),})
@Messages("CTL_TenderAction=Tender")
public final class TenderAction implements ActionListener {

    private final Panel panel = new Panel();

    public TenderAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var d = new NotifyDescriptor(
                panel,
                "Skapa anbud från mall",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                new String[]{Dict.CANCEL.toString(), "Skapa"},
                Dict.CANCEL.toString());

        var notificationLineSupport = d.createNotificationLineSupport();
        panel.mapToDialog(d, notificationLineSupport);
        if ("Skapa" == DialogDisplayer.getDefault().notify(d)) {
            create();
        }
    }

    private void create() {
        try {
            FileUtils.copyDirectory(panel.getTenderTemplateDirFile(), panel.getTenderTargetDir());
            panel.save();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
