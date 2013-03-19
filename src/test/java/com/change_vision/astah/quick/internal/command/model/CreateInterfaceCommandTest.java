package com.change_vision.astah.quick.internal.command.model;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.change_vision.astah.quick.command.exception.ExecuteCommandException;
import com.change_vision.jude.api.inf.model.INamedElement;

public class CreateInterfaceCommandTest {

    @Mock
    private ModelAPI api;
    
    @Mock
    private INamedElement element;
    
    private CreateInterfaceCommand command;
    
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        command = new CreateInterfaceCommand();
        command.setAPI(api);
    }

    @Test(expected=IllegalArgumentException.class)
    public void executeWithNull() throws Exception {
        command.execute((String[])null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void executeWithNoArgument() throws Exception {
        command.execute();
    }
    
    @Test
    public void executeWithArgument() throws Exception {
        when(api.findByFQCN("hoge")).thenReturn(new INamedElement[0]);
        command.execute("hoge");
        verify(api).createInterface("hoge");
    }
    
    @Test
    public void executeWithArguments() throws Exception {
        when(api.findByFQCN("hoge")).thenReturn(new INamedElement[0]);
        when(api.findByFQCN("fuga")).thenReturn(new INamedElement[0]);
        
        command.execute("hoge","fuga");

        verify(api).createInterface("hoge");
        verify(api).createInterface("fuga");
    }
    
    @Test(expected=ExecuteCommandException.class)
    public void executeWithFoundArgument() throws Exception {
        when(api.findByFQCN("hoge")).thenReturn(new INamedElement[]{
                element
        });
        command.execute("hoge");
    }

}
