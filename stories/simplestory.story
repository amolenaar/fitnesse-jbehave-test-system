!steps org.fitnesse.jbehave.ExampleSteps

Example symlinked scenarios for test system ${TEST_SYSTEM}

Scenario: 2 squared

Given a variable x with value 2
When I multiply x by 2
Then x should equal 4

Scenario: 3 squared

Given a variable x with value 3
When I multiply x by 3
Then x should equal 9

