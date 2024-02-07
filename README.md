# mvn-multibuild-issue
For background details of the issue this is reproducing see: https://lists.apache.org/thread/798voonf4shyxssvq4bt3lg3bx78vh9z

# Project structure
A monobuild has been created with the following structure:
* app (war)
  * Has a compile dependency on module-a
  * Has a test dependency on testsupport-module-1
* module-a
  * has a runtime dependency on module-b
* module-b
  * has a test dependency on testsupport-module-2
  * has a test with a 30 second sleep
* testsupport-module-1
  * has a test with a 10 second sleep
* testsupport-module-2
  * 'leaf' dependency with no content
  
Note the project is using the takari Maven extension.

# Test case
To reproduce the issue do the following you must run with Maven 3.9.x:
1. Run the following Maven build on the monobuild POM: mvn clean install
2. Run the following Maven build on the monobuild POM: mvn clean verify --builder smart -T2 -pl app,module-b,testsupport-module-1,testsupport-module-2

This should result in a multi-module build that builds all modules EXCEPT for Module 'module-a' (the link between "app" and "module-b").
Because the Maven reactor only considers DIRECT dependencies when building its graph it will determine that it has two independent build 'graphs' it can build in parallel:
* "testsupport-module-1" followed by "app"
* "testsupport-module-2" followed by "module-b"

There are tests with sleeps in them to ensure the following build order will occur in the reactor:
1. "testsupport-module-1" and "testsupport-module-2" start building
2. "testsupport-module-2" completes ("testsupport-module-1" is running its test with a 10 second sleep)
3. "module-b" starts building
4. "testsupport-module-1" completes ("module-b" is running its test with a 30 second sleep)
5. "app" starts and completes build
6. "module-b" completes build

# Expected outcome
The app build has resulted in a war file being packaged that contains a WEB-INF/lib with 'functioning' module-a and module-b jars

# Actual outcome
The app build has resulted in a war file being packaged that contains a WEB-INF/lib with a 'functioning' module-a jar but an empty (0 byte) module-b jar

This unexpected outcome appears to be due to the fact that the "app" build starts before the "module-b" build completes and resolves "module-b" from the reactor rather than the mvn cache.