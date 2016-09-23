package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.transformation.common.MaudeIdentifiersTest;
import test.transformation.rules.smallrules.PatternElOidTest;

@RunWith(Suite.class)
@SuiteClasses({MaudeIdentifiersTest.class, PatternElOidTest.class})
public class AllTests {

}
