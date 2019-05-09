# RunCmd
Prototype for a utility that can securely pass credentials to other programs.

Example invocation (all on a single line):

```
java 
   -Dpwtool.path=C:\SCAInstallDir\bin\pwtool.bat 
   -Dpwtool.keysFile=C:\Temp\test.keys 
   -jar RunCmd
   runJar path\to\jarFile\to\run
   -SomeCredential {{pwtool:{fp0}rn8MDotW9UAi0MSQURzm4rdVTH17NSn94AC0NRiW74k=}}
   -SomeOtherCredential {{pwtool:some other credential encoded with pwtool}}
   -JustTesting {{text:Plain Text}}
```

This doesn't start a separate process to run the given jar file; credentials are passed in-memory to the main
class defined in the given jar file.

The following command would run any arbitrary command, but this hasn't yet been implemented. As a separate
process would be created for running the command, the credentials are potentially visible in the process list:

```
java 
   -Dpwtool.path=C:\SCAInstallDir\bin\pwtool.bat 
   -Dpwtool.keysFile=C:\Temp\test.keys 
   -jar RunCmd
   runCmd path\to\some\arbitrary\command\to\run
   -SomeCredential {{pwtool:{fp0}rn8MDotW9UAi0MSQURzm4rdVTH17NSn94AC0NRiW74k=}}
   -SomeOtherCredential {{pwtool:some other credential encoded with pwtool}}
   -JustTesting {{text:Plain Text}}
```
