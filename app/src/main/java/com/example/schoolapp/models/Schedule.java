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

    public static boolean isTimeRangeValid(String start, String end) {
        return isValidTimeFormat(start) && isValidTimeFormat(end) &&
                getMinutes(start) < getMinutes(end);
    }

    public static boolean isValidTimeFormat(String time) {
        if (time == null || !time.matches("^\\d{2}:\\d{2}$")) return false;

        try {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getMinutes(String time) {
        if (!isValidTimeFormat(time))
            throw new IllegalArgumentException("Invalid time format");

        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public static boolean checkConflict(ScheduleSubject curr, ScheduleSubject newSchedule) {
        if (!curr.getDay().equalsIgnoreCase(newSchedule.getDay())) return false;

        int start1 = getMinutes(curr.getStartTime());
        int end1 = getMinutes(curr.getEndTime());
        int start2 = getMinutes(newSchedule.getStartTime());
        int end2 = getMinutes(newSchedule.getEndTime());

        return start2 < end1 && end2 > start1 && curr.getSemester().equalsIgnoreCase(newSchedule.getSemester());
    }


    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + schedule_id +
                '}';
    }
}
