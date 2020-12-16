package org.smartregister.pathzeir.processor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.smartregister.reporting.domain.CompositeIndicatorTally;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.exception.MultiResultProcessorException;
import org.smartregister.pathzeir.reporting.common.ReportIndicatorsProcessor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
public class TripleResultProcessorTest {

    private ReportIndicatorsProcessor tripleResultProcessor;

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Before
    public void setUp() {
        tripleResultProcessor = new ReportIndicatorsProcessor();
    }

    @Test
    public void canProcessShouldReturnFalse() {
        assertFalse(tripleResultProcessor.canProcess(2, new String[]{"gender", "count"}));
    }

    @Test
    public void canProcessShouldReturnTrue() {
        Assert.assertTrue(tripleResultProcessor.canProcess(3,  new String[]{"gender", "vaccine", "count"}));
    }

    @Test
    public void processMultiResultTallyShouldReturnSingleIndicatorTallies() throws MultiResultProcessorException {
        String indicatorCode = "CH_IM_UNDER2";
        CompositeIndicatorTally compositeIndicatorTally = new CompositeIndicatorTally(3L, "[[\"gender\",\"vaccine\",\"count(*)\"],[\"male\",\"BCG\",12],[\"female\",\"BCG\",9],[\"female\",\"OPV\",4]]"
                , indicatorCode, new Date());
        ArrayList<String> expectedIndicators = new ArrayList<>();
        expectedIndicators.add(indicatorCode + "_female_BCG");
        expectedIndicators.add(indicatorCode + "_male_BCG");
        expectedIndicators.add(indicatorCode + "_female_OPV");
        expectedIndicators.add(indicatorCode + "_male_OPV");
        compositeIndicatorTally.setExpectedIndicators(expectedIndicators);

        List<IndicatorTally> indicatorTallies = tripleResultProcessor.processMultiResultTally(compositeIndicatorTally);
        assertEquals(3, indicatorTallies.size());
        assertFalse(indicatorTallies.get(0) instanceof CompositeIndicatorTally);
        assertEquals(12, indicatorTallies.get(0).getCount());
        assertEquals(9, indicatorTallies.get(1).getCount());
        assertEquals(4, indicatorTallies.get(2).getCount());

        assertEquals(indicatorCode + "_female_BCG", indicatorTallies.get(1).getIndicatorCode());
    }

    @Test
    public void processMultiResultTallyShouldThrowException() throws MultiResultProcessorException {
        thrownException.expect(MultiResultProcessorException.class);
        thrownException.expectMessage(" could not be processed at value ");

        String indicatorCode = "CH_IM_UNDER2";
        CompositeIndicatorTally compositeIndicatorTally = new CompositeIndicatorTally(3L, "[[\"gender\",\"vaccine\",\"count(*)\"],[\"male\",\"BCG\",\"12\"],[\"female\",\"BCG\",\"9\"],[\"female\",\"OPV\",\"4\"]]", indicatorCode, new Date());

        tripleResultProcessor.processMultiResultTally(compositeIndicatorTally);
    }
}