# javaMainRunner
Java MainRunner base class.

Your Java Main Class/Method should be as simple .. as possible:

Class: `MainRunBase`

* is (simple) Class for CommandLine (Java) programs.

It offers:

* method: initialize()
  * .. to initialize variables
* setConfig() .. to handle (external) configuration loading
* defineArguments() .. to define (program) argument(-s)
* readArguments() .. to handle argument extraction
* run() .. for actual logic
* ..

## Example

### Main method

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
    }
```
