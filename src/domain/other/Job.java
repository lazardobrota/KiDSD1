package domain.other;

import domain.commands.Command;
import domain.utils.ProgramUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Job {

    private static final Map<Job, Command> runningJobs = new ConcurrentHashMap<>();
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
