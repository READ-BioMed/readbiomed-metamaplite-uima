# readbiomed-metamaplite-uima

Documentation under development.

Wrap MetaMap Lite so it runs as a UIMA analysis engine. It allows as well to be used with Python using the interface defined in [readbiomed-uima-python-interface](https://github.com/READ-BioMed/readbiomed-uima-python-interface)


## Installation

This package has been tested with Java 11 and Maven 3.6.3 and Python 3.9

Download and install [MetaMap Lite](https://lhncbc.nlm.nih.gov/ii/tools/MetaMap/run-locally/MetaMapLite.html).

From the MetaMap Lite folder run the `install-maven-artifacts` (.bat or .sh, depending on your system) script.
This step makes the libraries needed by MetaMap Lite available through Maven.

Once the libraries are installed, run the script below to install the MetaMap Lite library so it is available to Maven.
Avoid the standalone jar file provided with MetaMap Lite since it points to a version of UIMA incompatible with this tool.

```
mvn install:install-file  -Dfile=target/metamaplite-3.6.2rc6.jar ^
                          -DgroupId=gov.nih.nlm.nls ^
                          -DartifactId=metamaplite ^
                          -Dversion=3.6.2rc7 ^
                          -Dpackaging=jar
```

Clone and install [readbiomed-uima-python-interface](https://github.com/READ-BioMed/readbiomed-uima-python-interface)

To install readbiomed-metamaplite-uima, first clone it.

Run `mvn install` from the readbiomed-metamaplite-uima file. When running or setting it up, use the CLASSPATH configured in the step above.