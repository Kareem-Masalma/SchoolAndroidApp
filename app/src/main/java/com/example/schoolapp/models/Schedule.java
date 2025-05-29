package com.example.schoolapp.models;

public class Schedule {
    private Integer schedule_id;

    public Schedule() {

    }

    public Schedule(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    public Integer getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(Integer schedule_id) {
        this.schedule_id = schedule_id;
    }

    public static boolean checkConflict(ScheduleSubject curr, ScheduleSubject newSchedule) {
        String startTime = curr.getStartTime();
        String endTime = curr.getEndTime();

        int startMinutes = getMinutes(startTime);
        int endMinutes = getMinutes(endTime);

        String newStartTime = newSchedule.getStartTime();
        String newEndTime = newSchedule.getEndTime();

        int newStartMinutes = getMinutes(newStartTime);
        int newEndMinutes = getMinutes(newEndTime);

        return ((newStartMinutes < endMinutes) && (newEndMinutes > startMinutes)) && curr.getDay().equalsIgnoreCase(newSchedule.getDay());
    }

    private static int getMinutes(String time) {
        String[] splitTime = time.split(":");

        int hours = Integer.parseInt(splitTime[0]);
        int minutes = Integer.parseInt(splitTime[1]);

        return hours * 60 + minutes;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + schedule_id +
                '}';
    }
}
