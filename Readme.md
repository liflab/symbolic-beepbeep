Conversion of BeepBeep pipelines into NuSMV models
==================================================

This project is an extension to the [BeepBeep
3](https://liflab.github.io/beepbeep-3), event stream processing engine,
called a *palette*, that provides functionalities to produce models for the
[NuSMV](https://nusmv.fbk.eu/) model checker and verify CTL and LTL properties
on those models.

What is this?
-------------

The palette is the result of work submitted in the following research paper:

- A. Bédard, S. Hallé. (2023). *Formal Verification for Event Stream
  Processing: Model Checking of BeepBeep Stream Processing Pipelines*.
  Submitted to *Information and Computation*.

This work extends the original modeling presented in this paper:

- A. Bédard, S. Hallé. (2020). *Model checking of stream processing pipelines.*
  In: C. Combi, J. Eder, M. Reynolds (Eds.), 28th International Symposium on
  Temporal Representation and Reasoning, TIME 2021, September 27-29,
  2021, Klagenfurt, Austria, Vol. 206 of LIPIcs, Schloss Dagstuhl - Leibniz-
  Zentrum für Informatik, 2021, pp. 5:1–5:17. DOI: `10.4230/LIPIcs.TIME.2021.5`.

Please refer to these research papers for detailed information and examples of
what this extension can do.

Building this palette
---------------------

To compile the palette, make sure you have the following:

- The Java Development Kit (JDK) to compile. The palette complies
  with Java version 11; it is probably safe to use any later version.
- [Ant](http://ant.apache.org) to automate the compilation and build process

The palette also requires the following Java libraries:

- The latest version of [BeepBeep 3](https://liflab.github.io/beepbeep-3)
- The latest version of [nusmv4j](https://github.com/liflab/nusmv4j)

These dependencies can be automatically downloaded and placed in the
`dep` folder of the project by typing:

    ant download-deps

### Compiling

Compile the sources by simply typing:

    ant

This will produce a file called `nusmv4j.jar` in the project's root folder.

### Testing
                                             
If the project includes unit tests, they can be run by typing:

    ant test

Unit tests are run with [jUnit](http://junit.org); a detailed report of
these tests in HTML format is availble in the folder `tests/junit`, which
is automatically created. Code coverage is also computed with
[JaCoCo](http://www.eclemma.org/jacoco/); a detailed report is available
in the folder `tests/coverage`.

### Documentation

The Javadoc documentation of the project can be created by typing:

    ant javadoc

The documentation will be placed in the `doc` folder at the root of the
project. Please refer to this documentation to learn how to use the extension.

About the author                                                   {#about}
----------------

This palette was written by [Sylvain Hallé](https://leduotang.ca/sylvain),
Full Professor at [Université du Québec à
Chicoutimi](https://www.uqac.ca/), Canada. It is based on original work done by
[Alexis Bédard](https://github.com/alexisBedard) during his Master's project at
UQAC.
