package regression;
import org.safs.model.Component;
import org.safs.model.tools.RuntimeDataAware;
import org.safs.tools.RuntimeDataInterface;

/** *** DO NOT EDIT THIS FILE ***<br>
    THIS FILE IS GENERATED AUTOMATICALLY by org.safs.model.tools.ComponentGenerator!<br><p>
    A JSAFS AppMap reference for SAFS App Map: 'C:/SePlusInstalled/REGRESSION/Maps' */
public final class Map implements RuntimeDataAware {

    private static RuntimeDataInterface dataInterface = null;

    /** Called internally as part of the bootstrap process of Dependency Injection. */
    public void setRuntimeDataInterface(RuntimeDataInterface helper){
        dataInterface = helper;
    }

    // The Names of ApplicationConstants:

    /** "SwingAppID" */
    public static final String SwingAppID = "SwingAppID";

    /** The resolved runtime value of constant 'SwingAppID', or null. */
    public static String SwingAppID(){
        try{ return dataInterface.getVariable("SwingAppID"); }
        catch(Exception x){ return null; }
    }

    /** "SwingAppJar" */
    public static final String SwingAppJar = "SwingAppJar";

    /** The resolved runtime value of constant 'SwingAppJar', or null. */
    public static String SwingAppJar(){
        try{ return dataInterface.getVariable("SwingAppJar"); }
        catch(Exception x){ return null; }
    }

    // The Names of Window and Child Component objects: 

    /** "SwingApp" Component and its children. */
    public static class SwingApp {

        // No use for a default constructor.
        private SwingApp(){}

        /** "SwingApp" Window Component itself. */
        public static final Component SwingApp = new Component("SwingApp");

        /** "JDragTab" Component in "SwingApp". */
        public static final Component JDragTab = new Component(SwingApp, "JDragTab");
    }

    /** "ApplicationConstants" Component and its children. */
    public static class ApplicationConstants {

        // No use for a default constructor.
        private ApplicationConstants(){}

        /** "ApplicationConstants" Window Component itself. */
        public static final Component ApplicationConstants = new Component("ApplicationConstants");

        /** "SwingAppID" Component in "ApplicationConstants". */
        public static final Component SwingAppID = new Component(ApplicationConstants, "SwingAppID");

        /** "SwingAppJar" Component in "ApplicationConstants". */
        public static final Component SwingAppJar = new Component(ApplicationConstants, "SwingAppJar");
    }
}