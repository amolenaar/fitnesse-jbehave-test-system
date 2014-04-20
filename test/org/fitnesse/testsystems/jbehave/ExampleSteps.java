package org.fitnesse.testsystems.jbehave;

import org.jbehave.core.annotations.*;
import org.jbehave.core.steps.Steps;

public class ExampleSteps extends Steps {
    int x;

    @Given("a variable x with value $value")
    @Alias("a variable x with value <value>") // examples table
    public void givenXValue(@Named("value") int value) {
        x = value;
    }

    @When("I multiply x by $value")
    @Alias("I multiply x by <value>") // examples table
    public void whenImultiplyXBy(@Named("value") int value) {
        x = x * value;
    }

    @Then("x should equal $outcome")
    @Alias("x should equal <outcome>") // examples table
    public void thenXshouldBe(@Named("outcome") int value) {
        if (value != x)
            throw new AssertionError("x is " + x + ", but should be " + value);
    }

    @Then("pending method")
    public void iAmPending() {
    }

}