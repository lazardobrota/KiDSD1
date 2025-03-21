package domain.other;

import domain.utils.ProgramUtils;

public class Job {

    private final String name;
    private final String wholeCommand;
    private EJob jobStatus;

    public Job(String name, String wholeCommand, EJob jobStatus) {
        this.name = name;
        this.wholeCommand = wholeCommand;
        this.jobStatus = jobStatus;
    }

    public String getName() {
        return name;
    }

    public String getWholeCommand() {
        return wholeCommand;
    }

    public EJob getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(EJob jobStatus) {
        if (ProgramUtils.running.get())
            this.jobStatus = jobStatus;
    }
}
