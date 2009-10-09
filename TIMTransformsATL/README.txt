Scenario-based Model-driven Configuration Management Proof-of-Concept
based on MDA technologies
=====================================================================

Fermín Galán Márquez (galan at dit.upm.es)
October 2009

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

* input, this directory contains the input models (TIM and Testbed Parameters)
  used in the transformations, encoded in XMI for ECore. In particular:
  
  - basic.ecore models the basic scenario
  - nsfnet.ecore and nsfnet-ospf.ecore model the nsfnet scenario
  - rediris.ecore and rediris-ospf.ecore model the rediris scenario
  - adnet-settings.ecore are the Testbed Parameters to use for ADNETCONF in
    all the scenarios
  - vnuml-ospf-settings.ecore and vnuml-no_ospf-settings.ecore are the Testbed
    Parameters to use for VNUML in scenarios with (nsfnet, rediris) and 
    without (basic) OSPF processes/routes 

* metamodels, this directory includes all the required metamodels to run the
  experimental validation, encoded in XMI for ECore. In particular:
  
  - Tim.ecore is the metamodel for the TIM Core
  - TimOspf.ecore is the metamodel for the TIM OSPF Module
  - VNUML.ecore and VNUMLOspf.ecore define the VNUML TSM metamodel
  - ADNETCONF.ecore and ADNETCONFOspf.ecore define the ADNETCONF TSM metamodel
  - XML.ecore defines the XML metamodel

* transformations, this directory include the ATL defining both the TIM-to-TSM
  and TSM-to-XML transformation. It is composed by the following subdirectories:

  - Helpers, which stores helpers used by the actual transformations 
  - M2MTransforms, which stores the transformations themselves

Prerequirements
---------------

You need Eclipse with the ATL and AM3 to run the experimental
validation. In particular, as reference we have used 
Eclipse 3.3.2 (run on a JVM 1.6.0) with the ATL 2.0.0 and AM3 0.2.0.
The operating system was Ubuntu 9.04.

Given that set up a working installation of ATL and AM3 plugins can
be tricky (at least for us :), we have prepared an Eclipse bunch
containing all the required pieces that should work in any GNU/Linux
distribution:

http://www.dit.upm.es/galan/TIMTransforms/eclipse-SDK-3.3.2-linux-gtk+EMF+SVN+ATL+AM3.tar.gz (168MB)

We highly encourage you to use that one.

Installation
------------

* Download and untar the package (although given that you are 
  reading this README.txt, I guess you have already done it :)
  
  # cd /usr/local/src
  # wget http://www.dit.upm.es/galan/TIMTransformsMDA/TIMTransformsMDA-v2009_10_15.tar.gz
  # tar xfvz TIMTransforms-v2009_10_15.tar.gz

* Create a New Project (ATL Project) in your Eclipse workspace, import the code under 
  /usr/local/src/TIMTransformsMDA into your Eclipse workspace into that project. 
   
* Double click in the build.xml, choose "Run As -> Ant Build...". Then in the "JRE"
  tag, check the following option:  

  "Run in the same JRE as the workspace"
  
  Then "Apply" and "Close"
  
Running the test
----------------

Double click in the build.xml, choose "Run As -> Ant Build". 
After a while, the process ends.

(By default all the transformations are execute, but you can 
edit the target in build.xml to procude just one).

If the scenarios have been transformed and all is ok 
you will get at the end ten files in /tmp:

* adnet-basic.xml
* adnet-nsfnet.xml
* adnet-nsfnet-ospf.xml
* adnet-rediris.xml
* adnet-rediris-ospf.xml
* vnuml-basic.xml
* vnuml-nsfnet.xml
* vnuml-nsfnet-ospf.xml
* vnuml-rediris.xml
* vnuml-rediris-ospf.xml

In can be checked that vnuml-*.xml can be processed by VNUML
tool and adnet-*.xml by ADNETCONF. For example you can test:

# vnumlparser.pl -t /tmp/vnuml-basic.xml -v -Z

(The installation of VNUML however it is out of the scope of
this document; see http://www.dit.upm.es/vnuml if you are
interested)