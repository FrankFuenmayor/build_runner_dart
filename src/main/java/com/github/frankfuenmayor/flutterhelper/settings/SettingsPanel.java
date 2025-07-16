package com.github.frankfuenmayor.flutterhelper.settings;

import com.github.frankfuenmayor.flutterhelper.buildrunner.BuildRunnerAnnotation;
import com.intellij.util.ui.AbstractTableCellEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SettingsPanel implements Configurable {
    JPanel rootPanel;
    private JTable annotationsTable;
    private JLabel lblPlus;

    AnnotationsTableModel model;

    private boolean isModified = false;

    public SettingsPanel() {
        List<@NotNull BuildRunnerAnnotation> initialAnnotations = SettingsService.Companion.getInstance().getState().annotations;
        model = new AnnotationsTableModel(initialAnnotations);
        annotationsTable.setModel(model);

        model.addTableModelListener(e -> {
            isModified = true;
        });

        lblPlus.addMouseListener(new MouseAdapter() {
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
