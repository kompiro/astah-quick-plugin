package com.change_vision.astah.quick.internal.ui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.quick.internal.AstahAPIWrapper;
import com.change_vision.astah.quick.internal.command.Commands;
import com.change_vision.astah.quick.internal.model.QuickProperties;

class OpenQuickWindowEventDispatcher implements KeyEventDispatcher {

    private final class ClickOutsideWindowListener implements AWTEventListener {
        @Override
        public void eventDispatched(AWTEvent event) {
            int id = event.getID();
            if (id == MouseEvent.MOUSE_CLICKED) {
                if (event instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) event;
                    Object source = me.getSource();
                    if (source instanceof Component) {
                        Component c = (Component) source;
                        if(window != null && window.isVisible() && (window.isAncestorOf(c) || c == window) == false ){
                            window.close();
                            window.reset();
                        }
                    }
                }
            }
        }
    }

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(OpenQuickWindowEventDispatcher.class);
    
    private final AstahAPIWrapper wrapper = new AstahAPIWrapper();
    
    private final QuickProperties properties;

    private QuickWindow window;
    
    private final Commands commands;
    
    OpenQuickWindowEventDispatcher(QuickProperties properties, Commands commands) {
        this.commands = commands;
        this.properties = properties;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
        String fireKeyStroke = properties.getKeyStroke();
        KeyStroke strokeEvent = KeyStroke.getKeyStroke(fireKeyStroke);
        if(strokeEvent.equals(keyStroke)){
            logger.trace("{} is fired.",fireKeyStroke);
            if(window == null){
                createQuickWindow();
            }
            if(window.isVisible()){
                window.close();
                window.reset();
            }else{
                window.open();
            }
            return true;
        }
        return false;
    }
    

    private void createQuickWindow() {
        JFrame frame = wrapper.getMainFrame();
        window = new QuickWindow(frame,commands);
        setClickOutsideWindowBehavior();
    }

    private void setClickOutsideWindowBehavior() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new ClickOutsideWindowListener(), AWTEvent.MOUSE_EVENT_MASK);
    }
}
