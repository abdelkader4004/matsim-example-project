package org.matsim.project;

import org.junit.Rule;
import org.junit.Test;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.testcases.MatsimTestUtils;



public class RunMatsim2Test {

    @Rule
    public MatsimTestUtils utils =new MatsimTestUtils();


    @Test
    public void testSomeThing(){
 System.out.println("1 "+ utils.getOutputDirectory());
 System.out.println("2 "+ utils.getInputDirectory());
//Assert.assertEquals(10,8+2);

//
        Config config = ConfigUtils.createConfig() ;
        config.network().setInputFile(utils.getClassInputDirectory() + "/network.xml.gz");
        config.plans().setInputFile(utils.getInputDirectory()+ "/plans.xml.gz");
        config.controler().setOutputDirectory( utils.getOutputDirectory() );
//        config.controler().setLastIteration(0);
        var scenario= ScenarioUtils.loadScenario(config);
        Controler controller=new Controler(scenario);
        controller.run();
    }
}