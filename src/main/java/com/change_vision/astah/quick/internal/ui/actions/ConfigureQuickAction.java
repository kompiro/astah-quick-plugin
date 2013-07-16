package com.change_vision.astah.quick.internal.ui.actions;

import com.change_vision.astah.quick.internal.Activator;
import com.change_vision.astah.quick.internal.ui.QuickInterfaceUI;
import com.change_vision.astah.quick.internal.ui.configure.ConfigWindow;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfigureQuickAction implements IPluginActionDelegate {

    @Override
    public Object run(IWindow arg0) throws UnExpectedException {
        Activator instance = Activator.getInstance();
        final QuickInterfaceUI ui = instance.getUI();
        ui.uninstall();
        Window frame = arg0.getParent();
        ConfigWindow window = new ConfigWindow(frame);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ui.install();
            }
        });
        window.open();
        return null;
    }

}
