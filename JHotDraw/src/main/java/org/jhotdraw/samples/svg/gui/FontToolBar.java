/*
 * @(#)StrokeToolBar.java  1.2  2008-05-23
 *
 * Copyright (c) 2007-2008 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */
package org.jhotdraw.samples.svg.gui;

import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;
import javax.swing.border.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.SliderUI;
import org.jhotdraw.app.JHotDrawFeatures;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.FigureAttributeEditorHandler;
import org.jhotdraw.gui.JAttributeSlider;
import org.jhotdraw.gui.JAttributeTextField;
import org.jhotdraw.gui.JFontChooser;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.gui.plaf.palette.*;
import org.jhotdraw.text.FontFormatter;
import org.jhotdraw.text.JavaNumberFormatter;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;

/**
 * StrokeToolBar.
 *
 * @author Werner Randelshofer
 * @version 1.2 2008-05-23 Hide the toolbar if nothing is selected, and no
 * creation tool is active.
 * <br>1.1 2008-03-26 Don't draw button borders.
 * <br>1.0 May 1, 2007 Created.
 */
public class FontToolBar extends AbstractToolBar {

    private SelectionComponentDisplayer displayer;

    /**
     * Creates new instance.
     */
    public FontToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
        setName(labels.getString("font.toolbar"));
        JFontChooser.loadAllFonts();
        setDisclosureStateCount(3);
    }

    @Override
    public void setEditor(DrawingEditor newValue) {
        if (displayer != null) {
            displayer.dispose();
            displayer = null;
        }
        super.setEditor(newValue);
        if (newValue != null) {
            displayer = createSelectionComponentDisplayer(editor, this);
        }
    }

    private SelectionComponentDisplayer createSelectionComponentDisplayer(DrawingEditor editor, JComponent component) {
        return new SelectionComponentDisplayer(editor, component) {

            @Override
            public void updateVisibility() {
                boolean newValue = editor != null
                        && editor.getActiveView() != null
                        && (isVisibleIfCreationTool && ((editor.getTool() instanceof TextCreationTool) || editor.getTool() instanceof TextAreaCreationTool)
                        || containsTextHolderFigure(editor.getActiveView().getSelectedFigures()));
                component.setVisible(newValue);
                component.setPreferredSize(newValue ? null : new Dimension(0, 0));
                component.revalidate();
            }
        };
    }

    /**
     * Checks if a collection of figures contain a TextHolderFigure
     * 
     * @param figures a collection of figures
     * @return true or false depending on, if one of the figures is an instance of TextHolderFigure
     */
    private boolean containsTextHolderFigure(Collection<Figure> figures) {
        for (Figure f : figures) {
            if (f instanceof TextHolderFigure) {
                return true;
            } else if (f instanceof CompositeFigure) {
                if (containsTextHolderFigure(((CompositeFigure) f).getChildren())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates the palette tool component in the tool bar
     * 
     * @param state the open-close state of the component (0 = close, 1 = compact 2 = full)
     * @return the JComponent of the font palette bar component
     */
    @Override
    @FeatureEntryPoint(JHotDrawFeatures.FONT_PALETTE)
    protected JComponent createDisclosedComponent(int state) {
        JPanel p = null;

        if (state >= 1 && state <= 2) {
            GridBagLayout layout = new GridBagLayout();

            p = new JPanel();
            p.setOpaque(false);
            p.setBorder(new EmptyBorder(5, 5, 5, 8));
            p.setLayout(layout);

            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");

            createFontChooserComponent(p, labels, state);

            createFontSizeComponent(p, labels);

            createFontStyleButton(p, ButtonFactory.createFontStyleBoldButton(editor, labels), "first");
            createFontStyleButton(p, ButtonFactory.createFontStyleItalicButton(editor, labels), "middle");
            createFontStyleButton(p, ButtonFactory.createFontStyleUnderlineButton(editor, labels), "last");
        }

        return p;
    }

    /**
     * Create a slider for choosing the font size
     *
     * @param sizePanel the panel which the font size slider should be added to
     * @param labels the labels used to display the attribute
     */
    private void createFontSizeSlider(JPanel sizePanel, ResourceBundleUtil labels) {

        JPopupButton sizePopupButton = new JPopupButton();
        JAttributeSlider sizeSlider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 12);
        sizePopupButton.add(sizeSlider);
        labels.configureToolBarButton(sizePopupButton, "attribute.fontSize");
        sizePopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(sizePopupButton));
        sizePopupButton.setPopupAnchor(SOUTH_EAST);
        new SelectionComponentRepainter(editor, sizePopupButton);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(3, 0, 0, 0);
        sizePanel.add(sizePopupButton, gbc);
        sizeSlider.setUI((SliderUI) PaletteSliderUI.createUI(sizeSlider));
        sizeSlider.setScaleFactor(1d);
        new FigureAttributeEditorHandler<Double>(FONT_SIZE, sizeSlider, editor);
    }

    /**
     * Creates an input field for the font size
     *
     * @param sizePanel the panel which the font size slider should be added to
     * @param labels the labels used to display the attribute
     */
    private void createFontSizeField(JPanel sizePanel, ResourceBundleUtil labels) {

        JAttributeTextField<Double> sizeField = new JAttributeTextField<Double>();
        sizeField.setColumns(1);
        sizeField.setToolTipText(labels.getString("attribute.fontSize.toolTipText"));
        sizeField.setHorizontalAlignment(JAttributeTextField.RIGHT);
        sizeField.putClientProperty("Palette.Component.segmentPosition", "first");
        sizeField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(sizeField));
        sizeField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(0d, 1000d, 1d));
        sizeField.setHorizontalAlignment(JTextField.LEADING);
        new FigureAttributeEditorHandler<Double>(FONT_SIZE, sizeField, editor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridwidth = 2;
        gbc.weightx = 1f;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sizePanel.add(sizeField, gbc);
    }

    /**
     * Creates a component with a font size field and slider
     *
     * @param parentPanel the panel which the font size slider should be added to
     * @param labels the labels used to display the attribute
     */
    private void createFontSizeComponent(JPanel parentPanel, ResourceBundleUtil labels) {

        JPanel sizePanel = new JPanel(new GridBagLayout());
        sizePanel.setOpaque(false);

        createFontSizeSlider(sizePanel, labels);
        createFontSizeField(sizePanel, labels);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(0, 0, 0, 0);

        parentPanel.add(sizePanel, gbc);
    }

    /**
     * Creates a font chooser text field
     *
     * @param panel the panel which the font size slider should be added to
     * @param labels the labels used to display the attribute
     * @param state the open-close state of the component (0 = close, 1 = compact 2 = full)
     */
    private void createFontChooserField(JPanel panel, ResourceBundleUtil labels, int state) {

        int[] columns = new int[]{0, 2, 10};
        int[] gridWidth = new int[]{0, 2, 3};

        JAttributeTextField<Font> faceField = new JAttributeTextField<>();
        faceField.setColumns(columns[state]);
        faceField.setToolTipText(labels.getString("attribute.font.toolTipText"));
        faceField.setHorizontalAlignment(JAttributeTextField.RIGHT);
        faceField.putClientProperty("Palette.Component.segmentPosition", "first");
        faceField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(faceField));
        faceField.setHorizontalAlignment(JTextField.LEADING);
        faceField.setFormatterFactory(FontFormatter.createFormatterFactory());
        new FigureAttributeEditorHandler<Font>(FONT_FACE, faceField, editor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridwidth = gridWidth[state];
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(faceField, gbc);
    }

    /**
     * Creates a font chooser button
     *
     * @param panel the panel which the font size slider should be added to
     * @param labels the labels used to display the attribute
     */
    private void createFontChooserButton(JPanel panel, ResourceBundleUtil labels) {

        AbstractButton btn = ButtonFactory.createFontButton(editor, labels);
        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(btn, gbc);
    }

    /**
     * Creates a font chooser field
     *
     * @param panel Panel to put the font chooser on
     * @param labels Label resource for the tool tip
     * @param columns Amount of columns in the font chooser field
     * @param gridWidth Width of the grid in the constraints
     */
    private void createFontChooserComponent(JPanel panel, ResourceBundleUtil labels, int state) {

        createFontChooserField(panel, labels, state);
        createFontChooserButton(panel, labels);
    }

    /**
     * Creates a button with specific constraints based on an abstract button
     *
     * @param panel panel to put the font chooser on
     * @param btn the button type, e.g. bold, italic, underline
     * @param clientProperty the order of the button in the panel e.g. first, middle, last
     */
    private void createFontStyleButton(JPanel panel, AbstractButton btn, String clientProperty) {

        btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
        btn.putClientProperty("Palette.Component.segmentPosition", clientProperty);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.insets = new Insets(3, 0, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(btn, gbc);
    }

    @Override
    protected String getID() {
        return "font";
    }

    @Override
    protected int getDefaultDisclosureState() {
        return 1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
