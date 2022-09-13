package com.diguage.cafe.divecode;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class AttachMain {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, URISyntaxException {
//        URL url = Thread.currentThread().getContextClassLoader().getResource("com/diguage/cafe/divecode/AgentMain.class");
//        String basePath = url.toURI().getPath().replace("classes/" + "com/diguage/cafe/divecode/AgentMain.class", "");
        String basePath = "/Users/lijun695/Documents/byte-buddy-tutorial/target/";
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        try {
            vm.loadAgent(basePath + "jiadao.jar");
        } finally {
            vm.detach();
        }
    }
}
