JBehave test system for FitNesse
================================

[![maven central](https://maven-badges.herokuapp.com/maven-central/org.fitnesse.jbehave/fitnesse-jbehave/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.fitnesse.jbehave/fitnesse-jbehave)

This project is a nice demonstration of the state of FitNesse and it's modularity. With fairly little code
it's possible to define and run your JBehave tests from FitNesse.

Which is rather cool, when you think about it :)


Note that no sub-processes are spawn during the execution of the JBehave tests. This is different from how SliM and Fit work.

Features
--------

 - Execute JBehave BDD tests via FitNesse.
 - Easily manage your BDD test cases in FitNesse.
 - The JBehave test system uses a special wiki symbol (`!steps`) that can be used to define step classes.
 - Story files can be accessed directly from within FitNesse. This way you can make use of the reporting facilities of FitNesse.

Using the plugin
----------------

You'll need FitNesse version [20150814] or newer.

Binaries (jar files) are available from the [Maven Central] repository.

The most convenient way to use this plugin is to create a build script and add `fitnesse-jbehave` as a dependency. Have a look at the [Maven Central] page.

Alternatively you can download the standalone jar and add it to a `plugins` folder next to your `FitNesseRoot`.

The plugin will register itself with FitNesse automatically.

[20150814]: http://fitnesse.org/.FrontPage.FitNesseDevelopment.FitNesseRelease20150814
[Maven Central]: https://maven-badges.herokuapp.com/maven-central/org.fitnesse.jbehave/fitnesse-jbehave