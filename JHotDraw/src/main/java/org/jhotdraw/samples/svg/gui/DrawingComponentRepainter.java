/**
 * @(#)DrawingComponentRepainter.java  1.0  2008-06-08
 *
 * Copyright (c) 2008 by the original authors of JHotDraw
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

import org.jhotdraw.draw.action.*;
import java.beans.*;
import javax.swing.*;
import org.jhotdraw.draw.*;

/**
 * Calls repaint on components, which show attributes of a drawing object
 * on the current view of the editor.
 *
 * @author Werner Randelshofer
 * @version 1.0 23.05.2008 Created.
 */

// Implements AbstractComponentRepainter interface since a lot of the methods
// for DrawingComponentRepainter & SelectionComponentRepainter are the same
public class DrawingComponentRepainter extends FigureAdapter
        implements AbstractComponentRepainter {

    private DrawingEditor editor;
    private JComponent component;

    // Constructor uses addListeners to reduce the code
    public DrawingComponentRepainter(DrawingEditor editor, JComponent component) {
        this.editor = editor;
        this.component = component;
        if (editor != null) {
            if (editor.getActiveView() != null) {
                DrawingView view = editor.getActiveView();
                this.addListeners(view);
            }

            editor.addPropertyChangeListener(this);
        }
    }

    @Override
    public void attributeChanged(FigureEvent evt) {
        component.repaint();
    }

    /**
     * Rewrote the propertyChange() to use the 
     * activeViewPropertyChangedHandler() or the drawingPropertyChangedHandler()
     * depending on if the propertyName hits the Active View or the DrawingView
     * @param evt A PropertyChangeEvent object describing the event source and 
     * the property that has changed to determine if the method executes the logic
     * for activeViewPropertyChangedHandler() or 
     * drawingPropertyChangedHandler()
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        
        if (name == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
            this.activeViewPropertyChangedHandler(evt);
        } else if (name == DrawingView.DRAWING_PROPERTY) {
            this.drawingPropertyChangedHandler(evt);
        } else {
            component.repaint();
        }
    }

    // Write method to remove Listeners
    public void dispose() {
        if (editor != null) {
            if (editor.getActiveView() != null) {
                DrawingView view = editor.getActiveView();
                this.removeListeners(view);
            }
            editor.removePropertyChangeListener(this);
            editor = null;
        }
        component = null;
    }

    /**
     * Adds the listeners
     * @param view paints a Drawing on a JComponent
     */
    @Override
    public void addListeners(DrawingView view) {
        view.addPropertyChangeListener(this);
                
        if (view.getDrawing() != null) {
            view.getDrawing().addFigureListener(this);
        }
    }

    /**
     * Removes the listeners
     * @param view paints a Drawing on a JComponent
     */
    @Override
    public void removeListeners(DrawingView view) {
        view.removePropertyChangeListener(this);
        
        if (view.getDrawing() != null) {
            view.getDrawing().removeFigureListener(this);
        }
    }

    /**
     * Updates the old values with the new values by removing the old
     * listeners and adding the new listeners when the values are changed
     * for the Active View 
     * @param evt the values for the DrawingView Object
     */
    @Override
    public void activeViewPropertyChangedHandler(PropertyChangeEvent evt) {
        DrawingView view = (DrawingView) evt.getOldValue();
        
        if (view != null) {
            this.removeListeners(view);
        }
        
        view = (DrawingView) evt.getNewValue();
        
        if (view != null) {
           this.addListeners(view);
        }
        
        component.repaint();
    }

    /**
     * Updates the old values with the new values by removing the old
     * listeners and adding the new listeners when the values are changed
     * for the Drawing
     * @param evt the values for the DrawingView Object
     */
    @Override
    public void drawingPropertyChangedHandler(PropertyChangeEvent evt) {
        Drawing drawing = (Drawing) evt.getOldValue();
        
        if (drawing != null) {
            drawing.removeFigureListener(this);
        }
        
        drawing = (Drawing) evt.getNewValue();
        
        if (drawing != null) {
            drawing.addFigureListener(this);
        }
        
        component.repaint();
    }
}

