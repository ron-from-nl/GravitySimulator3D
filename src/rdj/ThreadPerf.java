package rdj;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Calendar;

public class ThreadPerf // This object being called by API does all the performance timing and calculations and returns ThreadPerformanceData Object)
{
    private ThreadMXBean bean;
    private ThreadPerformanceData data;

    public ThreadPerf()
    {
        bean = ManagementFactory.getThreadMXBean();
//        bean.setThreadContentionMonitoringEnabled(true);
//        bean.setThreadCpuTimeEnabled(true);
        
        if ((! bean.isCurrentThreadCpuTimeSupported()) || (! bean.isObjectMonitorUsageSupported())) { System.out.println("bean failed to load"); }

        data = new ThreadPerformanceData();
    }

    // Just a wrapper method (pressing twice on the same start/stop button of a stopwatch)
//    public void startWatch(String message) { this.message = message; System.out.println("\nStartWatch: " + message); stopWatch(); }

    public void clock(String message)
    {
        long id = java.lang.Thread.currentThread().getId();
        
        if ( (bean instanceof java.lang.management.ThreadMXBean) )
        {
            data.setCurrCalendar(Calendar.getInstance());
            data.setMilliesPassed((data.getEndedCalendar().getTimeInMillis() - data.getStartedInternalCalendar().getTimeInMillis()));

            data.setCurrCPUUSRMilliesUsed(bean.getThreadUserTime(id)/1000000);
            data.setCurrCPUTOTMilliesUsed(bean.getThreadCpuTime(id)/1000000);

            data.setDiffCPUUSRMilliesUsed((data.getCurrCPUUSRMilliesUsed() - data.getLastCPUUSRMilliesUsed()));
            data.setDiffCPUTOTMilliesUsed((data.getCurrCPUTOTMilliesUsed() - data.getLastCPUTOTMilliesUsed()));

            data.setUsrPercentage((int)Math.round((data.getDiffCPUUSRMilliesUsed() / (data.getMilliesPassed()*0.01))));
            data.setTotPercentage((int)Math.round((data.getDiffCPUTOTMilliesUsed() / (data.getMilliesPassed()*0.01))));
            
            data.setLastCPUUSRMilliesUsed(data.getCurrCPUUSRMilliesUsed());
            data.setLastCPUTOTMilliesUsed(data.getCurrCPUTOTMilliesUsed());
            
            data.setStartedCalendar(data.getStartedInternalCalendar());
            data.setLastInternalCalendar(data.getEndedCalendar());
        }
        else
        {
            data.setUsrPercentage(50);
            data.setTotPercentage(50);
        }
	System.out.println(getPerformance(message));
    }
    
    public ThreadInfo getThreadInfo() // Doesn't provide a whole lot interesting threadinfo (at least not on runnables)
    {
        long id = java.lang.Thread.currentThread( ).getId( );
        return bean.getThreadInfo(id);
    }

    public String getPerformance(String message)
    {
        String  output =  "started:  " +         getHumanDate(getData().getStartedCalendar()) + " " + message + "\n";
                output += "cpu-usr:  " +          getHumanDate(getData().getCPUUSREndedCalendar()) + " [" + String.format("%03d", getData().getCPUUSRPercentage()) + "%] " + getCalendarDiff2Seconds(getData().getStartedCalendar(), getData().getCPUUSREndedCalendar()) + "\n";
                output += "cpu-tot:  " +          getHumanDate(getData().getCPUTOTEndedCalendar()) + " [" + String.format("%03d", getData().getCPUTOTPercentage()) + "%] " + getCalendarDiff2Seconds(getData().getStartedCalendar(), getData().getCPUTOTEndedCalendar()) + "\n";
                output += "finished: " +          getHumanDate(getData().getEndedCalendar()) + " [100%] " +                                                                  getCalendarDiff2Seconds(getData().getStartedCalendar(), getData().getEndedCalendar()) + "\n";
        return  output;
    }
    
    public ThreadPerformanceData getData() { return data; }    
    
    public String getHumanDate(Calendar cal)
    {
	String dateString = "";

	dateString = "" +
	String.format("%04d", cal.get(Calendar.YEAR)) + "-" +
	String.format("%02d", cal.get(Calendar.MONTH) + 1) + "-" +
	String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + " " +
	String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" +
	String.format("%02d", cal.get(Calendar.MINUTE)) + ":" +
	String.format("%02d", cal.get(Calendar.SECOND)) + "," +
	String.format("%03d", cal.get(Calendar.MILLISECOND));

	return dateString;
    }

