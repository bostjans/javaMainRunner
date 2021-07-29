# javaMainRunner
Java MainRunner base class.

Your Java Main Class/Method should be as simple .. as possible:

```
    public static void main(String[] a_args) {
        // Initialization
        GlobalVar.getInstance().sProgName = "programShell";
        GlobalVar.getInstance().sVersionBuild = "225";

        // Generate main program class
        objInstance = new MainRun();

        iReturnCode = objInstance.invokeApp(a_args);
        // Return
        if (iReturnCode != ConstGlobal.PROCESS_EXIT_SUCCESS)
            System.exit(iReturnCode);
        else
            return;
    }
```
