Scenario-based Model-driven Configuration Management Proof-of-Concept
=====================================================================

Fermín Galán Márquez (galan at dit.upm.es)
February 2009

Here it is a software package that can be used to check the 
feasibility of scenario-based model-driven configuration management 
for experimentation infrastructures (i.e., testbeds) based on 
TIM-to-TSM transformations.

Note that here we provide a software proof-of-concept, but not the 
details of the scenario-based model-driven configuration management 
approach. Such details (e.g., architecture, TIM-to-TSM transformations 
principles, the Testbed Independent Model semantics, information 
regarding VNUML-based and ADRENALINE testbeds, etc.) are provided 
elsewhere (although if you are reading this you maybe have come from 
some of those sources and you are already aware of the details :)

Contact
-------

Fermín Galán Márquez (galan at dit.upm.es)

Don't hesitate to contact me if you have problems using the software 
or any other question. In addition, feedback (regarding the software 
implementation or the ideas behind it) is highly appreciated!

License
-------

All the materials within the package can be used under the terms of 
GPL and Creative Commons Attribution-Share Alike 2.5 Spain licenses for 
software code and models/documentation, respectively.

Contents
--------

Within mof/ (DMTF's MOF specification)

* tim.mof:
    The TIM (Testbed Independent Model) specification
    
* basic.mof:
    The basic scenario, conforming to TIM
    
* nsfnet.mof:
    The NSFNet scenario, conforming to TIM

* rediris.mof:
    The RedIRis scenario, conforming to TIM

* testbed_parameters.mof:
    Extension of CIM_SettingData to model testbed parameters
    for VNUML and ADRENALINE transformation (used when 
    initializeFromCIMOM=true in the code).

* testbed_parameters-defaults.mof:
    Example use of the aforementioned extension to model two
    sets (for both VNUML and ADRENALINE transformations) of
    actual testbed parameters.

Within src/ (Source code)

* The Java source code implementing the TIM-to-TSM transformations 
  for VNUML-based testbed and the ADRENALINE testbed.

Within doc/ (documentation)

* Javadoc for the source in src/

Within scripts (supporting scripts)

* expand_alias.pl:
    This script performs alias substitution (e.g., $TESTBED) for actual
    object paths. This is intended to overcome limitations on some MOF
    compilers (in particular, the mofcomp that comes with WBEMServices),
    that seem not being able to resolve the alias themselves.

* reset_cimom.sh:
    Helper to automate the setting up of the proof-of-concept,
    reseting CIMOM repository and compiling into it all the
    required stuff into CIMOM (CIM Schema, TIM, the three scenarios
    and the testbed parameters).

Observations
------------

We assume you have root privilegies to execute the following actions.
Otherwise, there could be permissions problems.

Prerequirements
---------------

In order to install and run this proof-of-concept you need a CIMOM
and a Java SDK

* Java SDK (http://java.sun.com). No particular requirements has been 
  identified, so you could use (in theory) any version. As reference, 
  we have testbed with JDK 1.6.0_06.

* Although you can use in theory any standards compliant CIMOM, we 
  highly encourage to use the one which comes with WBEMServices
  (http://wbemservices.sourceforge.net), which installation 
  (version 1.0.2) is described following:

  The JAVA_HOME environment variable have to be properly configured
  or WBEMServices CIMOM starting/stopping scripts won't work. 
  Assuming that your Java SDK is intalled in /usr/local/jdk1.6.0_06/,
  you can just set "export JAVA_HOME=/usr/local/jdk1.6.0_06/" at the beginning
  at the beginning of ~/.bashrc to do so.

  # cd /usr/local/src
  # wget http://kent.dl.sourceforge.net/sourceforge/wbemservices/wbemservices-1.0.2.bin.zip
  # unzip wbemservices-1.0.2.bin.zip
  # vi wbemservices/bin/mofcomp
  (change the JAVA_HOME in the script preamble to the proper value in your
  environment, for example "JAVA_HOME=/usr/local/jdk1.6.0_06/")

  You could use any other valid SourceForge.net mirror in the second step.

Installation
------------

* Download and untar the package (although given that you are 
  reading this README.txt, I guess you have already done it :)
  
  # cd /usr/local/src
  # wget http://jungla.dit.upm.es/~galan/TIMTransforms/TIMTransforms-v2009_02_21.tar.gz
  # tar xfvz TIMTransforms-v2009_02_21.tar.gz

* Prepare the CIMOM repository. The best way to do this is maybe
  using the reset_cimom.sh script, after editing some of the 
  parameters at the beginning of the script.
   
  # vi /usr/local/src/TIMTransforms-v2009_02_21/scripts/reset_cimom.sh
  (edit the parameters at the beginning of the script, for example:
     PROJECT=/usr/local/src/TIMTransforms-v2009_02_21
     HOST=localhost
     NS=root/cimv2181
  )
  # cd /usr/local/src/TIMTransforms-v2009_02_21/scripts
  # ./reset_cimom.sh

* Check the CIMOM repository (Optional). The reset_cimom.sh script should start the
  CIMOM properly. However, you can check it using a CIM browser, as the one that comes
  with wbemservices. You can also check that the new classes included in tim.mof has
  been properly compiled there (e.g., TIM_TestbedScenario).
  
  # cd /usr/local/src/wbemservices/bin
  #./cimworkshop.sh

* Compile the source code. You will need the wbem.jar library to 
  do it. In addition, you may need to tune some of the parameters
  within TransTester.java main function, to set properly the
  host and namespace used by your CIMOM.
  
  # cd /usr/local/src/TIMTransforms-v2009_02_21/src
  # javac *.java -classpath ../lib/wbem.jar

Running the test
----------------

Just execute the main program:

# cd /usr/local/src/TIMTransforms-v2009_02_21/src
# java -classpath .:../lib/wbem.jar TransTester

If the scenarios have been properly compiled in the CIMOM and 
all is ok) you will get at the end six files in /tmp:

 * vnuml-basic.xml
 * adnet-basic.xml
 * vnuml-nsfnet.xml
 * adnet-nsfnet.xml
 * vnuml-rediris.xml
 * adnet-rediris.xml

In can be checked that vnuml-*.xml can be processed by VNUML
tool and adnet-*.xml by ADNETCONF. For example you can test:

# vnumlparser.pl -t /tmp/vnuml-basic.xml -v -Z

(The installation of VNUML however it is out of the scope of
this document; see http://www.dit.upm.es/vnuml if you are
interested)