    public String   getCalendarDiff2Seconds(Calendar oldCal, Calendar newCal)
    {
	long milliSecondsTotal =        ( newCal.getTimeInMillis() - oldCal.getTimeInMillis() );
	long secondsRemainder =         ( milliSecondsTotal / 1000);
	long milliSecondsRemainder =    ( milliSecondsTotal - (secondsRemainder*1000) );
	String response = "(" + secondsRemainder + " sec(s), " + String.format("%03d", milliSecondsRemainder) + " msec(s))";
	return response;
    }

//========================================================================================================    
    
    public class ThreadPerformanceData // This Data object contains the performance stats
    {
        private Calendar    startedCalendar;
        private Calendar    endedCalendar;
        private Calendar    startedInternalCalendar;

        private long        milliesPassed;

        private long        currCPUUSRMilliesUsed;
        private long        lastCPUUSRMilliesUsed;
        private long        currCPUTOTMilliesUsed;
        private long        lastCPUTOTMilliesUsed;
        private long        diffCPUUSRMilliesUsed;
        private long        diffCPUTOTMilliesUsed;

        private long        cpuUsrPercent;
        private long        cpuTotPercent;

        public ThreadPerformanceData()
        {
            endedCalendar = Calendar.getInstance();
            startedInternalCalendar = Calendar.getInstance();
            startedCalendar = Calendar.getInstance();

            milliesPassed = 0;

            currCPUUSRMilliesUsed = 0;
            lastCPUUSRMilliesUsed = 0;
            currCPUTOTMilliesUsed = 0;
            lastCPUTOTMilliesUsed = 0;
            diffCPUUSRMilliesUsed = 0;
            diffCPUTOTMilliesUsed = 0;

            cpuUsrPercent = 0;
            cpuTotPercent = 0;        
        }

        public Calendar getEndedCalendar()                                              { return endedCalendar; }
        public Calendar getStartedInternalCalendar()                                    { return startedInternalCalendar; }
        public Calendar getStartedCalendar()                                            { return startedCalendar; }

        public long     getMilliesPassed()                                              { return milliesPassed; }

        public long     getCurrCPUUSRMilliesUsed()                                      { return currCPUUSRMilliesUsed; }
        public long     getLastCPUUSRMilliesUsed()                                      { return lastCPUUSRMilliesUsed; }    
        public long     getCurrCPUTOTMilliesUsed()                                      { return currCPUTOTMilliesUsed; }
        public long     getLastCPUTOTMilliesUsed()                                      { return lastCPUTOTMilliesUsed; }    
        public long     getDiffCPUUSRMilliesUsed()                                      { return diffCPUUSRMilliesUsed; }
        public long     getDiffCPUTOTMilliesUsed()                                      { return diffCPUTOTMilliesUsed; }

        public long     getCPUUSRPercentage()                                           { return cpuUsrPercent; }
        public long     getCPUTOTPercentage()                                           { return cpuTotPercent; }

        public Calendar getCPUUSREndedCalendar()                                        { Calendar cal = Calendar.getInstance(); cal.setTimeInMillis(startedCalendar.getTimeInMillis() + diffCPUUSRMilliesUsed); return cal; }
        public Calendar getCPUTOTEndedCalendar()                                        { Calendar cal = Calendar.getInstance(); cal.setTimeInMillis(startedCalendar.getTimeInMillis() + diffCPUTOTMilliesUsed); return cal; }

        public void     setCurrCalendar(Calendar currCalendarParam)                     { endedCalendar =           currCalendarParam; }
        public void     setLastInternalCalendar(Calendar lastInternalCalendarParam)     { startedInternalCalendar = lastInternalCalendarParam; }
        public void     setStartedCalendar(Calendar lastCalendarParam)                  { startedCalendar =         lastCalendarParam; }

        public void     setMilliesPassed(long milliesPassedParam)                       { milliesPassed =           milliesPassedParam; }

        public void     setCurrCPUUSRMilliesUsed(long param)				{ currCPUUSRMilliesUsed =   param; }
        public void     setLastCPUUSRMilliesUsed(long param)				{ lastCPUUSRMilliesUsed =   param; }
        public void     setCurrCPUTOTMilliesUsed(long param)				{ currCPUTOTMilliesUsed =   param; }
        public void     setLastCPUTOTMilliesUsed(long param)				{ lastCPUTOTMilliesUsed =   param; }
        public void     setDiffCPUUSRMilliesUsed(long param)				{ diffCPUUSRMilliesUsed =   param; }
        public void     setDiffCPUTOTMilliesUsed(long param)				{ diffCPUTOTMilliesUsed =   param; }

        public void     setUsrPercentage(long cpuUserPercentParam)                      { cpuUsrPercent =           cpuUserPercentParam; }
        public void     setTotPercentage(long cpuTotPercentParam)                       { cpuTotPercent =           cpuTotPercentParam; }
    }
}
