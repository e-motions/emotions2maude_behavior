package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.transformation.common.MaudeIdentifiersTest;
import test.transformation.rules.smallrules.PatternElOidTest;
import test.transformation.rules.smallrules.PatternNACTest;

@RunWith(Suite.class)
@SuiteClasses({MaudeIdentifiersTest.class, PatternElOidTest.class, PatternNACTest.class})
public class AllTests {

}
