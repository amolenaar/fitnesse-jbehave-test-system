JBehave test system for FitNesse
================================
(a.k.a. Felicity -- Oh Behave!)

This project is a nice demonstration of the state of FitNesse and it's modularity. With fairly little code
it's possible to define and run your JBehave tests from FitNesse.

Which is rather cool, when you think about it :)


Note that no sub-processes are spawn during the execution of the JBehave tests. This is different from how SliM and Fit work.

Features:
 - Execute JBehave BDD tests via FitNesse.
 - Easily manage your BDD test cases in FitNesse.
 - The JBehave test system uses a special wiki symbol (`!steps`) that can be used to define step classes.
 - Story files can be accessed directly from within FitNesse. This way you can make use of the reporting facilities of FitNesse.
