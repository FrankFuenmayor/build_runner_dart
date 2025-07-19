package com.github.frankfuenmayor.flutterhelper.settings;

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import com.github.frankfuenmayor.flutterhelper.buildrunner.Icons;
import com.intellij.openapi.options.Configurable;
import com.intellij.util.ui.AbstractTableCellEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SettingsPanel implements Configurable {
    JPanel rootPanel;
    private JTable annotationsTable;
    private JButton button1;

    AnnotationsTableModel model;

    public SettingsPanel() {
        List<@NotNull BuildRunnerAnnotation> initialAnnotations = SettingsService.Companion.getInstance().getState().annotations;

button1.setIcon(Icons.Add);

        model = new AnnotationsTableModel(initialAnnotations);
        annotationsTable.setModel(model);
        annotationsTable.setCellEditor(new AbstractTableCellEditor() {
            @Override
            public Object getCellEditorValue() {
                return null;
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                return new JTextField();
            }
        });

        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.addRow();
            }
        });
    }

    @Override
    public @Nullable JComponent createComponent() {
        return rootPanel;
    }

    public boolean isModified() {
        return SettingsService.Companion.getInstance().getState().annotations.size() != model.getAnnotations().size();
    }

    @Override
    public void apply() {
        SettingsService
                .getInstance()
                .setAnnotations(model.getAnnotations());
    }

    @Override
    public void reset() {
        model.reset();
    }

    @Override
    public String getDisplayName() {
        return "Flutter Helper";
    }
}
