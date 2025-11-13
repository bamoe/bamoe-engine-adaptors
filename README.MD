# IBM Business Automation Manager Open Editions Assets: Embedded Engine Adaptors
This repository includes a set of Java engine adaptors for implementing IBM BAMOE in `Embedded Mode`.  

## How To Build 
This repository is built using `mvn clean install (deploy)` by either the CI/CD pipeline or on a local developer workstation.  Maven `install` places the packaged JAR file into the local Maven `~/.M2/repository`.  Maven `deploy` places the packaged JAR file into the enterprise Maven repository.  This repository relies on application dependencies to be installed into the enterprise Maven repository first.  

## Additional Information (*Appendicies*)
This repository is focused on business automation using [**IBM Business Automation Manager Open Editions**](https://www.ibm.com/docs/en/ibamoe/9.2.x) products, specifically the IBM build of [**Kogito**](https://kogito.kie.org/) known as **IBM Decision Manager Open Edition (DMOE)** and **IBM Process Automation Manager Open Edition (PAMOE)**, leveraging [**Quarkus**](https://quarkus.io/) or [**Spring Boot** _(currently for Decisions only)_](https://spring.io/) as the assoicated container runtime.  The following online documentation is available in order to learn various aspects of these products and frameworks:

- [**Apache Maven**](https://maven.apache.org/) is a free and open source software project management and comprehension tool. Based on  the concept of a project object model (POM), Maven can manage a project’s build, reporting and documentation from a central piece of  information. A **getting started guide** is available [here](http://maven.apache.org/guides/getting-started/).

- [**Git**](https://git-scm.com//) is a free and open source distributed version control system designed to handle everything from small to very large projects with speed and efficiency. There is a **handbook** available [here](https://guides.github.com/introduction/git-handbook/), as well as various **guides** for learning and working with Git available [here](https://guides.github.com/)