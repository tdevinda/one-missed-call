package lk.tharaka.mca.test;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

import lk.tharaka.mca.MissedCall;
import lk.tharaka.mca.scrapers.DialogMessageScraper;
import lk.tharaka.mca.scrapers.MessageScraper;

/**
 * Created by Tharu on 2016-11-14.
 */

public class DialogScraperTest {


    @Test
    public void scraperTest() {
        String sms = "2 Missed calls from 773336050 on 14-11-16 *this is a test ad*";

        MessageScraper dialogScraper = new DialogMessageScraper();
        ArrayList<MissedCall> missedCallsFromSMS = dialogScraper.getMissedCallsFromSMS(sms);
        for (MissedCall call : missedCallsFromSMS) {

            System.out.println(call.toString());

            Assert.assertEquals(call.count, 2);

        }
    }

}
