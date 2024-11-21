/*
 * Copyright 2024 Patrik Karlström.
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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.StringUtils;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;

/**
 *
 * @author Patrik Karlström
 */
public class Panel extends javax.swing.JPanel {

    private static final Logger LOG = Logger.getLogger(Panel.class.getName());

    private NotificationLineSupport mNotificationLineSupport;
    private NotifyDescriptor mNotifyDescriptor;
    private File mTargetDir;
    private final Preferences preferences = NbPreferences.forModule(Panel.class);

    /**
     * Creates new form Panel
     */
    public Panel() {
        initComponents();
        init();
    }

    public File getTenderTargetDir() {
        return mTargetDir;
    }

    public void mapToDialog(NotifyDescriptor d, NotificationLineSupport notificationLineSupport) {
        mNotifyDescriptor = d;
        mNotificationLineSupport = notificationLineSupport;

        calculateTargetDirectory();
    }

    private void init() {
        baseFilePanel.setHeader("Baskatalog");
        templateFilePanel.setHeader("Mallkatalog");
        baseFilePanel.setMode(JFileChooser.DIRECTORIES_ONLY);
        templateFilePanel.setMode(JFileChooser.DIRECTORIES_ONLY);

        nameTextField.setText(preferences.get("name", ""));
        baseFilePanel.setPath(preferences.get("base", ""));
        templateFilePanel.setPath(preferences.get("template", ""));

        nameTextField.selectAll();

        var listener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                calculateTargetDirectory();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                calculateTargetDirectory();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculateTargetDirectory();
            }
        };

        nameTextField.getDocument().addDocumentListener(listener);
        baseFilePanel.getTextField().getDocument().addDocumentListener(listener);
        templateFilePanel.getTextField().getDocument().addDocumentListener(listener);
    }

    private void calculateTargetDirectory() {
        mNotifyDescriptor.setValid(false);

        if (StringUtils.isAnyBlank(nameTextField.getText(), baseFilePanel.getPath(), templateFilePanel.getPath())) {
            mNotificationLineSupport.setErrorMessage("Alla fält måste vara ifyllda.");
            return;
        }

        if (!baseFilePanel.getFile().isDirectory() || !templateFilePanel.getFile().isDirectory()) {
            mNotificationLineSupport.setErrorMessage("Valda kataloger finns ej.");
            return;
        }

        var prefix = "";
        var maxValue = 0;
        var baseDir = baseFilePanel.getFile();

        for (var file : baseDir.listFiles()) {
            if (file.isFile()) {
                continue;
            }
            try {

                var nameComponents = StringUtils.split(file.getName(), " ");
                if (!StringUtils.equalsIgnoreCase(nameComponents[0], "Anbud")) {
                    continue;
                }

                prefix = StringUtils.left(nameComponents[1], 1);
                var value = Integer.parseInt(StringUtils.removeStart(nameComponents[1], prefix));

                maxValue = Math.max(value, maxValue);
            } catch (Exception e) {
            }
        }

        var newName = "Anbud %s%03d %s".formatted(prefix, ++maxValue, nameTextField.getText());
        LOG.log(Level.SEVERE, newName);

        mTargetDir = new File(baseDir, newName);
        mNotificationLineSupport.setInformationMessage(mTargetDir.toString());
        mNotifyDescriptor.setValid(false);//TODO true
    }

    public void save() {
        preferences.put("name", nameTextField.getText());
        preferences.put("base", baseFilePanel.getPath());
        preferences.put("template", templateFilePanel.getPath());
    }

    public File getTenderTemplateDirFile() {
        return templateFilePanel.getFile();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        baseFilePanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();
        templateFilePanel = new se.trixon.almond.util.swing.dialogs.FileChooserPanel();

        org.openide.awt.Mnemonics.setLocalizedText(nameLabel, org.openide.util.NbBundle.getMessage(Panel.class, "Panel.nameLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameTextField)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(templateFilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                    .addComponent(baseFilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(nameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                .addComponent(baseFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(templateFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel baseFilePanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private se.trixon.almond.util.swing.dialogs.FileChooserPanel templateFilePanel;
    // End of variables declaration//GEN-END:variables
}
