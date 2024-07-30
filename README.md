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
        GlobalVar.getInstance().sVersionBuild = "2..";

        // Generate main program class
        objInstance = new MainRun();

        iReturnCode = objInstance.invokeApp(a_args);
        // Return
        if (iReturnCode != ConstGlobal.PROCESS_EXIT_SUCCESS)
            System.exit(iReturnCode);
    }
```


# ToDo

* write sample(s)
* prepare run script (run.sh)
* prepare "watchDog" script (processCheck.sh)
  * check for PID in: /var/run/ -> /var/tmp/


# Versions

```
curl -i http://developer.dev404.net/maven/com/stupica/base/mainRunner/
curl -i https://developer.dev404.net/maven/com/stupica/base/mainRunner/
```


# Other

* https://github.com/melin/java-main-runner
* https://github.com/mrcnc/maven-main-runner
* https://github.com/moditect/layrry