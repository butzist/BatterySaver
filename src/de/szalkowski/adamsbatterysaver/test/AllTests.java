package de.szalkowski.adamsbatterysaver.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.szalkowski.adamsbatterysaver.devices.test.*;
import de.szalkowski.adamsbatterysaver.service.test.*;

@RunWith(Suite.class)
@SuiteClasses({ TestPowerSaver.class, TestMainService.class })
public class AllTests {

}
