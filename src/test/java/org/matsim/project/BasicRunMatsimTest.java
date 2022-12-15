package org.matsim.project;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.testcases.MatsimTestCase;
import org.matsim.testcases.MatsimTestUtils;

import static org.junit.Assert.*;

public class BasicRunMatsimTest {
    @Rule
    public MatsimTestUtils utils = new MatsimTestUtils();
    @Test
public void testTheCode(){
  //  Assert.assertEquals(3,2+1);
//
//    System.out.println("1 "+ utils.getOutputDirectory());
//    System.out.println("2 "+ utils.getInputDirectory());
        Config config = ConfigUtils.createConfig() ;
        config.network().setInputFile(utils.getClassInputDirectory() + "/network.xml.gz");
        config.plans().setInputFile(utils.getInputDirectory()+ "/plans.xml.gz");
        config.controler().setOutputDirectory( utils.getOutputDirectory() );
        config.controler().setLastIteration(0);
        var scenario= ScenarioUtils.loadScenario(config);
        Controler controller=new Controler(scenario);
        controller.run();
}
}